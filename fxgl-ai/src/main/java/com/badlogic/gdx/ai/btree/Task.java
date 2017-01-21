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

import com.almasb.fxgl.core.reflect.ClassReflection;
import com.almasb.fxgl.core.reflect.ReflectionException;
import com.badlogic.gdx.ai.btree.annotation.TaskConstraint;

/** This is the abstract base class of all behavior tree tasks. The {@code Task} of a behavior tree has a status, one control and a
 * list of children.
 * 
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * 
 * @author implicit-invocation
 * @author davebaol */
@TaskConstraint
public abstract class Task<E> {

	/** The enumeration of the values that a task's status can have.
	 * 
	 * @author davebaol */
	public enum Status {
		/** Means that the task has never run or has been reset. */
		FRESH,
		/** Means that the task needs to run again. */
		RUNNING,
		/** Means that the task returned a failure result. */
		FAILED,
		/** Means that the task returned a success result. */
		SUCCEEDED,
		/** Means that the task has been terminated by an ancestor. */
		CANCELLED;
	}

	/** The clone strategy (if any) that {@link #cloneTask()} will use. Defaults to {@code null}, meaning that {@link #copyTo(Task)}
	 * is used instead. In this case, properly overriding this method in each task is developer's responsibility but this gives you
	 * the opportunity to target GWT.
	 * <p>
	 * For instance, if you don't care about GWT, you can let Kryo make a deep copy for you like that
	 * 
	 * <pre>
	 * <code>
	 *    Task.TASK_CLONER = new TaskCloner() {
	 *       Kryo kryo;
	 *       
	 *       {@literal @}Override
	 *       public {@code <T> Task<T> cloneTask (Task<T>} task) {
	 *          if (kryo == null) {
	 *             kryo = new Kryo();
	 *             kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
	 *          }
	 *          return kryo.copy(task);
	 *       }
	 *    };
	 * </code>
	 * </pre> */
	public static TaskCloner TASK_CLONER = null;

	/** The status of this task. */
	protected Status status = Status.FRESH;

	/** The parent of this task */
	protected Task<E> control;

	/** The behavior tree this task belongs to. */
	protected BehaviorTree<E> tree;

	/** The guard of this task */
	protected Task<E> guard;

	/** This method will add a child to the list of this task's children
	 * 
	 * @param child the child task which will be added
	 * @return the index where the child has been added.
	 * @throws IllegalStateException if the child cannot be added for whatever reason. */
	public final int addChild (Task<E> child) {
		int index = addChildToTask(child);
		if (tree != null && tree.listeners != null) tree.notifyChildAdded(this, index);
		return index;
	}

	/** This method will add a child to the list of this task's children
	 * 
	 * @param child the child task which will be added
	 * @return the index where the child has been added.
	 * @throws IllegalStateException if the child cannot be added for whatever reason. */
	protected abstract int addChildToTask (Task<E> child);

	/** Returns the number of children of this task.
	 * 
	 * @return an int giving the number of children of this task */
	public abstract int getChildCount ();

	/** Returns the child at the given index. */
	public abstract Task<E> getChild (int i);

	/** Returns the blackboard object of the behavior tree this task belongs to.
	 * @throws IllegalStateException if this task has never run */
	public E getObject () {
		if (tree == null) throw new IllegalStateException("This task has never run");
		return tree.getObject();
	}

	/** Returns the guard of this task. */
	public Task<E> getGuard () {
		return guard;
	}

	/** Sets the guard of this task.
	 * @param guard the guard */
	public void setGuard (Task<E> guard) {
		this.guard = guard;
	}

	/** Returns the status of this task. */
	public final Status getStatus () {
		return status;
	}

	/** This method will set a task as this task's control (parent)
	 * 
	 * @param control the parent task */
	public final void setControl (Task<E> control) {
		this.control = control;
		this.tree = control.tree;
	}

	/** Checks the guard of this task.
	 * @param control the parent task
	 * @return {@code true} if guard evaluation succeeds or there's no guard; {@code false} otherwise.
	 * @throws IllegalStateException if guard evaluation returns any status other than {@link Status#SUCCEEDED} and
	 *            {@link Status#FAILED}. */
	public boolean checkGuard (Task<E> control) {
		// No guard to check
		if (guard == null) return true;
		
		// Check the guard of the guard recursively
		if (!guard.checkGuard(control)) return false;

		// Use the tree's guard evaluator task to check the guard of this task
		guard.setControl(control.tree.guardEvaluator);
		guard.start();
		guard.run();
		switch (guard.getStatus()) {
		case SUCCEEDED:
			return true;
		case FAILED:
			return false;
		default:
			throw new IllegalStateException("Illegal guard status '" + guard.getStatus() + "'. Guards must either succeed or fail in one step.");
		}
	}

	/** This method will be called once before this task's first run. */
	public void start () {
	}

	/** This method will be called by {@link #success()}, {@link #fail()} or {@link #cancel()}, meaning that this task's status has
	 * just been set to {@link Status#SUCCEEDED}, {@link Status#FAILED} or {@link Status#CANCELLED} respectively. */
	public void end () {
	}

	/** This method contains the update logic of this task. The actual implementation MUST call {@link #running()},
	 * {@link #success()} or {@link #fail()} exactly once. */
	public abstract void run ();

	/** This method will be called in {@link #run()} to inform control that this task needs to run again */
	public final void running () {
		Status previousStatus = status;
		status = Status.RUNNING;
		if (tree.listeners != null && tree.listeners.size() > 0) tree.notifyStatusUpdated(this, previousStatus);
		if (control != null) control.childRunning(this, this);
	}

	/** This method will be called in {@link #run()} to inform control that this task has finished running with a success result */
	public final void success () {
		Status previousStatus = status;
		status = Status.SUCCEEDED;
		if (tree.listeners != null && tree.listeners.size() > 0) tree.notifyStatusUpdated(this, previousStatus);
		end();
		if (control != null) control.childSuccess(this);
	}

	/** This method will be called in {@link #run()} to inform control that this task has finished running with a failure result */
	public final void fail () {
		Status previousStatus = status;
		status = Status.FAILED;
		if (tree.listeners != null && tree.listeners.size() > 0) tree.notifyStatusUpdated(this, previousStatus);
		end();
		if (control != null) control.childFail(this);
	}

	/** This method will be called when one of the children of this task succeeds
	 * 
	 * @param task the task that succeeded */
	public abstract void childSuccess (Task<E> task);

	/** This method will be called when one of the children of this task fails
	 * 
	 * @param task the task that failed */
	public abstract void childFail (Task<E> task);

	/** This method will be called when one of the ancestors of this task needs to run again
	 * 
	 * @param runningTask the task that needs to run again
	 * @param reporter the task that reports, usually one of this task's children */
	public abstract void childRunning (Task<E> runningTask, Task<E> reporter);

	/** Terminates this task and all its running children. This method MUST be called only if this task is running. */
	public final void cancel () {
		cancelRunningChildren(0);
		Status previousStatus = status;
		status = Status.CANCELLED;
		if (tree.listeners != null && tree.listeners.size() > 0) tree.notifyStatusUpdated(this, previousStatus);
		end();
	}

	/** Terminates the running children of this task starting from the specified index up to the end.
	 * @param startIndex the start index */
	protected void cancelRunningChildren (int startIndex) {
		for (int i = startIndex, n = getChildCount(); i < n; i++) {
			Task<E> child = getChild(i);
			if (child.status == Status.RUNNING) child.cancel();
		}
	}

	/** Resets this task to make it restart from scratch on next run. */
	public void reset () {
		if (status == Status.RUNNING) cancel();
		for (int i = 0, n = getChildCount(); i < n; i++) {
			getChild(i).reset();
		}
		status = Status.FRESH;
		tree = null;
		control = null;
	}

	/** Clones this task to a new one. If you don't specify a clone strategy through {@link #TASK_CLONER} the new task is
	 * instantiated via reflection and {@link #copyTo(Task)} is invoked.
	 * @return the cloned task
	 * @throws TaskCloneException if the task cannot be successfully cloned. */
	@SuppressWarnings("unchecked")
	public Task<E> cloneTask () {
		if (TASK_CLONER != null) {
			try {
				return TASK_CLONER.cloneTask(this);
			} catch (Throwable t) {
				throw new TaskCloneException(t);
			}
		}
		try {
			Task<E> clone = copyTo(ClassReflection.newInstance(this.getClass()));
			clone.guard = guard == null ? null : guard.cloneTask();
			return clone;
		} catch (ReflectionException e) {
			throw new TaskCloneException(e);
		}
	}

	/** Copies this task to the given task. This method is invoked by {@link #cloneTask()} only if {@link #TASK_CLONER} is
	 * {@code null} which is its default value.
	 * @param task the task to be filled
	 * @return the given task for chaining
	 * @throws TaskCloneException if the task cannot be successfully copied. */
	protected abstract Task<E> copyTo (Task<E> task);

}
