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

package com.almasb.fxgl.ai.btree.decorator;

import com.almasb.fxgl.ai.btree.Decorator;
import com.almasb.fxgl.ai.btree.Task;
import com.almasb.fxgl.ai.btree.annotation.TaskAttribute;
import com.almasb.fxgl.ai.btree.annotation.TaskConstraint;
import com.almasb.fxgl.ai.btree.leaf.Failure;
import com.almasb.fxgl.ai.btree.leaf.Success;
import com.almasb.fxgl.ai.utils.random.ConstantFloatDistribution;
import com.almasb.fxgl.ai.utils.random.FloatDistribution;
import com.almasb.fxgl.core.math.FXGLMath;

/** The {@code Random} decorator succeeds with the specified probability, regardless of whether the wrapped task fails or succeeds.
 * Also, the wrapped task is optional, meaning that this decorator can act like a leaf task.
 * <p>
 * Notice that if success probability is 1 this task is equivalent to the decorator {@link AlwaysSucceed} and the leaf
 * {@link Success}. Similarly if success probability is 0 this task is equivalent to the decorator {@link AlwaysFail} and the leaf
 * {@link Failure}.
 * 
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * 
 * @author davebaol */
@TaskConstraint(minChildren = 0, maxChildren = 1)
public class Random<E> extends Decorator<E> {

	/** Optional task attribute specifying the random distribution that determines the success probability. It defaults to
	 * {@link ConstantFloatDistribution#ZERO_POINT_FIVE}. */
	@TaskAttribute
    public FloatDistribution success;

	private float p;

	/** Creates a {@code Random} decorator with no child that succeeds or fails with equal probability. */
	public Random () {
		this(ConstantFloatDistribution.ZERO_POINT_FIVE);
	}

	/** Creates a {@code Random} decorator with the given child that succeeds or fails with equal probability.
	 * 
	 * @param task the child task to wrap */
	public Random (Task<E> task) {
		this(ConstantFloatDistribution.ZERO_POINT_FIVE, task);
	}

	/** Creates a {@code Random} decorator with no child that succeeds with the specified probability.
	 * 
	 * @param success the random distribution that determines success probability */
	public Random (FloatDistribution success) {
		super();
		this.success = success;
	}

	/** Creates a {@code Random} decorator with the given child that succeeds with the specified probability.
	 * 
	 * @param success the random distribution that determines success probability
	 * @param task the child task to wrap */
	public Random (FloatDistribution success, Task<E> task) {
		super(task);
		this.success = success;
	}

	/** Draws a value from the distribution that determines the success probability.
	 * <p>
	 * This method is called when the task is entered. */
	@Override
	public void start () {
		p = success.nextFloat();
	}

	@Override
	public void run () {
		if (child != null)
			super.run();
		else
			decide();
	}

	@Override
	public void childFail (Task<E> runningTask) {
		decide();
	}

	@Override
	public void childSuccess (Task<E> runningTask) {
		decide();
	}

	private void decide () {
		if (FXGLMath.random() <= p)
			success();
		else
			fail();
	}

	@Override
	protected Task<E> copyTo (Task<E> task) {
		Random<E> random = (Random<E>)task;
		random.success = success; // no need to clone since it is immutable

		return super.copyTo(task);
	}

}
