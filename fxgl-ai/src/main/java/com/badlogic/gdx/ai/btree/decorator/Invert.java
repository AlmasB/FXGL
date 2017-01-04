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

/** An {@code Invert} decorator will succeed if the wrapped task fails and will fail if the wrapped task succeeds.
 * 
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * 
 * @author implicit-invocation */
public class Invert<E> extends Decorator<E> {

	/** Creates an {@code Invert} decorator with no child. */
	public Invert () {
	}

	/** Creates an {@code Invert} decorator with the given child.
	 * 
	 * @param task the child task to wrap */
	public Invert (Task<E> task) {
		super(task);
	}

	@Override
	public void childSuccess (Task<E> runningTask) {
		super.childFail(runningTask);
	}

	@Override
	public void childFail (Task<E> runningTask) {
		super.childSuccess(runningTask);
	}

}
