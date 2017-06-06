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
 * A single action that always succeeds.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class Action(val name: String) : LeafTask<GameEntity>() {

    constructor() : this("")

    abstract fun action()

    override final fun execute(): Status {
        `object`.getControlUnsafe(AIControl::class.java).setBubbleMessage(if (name.isNotEmpty()) name else javaClass.simpleName)
        action()

        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<GameEntity>): Task<GameEntity> {
        return task
    }
}