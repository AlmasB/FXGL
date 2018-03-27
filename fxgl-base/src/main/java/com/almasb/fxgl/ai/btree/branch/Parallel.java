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

import com.almasb.fxgl.ai.btree.BranchTask;
import com.almasb.fxgl.ai.btree.Task;
import com.almasb.fxgl.ai.btree.annotation.TaskAttribute;
import com.almasb.fxgl.core.collection.Array;

/** A {@code Parallel} is a special branch task that starts or resumes all children every single time. The actual behavior of
 * parallel task depends on its {@link #policy}:
 * <ul>
 * <li>{@link Policy#Sequence}: the parallel task fails as soon as one child fails; if all children succeed, then the parallel
 * task succeeds. This is the default policy.</li>
 * <li>{@link Policy#Selector}: the parallel task succeeds as soon as one child succeeds; if all children fail, then the parallel
 * task fails.</li>
 * </ul>
 * 
 * The typical use case: make the game entity react on event while sleeping or wandering.
 * 
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * 
 * @author implicit-invocation
 * @author davebaol */
public class Parallel<E> extends BranchTask<E> {

	/** Optional task attribute specifying the parallel policy (defaults to {@link Policy#Sequence}) */
	@TaskAttribute
    public Policy policy;

	private boolean noRunningTasks;
	private Boolean lastResult;
	private int currentChildIndex;

	/** Creates a parallel task with sequence policy and no children */
	public Parallel () {
		this(new Array<Task<E>>());
	}

	/** Creates a parallel task with sequence policy and the given children
	 * @param tasks the children */
	public Parallel (Task<E>... tasks) {
		this(new Array<Task<E>>(tasks));
	}

	/** Creates a parallel task with sequence policy and the given children
	 * @param tasks the children */
	public Parallel (Array<Task<E>> tasks) {
		this(Policy.Sequence, tasks);
	}

	/** Creates a parallel task with the given policy and no children
	 * @param policy the policy */
	public Parallel (Policy policy) {
		this(policy, new Array<Task<E>>());
	}

	/** Creates a parallel task with the given policy and children
	 * @param policy the policy
	 * @param tasks the children */
	public Parallel (Policy policy, Task<E>... tasks) {
		this(policy, new Array<Task<E>>(tasks));
	}

	/** Creates a parallel task with the given policy and children
	 * @param policy the policy
	 * @param tasks the children */
	public Parallel (Policy policy, Array<Task<E>> tasks) {
		super(tasks);
		this.policy = policy;
		noRunningTasks = true;
	}

	@Override
	public void run () {
		noRunningTasks = true;
		lastResult = null;
		for (currentChildIndex = 0; currentChildIndex < children.size(); currentChildIndex++) {
			Task<E> child = children.get(currentChildIndex);
			if (child.getStatus() == Status.RUNNING) {
				child.run();
			} else {
				child.setControl(this);
				child.start();
				if (child.checkGuard(this))
					child.run();
				else
					child.fail();
			}

			if (lastResult != null) { // Current child has finished either with success or fail
				cancelRunningChildren(noRunningTasks ? currentChildIndex + 1 : 0);
				if (lastResult)
					success();
				else
					fail();
				return;
			}
		}
		running();
	}

	@Override
	public void childRunning (Task<E> task, Task<E> reporter) {
		noRunningTasks = false;
	}

	@Override
	public void childSuccess (Task<E> runningTask) {
		lastResult = policy.onChildSuccess(this);
	}

	@Override
	public void childFail (Task<E> runningTask) {
		lastResult = policy.onChildFail(this);
	}

	@Override
	public void reset () {
		super.reset();
		noRunningTasks = true;
	}

	@Override
	protected Task<E> copyTo (Task<E> task) {
		Parallel<E> parallel = (Parallel<E>)task;
		parallel.policy = policy; // no need to clone since it is immutable

		return super.copyTo(task);
	}

	/** The enumeration of the policies supported by the {@link Parallel} task. */
	public enum Policy {
		/** The sequence policy makes the {@link Parallel} task fail as soon as one child fails; if all children succeed, then the
		 * parallel task succeeds. This is the default policy. */
		Sequence() {
			@Override
			public Boolean onChildSuccess (Parallel<?> parallel) {
				return parallel.noRunningTasks && parallel.currentChildIndex == parallel.children.size() - 1 ? Boolean.TRUE : null;
			}

			@Override
			public Boolean onChildFail (Parallel<?> parallel) {
				return Boolean.FALSE;
			}
		},
		/** The selector policy makes the {@link Parallel} task succeed as soon as one child succeeds; if all children fail, then the
		 * parallel task fails. */
		Selector() {
			@Override
			public Boolean onChildSuccess (Parallel<?> parallel) {
				return Boolean.TRUE;
			}

			@Override
			public Boolean onChildFail (Parallel<?> parallel) {
				return parallel.noRunningTasks && parallel.currentChildIndex == parallel.children.size() - 1 ? Boolean.FALSE : null;
			}
		};

		/** Called by parallel task each time one of its children succeeds.
		 * @param parallel the parallel task
		 * @return {@code Boolean.TRUE} if parallel must succeed, {@code Boolean.FALSE} if parallel must fail and {@code null} if
		 *         parallel must keep on running. */
		public abstract Boolean onChildSuccess (Parallel<?> parallel);

		/** Called by parallel task each time one of its children fails.
		 * @param parallel the parallel task
		 * @return {@code Boolean.TRUE} if parallel must succeed, {@code Boolean.FALSE} if parallel must fail and {@code null} if
		 *         parallel must keep on running. */
		public abstract Boolean onChildFail (Parallel<?> parallel);

	}
}
