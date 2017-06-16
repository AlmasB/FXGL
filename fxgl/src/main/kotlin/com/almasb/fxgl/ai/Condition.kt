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
 * Represents a single conditional statement of a behavior tree.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class Condition : LeafTask<GameEntity>() {

    /**
     * Condition succeeds if this returns true.
     */
    abstract fun evaluate(): Boolean

    override final fun execute(): Status {
        return if (evaluate()) Status.SUCCEEDED else Status.FAILED
    }

    override fun copyTo(task: Task<GameEntity>): Task<GameEntity> {
        return task
    }
}