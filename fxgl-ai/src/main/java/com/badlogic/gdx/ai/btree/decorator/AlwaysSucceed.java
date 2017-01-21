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

/** An {@code AlwaysSucceed} decorator will succeed no matter the wrapped task succeeds or fails.
 * 
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * 
 * @author implicit-invocation */
public class AlwaysSucceed<E> extends Decorator<E> {

	/** Creates an {@code AlwaysSucceed} decorator with no child. */
	public AlwaysSucceed () {
	}

	/** Creates an {@code AlwaysSucceed} decorator with the given child.
	 * 
	 * @param task the child task to wrap */
	public AlwaysSucceed (Task<E> task) {
		super(task);
	}

	@Override
	public void childFail (Task<E> runningTask) {
		childSuccess(runningTask);
	}

}
