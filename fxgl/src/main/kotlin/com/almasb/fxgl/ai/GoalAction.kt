/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai

import com.almasb.fxgl.ai.btree.LeafTask
import com.almasb.fxgl.ai.btree.Task
import com.almasb.fxgl.entity.GameEntity

/**
 * In a behavior tree a goal action is executed until it reaches the goal.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class GoalAction(val name: String) : LeafTask<GameEntity>() {

    constructor() : this("")

    /**
     * The action succeeds when this returns true.
     */
    abstract fun reachedGoal(): Boolean

    abstract fun action()

    override final fun execute(): Status {
        if (reachedGoal())
            return Status.SUCCEEDED

        `object`.getControl(AIControl::class.java).setBubbleMessage(if (name.isNotEmpty()) name else javaClass.simpleName)
        action()
        return if (reachedGoal()) Status.SUCCEEDED else Status.RUNNING
    }

    override fun copyTo(task: Task<GameEntity>): Task<GameEntity> {
        return task
    }
}