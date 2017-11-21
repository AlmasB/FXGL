/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.btree;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.math.FXGLMath;

/**
 * A {@code SingleRunningChildBranch} task is a branch task that supports only one running child at a time.
 *
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * @author implicit-invocation
 * @author davebaol
 */
public abstract class SingleRunningChildBranch<E> extends BranchTask<E> {

    /**
     * The child in the running status or {@code null} if no child is running.
     */
    private Task<E> runningChild;

    /**
     * The index of the child currently processed.
     */
    protected int currentChildIndex;

    /**
     * Array of random children. If it's {@code null} this task is deterministic.
     */
    protected Task<E>[] randomChildren;

    /**
     * Creates a {@code SingleRunningChildBranch} task with no children
     */
    public SingleRunningChildBranch() {
        super();
    }

    /**
     * Creates a {@code SingleRunningChildBranch} task with a list of children
     *
     * @param tasks list of this task's children, can be empty
     */
    public SingleRunningChildBranch(Array<Task<E>> tasks) {
        super(tasks);
    }

    @Override
    public void childRunning(Task<E> task, Task<E> reporter) {
        runningChild = task;
        running(); // Return a running status when a child says it's running
    }

    @Override
    public void childSuccess(Task<E> task) {
        this.runningChild = null;
    }

    @Override
    public void childFail(Task<E> task) {
        this.runningChild = null;
    }

    @Override
    public void run() {
        if (runningChild != null) {
            runningChild.run();
        } else {
            if (currentChildIndex < children.size()) {
                if (randomChildren != null) {
                    int last = children.size() - 1;
                    if (currentChildIndex < last) {
                        // Random swap
                        int otherChildIndex = FXGLMath.random(currentChildIndex, last);
                        Task<E> tmp = randomChildren[currentChildIndex];
                        randomChildren[currentChildIndex] = randomChildren[otherChildIndex];
                        randomChildren[otherChildIndex] = tmp;
                    }
                    runningChild = randomChildren[currentChildIndex];
                } else {
                    runningChild = children.get(currentChildIndex);
                }
                runningChild.setControl(this);
                runningChild.start();
                if (!runningChild.checkGuard(this))
                    runningChild.fail();
                else
                    run();
            } else {
                // Should never happen; this case must be handled by subclasses in childXXX methods
            }
        }
    }

    @Override
    public void start() {
        this.currentChildIndex = 0;
        runningChild = null;
    }

    @Override
    protected void cancelRunningChildren(int startIndex) {
        super.cancelRunningChildren(startIndex);
        runningChild = null;
    }

    @Override
    public void reset() {
        super.reset();
        this.currentChildIndex = 0;
        this.runningChild = null;
        this.randomChildren = null;
    }

    @Override
    protected Task<E> copyTo(Task<E> task) {
        SingleRunningChildBranch<E> branch = (SingleRunningChildBranch<E>) task;
        branch.randomChildren = null;

        return super.copyTo(task);
    }

    @SuppressWarnings("unchecked")
    protected Task<E>[] createRandomChildren() {
        Task<E>[] rndChildren = new Task[children.size()];
        System.arraycopy(children.getItems(), 0, rndChildren, 0, children.size());
        return rndChildren;
    }
}
