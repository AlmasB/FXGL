/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai

import com.almasb.fxgl.ai.btree.LeafTask
import com.almasb.fxgl.ai.btree.Task
import com.almasb.fxgl.entity.Entity

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AttackTask : LeafTask<Entity>() {

    override fun execute(): Status {
        return Status.SUCCEEDED
    }

    override fun copyTo(task: Task<Entity>): Task<Entity> {
        return task
    }
}