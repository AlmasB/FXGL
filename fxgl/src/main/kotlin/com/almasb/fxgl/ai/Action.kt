/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.ai

import com.almasb.fxgl.entity.GameEntity
import com.almasb.fxgl.ai.btree.LeafTask
import com.almasb.fxgl.ai.btree.Task

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