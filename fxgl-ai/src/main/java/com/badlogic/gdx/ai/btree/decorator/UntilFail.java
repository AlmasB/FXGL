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

import com.badlogic.gdx.ai.btree.LoopDecorator;
import com.badlogic.gdx.ai.btree.Task;

/** The {@code UntilFail} decorator will repeat the wrapped task until that task fails, which makes the decorator succeed.
 * <p>
 * Notice that a wrapped task that always succeeds without entering the running status will cause an infinite loop in the current
 * frame.
 * 
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * 
 * @author implicit-invocation
 * @author davebaol */
public class UntilFail<E> extends LoopDecorator<E> {

	/** Creates an {@code UntilFail} decorator with no child. */
	public UntilFail () {
	}

	/** Creates an {@code UntilFail} decorator with the given child.
	 * 
	 * @param task the child task to wrap */
	public UntilFail (Task<E> task) {
		super(task);
	}

	@Override
	public void childSuccess (Task<E> runningTask) {
		loop = true;
	}

	@Override
	public void childFail (Task<E> runningTask) {
		success();
		loop = false;
	}
}
