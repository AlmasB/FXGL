/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.ai.btree.decorator;

import com.badlogic.gdx.ai.btree.Decorator;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.ai.utils.NonBlockingSemaphore;
import com.badlogic.gdx.ai.utils.NonBlockingSemaphoreRepository;

/** A {@code SemaphoreGuard} decorator allows you to specify how many characters should be allowed to concurrently execute its
 * child which represents a limited resource used in different behavior trees (note that this does not necessarily involve
 * multithreading concurrency).
 * <p>
 * This is a simple mechanism for ensuring that a limited shared resource is not over subscribed. You might have a pool of 5
 * pathfinders, for example, meaning at most 5 characters can be pathfinding at a time. Or you can associate a semaphore to the
 * player character to ensure that at most 3 enemies can simultaneously attack him.
 * <p>
 * This decorator fails when it cannot acquire the semaphore. This allows a selector task higher up the tree to find a different
 * action that doesn't involve the contested resource.
 * 
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * 
 * @author davebaol */
public class SemaphoreGuard<E> extends Decorator<E> {

	/** Mandatory task attribute specifying the semaphore name. */
	@TaskAttribute(required = true) public String name;

	private transient NonBlockingSemaphore semaphore;
	private boolean semaphoreAcquired;

	/** Creates a {@code SemaphoreGuard} decorator with no child. */
	public SemaphoreGuard () {
	}

	/** Creates a {@code SemaphoreGuard} decorator with the given child.
	 * 
	 * @param task the child task to wrap */
	public SemaphoreGuard (Task<E> task) {
		super(task);
	}

	/** Creates a {@code SemaphoreGuard} decorator with no child the specified semaphore name.
	 * 
	 * @param name the semaphore name */
	public SemaphoreGuard (String name) {
		super();
		this.name = name;
	}

	/** Creates a {@code SemaphoreGuard} decorator with the specified semaphore name and child.
	 * 
	 * @param name the semaphore name
	 * @param task the child task to wrap */
	public SemaphoreGuard (String name, Task<E> task) {
		super(task);
		this.name = name;
	}

	/** Acquires the semaphore. Also, the first execution of this method retrieves the semaphore by name and stores it locally.
	 * <p>
	 * This method is called when the task is entered. */
	@Override
	public void start () {
		if (semaphore == null) {
			semaphore = NonBlockingSemaphoreRepository.getSemaphore(name);
		}
		semaphoreAcquired = semaphore.acquire();
		super.start();
	}

	/** Runs its child if the semaphore has been successfully acquired; immediately fails otherwise. */
	@Override
	public void run () {
		if (semaphoreAcquired) {
			super.run();
		} else {
			fail();
		}
	}

	/** Releases the semaphore.
	 * <p>
	 * This method is called when the task exits. */
	@Override
	public void end () {
		if (semaphoreAcquired) {
			if (semaphore == null) {
				semaphore = NonBlockingSemaphoreRepository.getSemaphore(name);
			}
			semaphore.release();
			semaphoreAcquired = false;
		}
		super.end();
	}

	@Override
	public void reset () {
		super.reset();
		semaphore = null;
		semaphoreAcquired = false;
	}

	@Override
	protected Task<E> copyTo (Task<E> task) {
		SemaphoreGuard<E> semaphoreGuard = (SemaphoreGuard<E>)task;
		semaphoreGuard.name = name;
		semaphoreGuard.semaphore = null;
		semaphoreGuard.semaphoreAcquired = false;

		return super.copyTo(task);
	}
}
