/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.btree;

import com.almasb.fxgl.ai.btree.annotation.TaskConstraint;

/**
 * A {@code LeafTask} is a terminal task of a behavior tree, contains action or condition logic, can not have any child.
 *
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * @author implicit-invocation
 * @author davebaol
 */
@TaskConstraint(minChildren = 0, maxChildren = 0)
public abstract class LeafTask<E> extends Task<E> {

    /**
     * Creates a leaf task.
     */
    public LeafTask() {
    }

    /**
     * This method contains the update logic of this leaf task. The actual implementation MUST return one of {@link Status#RUNNING}
     * , {@link Status#SUCCEEDED} or {@link Status#FAILED}. Other return values will cause an {@code IllegalStateException}.
     *
     * @return the status of this leaf task
     */
    public abstract Status execute();

    /**
     * This method contains the update logic of this task. The implementation delegates the {@link #execute()} method.
     */
    @Override
    public final void run() {
        Status result = execute();
        if (result == null)
            throw new IllegalStateException("Invalid status 'null' returned by the execute method");

        switch (result) {
            case SUCCEEDED:
                success();
                return;
            case FAILED:
                fail();
                return;
            case RUNNING:
                running();
                return;
            default:
                throw new IllegalStateException("Invalid status '" + result.name() + "' returned by the execute method");
        }
    }

    /**
     * Always throws {@code IllegalStateException} because a leaf task cannot have any children.
     */
    @Override
    protected int addChildToTask(Task<E> child) {
        throw new IllegalStateException("A leaf task cannot have any children");
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public Task<E> getChild(int i) {
        throw new IndexOutOfBoundsException("A leaf task can not have any child");
    }

    @Override
    public final void childRunning(Task<E> runningTask, Task<E> reporter) {
    }

    @Override
    public final void childFail(Task<E> runningTask) {
    }

    @Override
    public final void childSuccess(Task<E> runningTask) {
    }
}
