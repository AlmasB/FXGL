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

package com.badlogic.gdx.ai.btree.leaf;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;

/** {@code Failure} is a leaf that immediately fails.
 * 
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * 
 * @author davebaol */
public class Failure<E> extends LeafTask<E> {

	/** Creates a {@code Failure} task. */
	public Failure () {
	}

	/** Executes this {@code Failure} task.
	 * @return {@link Status#FAILED}. */
	@Override
	public Status execute () {
		return Status.FAILED;
	}

	@Override
	protected Task<E> copyTo (Task<E> task) {
		return task;
	}

}
