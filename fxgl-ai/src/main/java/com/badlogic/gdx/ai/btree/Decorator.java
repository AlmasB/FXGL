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

package com.badlogic.gdx.ai.btree;

import com.badlogic.gdx.ai.btree.annotation.TaskConstraint;

/** A {@code Decorator} is a wrapper that provides custom behavior for its child. The child can be of any kind (branch task, leaf
 * task, or another decorator).
 * 
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * 
 * @author implicit-invocation
 * @author davebaol */
@TaskConstraint(minChildren = 1, maxChildren = 1)
public abstract class Decorator<E> extends Task<E> {

	/** The child task wrapped by this decorator */
	protected Task<E> child;

	/** Creates a decorator with no child task. */
	public Decorator () {
	}

	/** Creates a decorator that wraps the given task.
	 * 
	 * @param child the task that will be wrapped */
	public Decorator (Task<E> child) {
		this.child = child;
	}

	@Override
	protected int addChildToTask (Task<E> child) {
		if (this.child != null) throw new IllegalStateException("A decorator task cannot have more than one child");
		this.child = child;
		return 0;
	}

	@Override
	public int getChildCount () {
		return child == null ? 0 : 1;
	}

	@Override
	public Task<E> getChild (int i) {
		if (i == 0 && child != null) return child;
		throw new IndexOutOfBoundsException("index can't be >= size: " + i + " >= " + getChildCount());
	}

	@Override
	public void run () {
		if (child.status == Status.RUNNING) {
			child.run();
		} else {
			child.setControl(this);
			child.start();
			if (child.checkGuard(this))
				child.run();
			else
				child.fail();
		}
	}

	@Override
	public void childRunning (Task<E> runningTask, Task<E> reporter) {
		running();
	}

	@Override
	public void childFail (Task<E> runningTask) {
		fail();
	}

	@Override
	public void childSuccess (Task<E> runningTask) {
		success();
	}

	@Override
	protected Task<E> copyTo (Task<E> task) {
		if (this.child != null) {
			Decorator<E> decorator = (Decorator<E>)task;
			decorator.child = this.child.cloneTask();
		}

		return task;
	}

}
