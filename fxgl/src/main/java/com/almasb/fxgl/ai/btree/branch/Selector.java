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

package com.almasb.fxgl.ai.btree.branch;

import com.almasb.fxgl.ai.btree.SingleRunningChildBranch;
import com.almasb.fxgl.ai.btree.Task;
import com.almasb.fxgl.core.collection.Array;

/** A {@code Selector} is a branch task that runs every children until one of them succeeds. If a child task fails, the selector
 * will start and run the next child task.
 * 
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * 
 * @author implicit-invocation */
public class Selector<E> extends SingleRunningChildBranch<E> {

	/** Creates a {@code Selector} branch with no children. */
	public Selector () {
		super();
	}

	/** Creates a {@code Selector} branch with the given children.
	 * 
	 * @param tasks the children of this task */
	public Selector (Task<E>... tasks) {
		super(new Array<Task<E>>(tasks));
	}

	/** Creates a {@code Selector} branch with the given children.
	 * 
	 * @param tasks the children of this task */
	public Selector (Array<Task<E>> tasks) {
		super(tasks);
	}

	@Override
	public void childFail (Task<E> runningTask) {
		super.childFail(runningTask);
		if (++currentChildIndex < children.size()) {
			run(); // Run next child
		} else {
			fail(); // All children processed, return failure status
		}
	}

	@Override
	public void childSuccess (Task<E> runningTask) {
		super.childSuccess(runningTask);
		success(); // Return success status when a child says it succeeded
	}

}
