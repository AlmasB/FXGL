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

import com.almasb.fxgl.ai.btree.Task;
import com.almasb.fxgl.core.collection.Array;

/** A {@code RandomSequence} is a sequence task's variant that runs its children in a random order.
 * 
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * 
 * @author implicit-invocation */
public class RandomSequence<E> extends Sequence<E> {

	/** Creates a {@code RandomSequence} branch with no children. */
	public RandomSequence () {
		super();
	}

	/** Creates a {@code RandomSequence} branch with the given children.
	 * 
	 * @param tasks the children of this task */
	public RandomSequence (Array<Task<E>> tasks) {
		super(tasks);
	}

	/** Creates a {@code RandomSequence} branch with the given children.
	 * 
	 * @param tasks the children of this task */
	public RandomSequence (Task<E>... tasks) {
		super(new Array<Task<E>>(tasks));
	}

	@Override
	public void start () {
		super.start();
		if (randomChildren == null) randomChildren = createRandomChildren();
	}
}
