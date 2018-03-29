/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai

import com.almasb.fxgl.ai.btree.LeafTask
import com.almasb.fxgl.ai.btree.Task
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.entity.Entity

/**
 * In a behavior tree a goal action is executed until it reaches the goal.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class GoalAction
@JvmOverloads constructor(val name: String = "") : LeafTask<Entity>() {

    /**
     * The action succeeds when this returns true.
     */
    abstract fun reachedGoal(): Boolean

    /**
     * Executed every frame when action is active.
     */
    abstract fun onUpdate(tpf: Double)

    override final fun execute(): Status {
        if (reachedGoal())
            return Status.SUCCEEDED

        entity.getComponent(AIControl::class.java).setBubbleMessage(if (name.isNotEmpty()) name else javaClass.simpleName)
        onUpdate(FXGL.getApp().tpf())
        return if (reachedGoal()) Status.SUCCEEDED else Status.RUNNING
    }

    override fun copyTo(task: Task<Entity>): Task<Entity> {
        return task
    }
}