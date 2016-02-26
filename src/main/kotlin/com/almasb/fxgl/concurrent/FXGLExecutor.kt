/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.concurrent

import com.almasb.fxeventbus.EventBus
import com.almasb.fxgl.event.FXGLEvent
import com.almasb.fxgl.util.FXGLLogger
import com.google.inject.Inject
import com.google.inject.Singleton
import javafx.concurrent.Task

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Logger

/**
 * Uses cached thread pool to run tasks in the background.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
class FXGLExecutor
@Inject
private constructor(eventBus: EventBus) : Executor {

    companion object {
        private val log = FXGLLogger.getLogger("FXGL.Executor")
    }

    private val service = Executors.newCachedThreadPool()

    init {
        eventBus.addEventHandler(FXGLEvent.EXIT) { event -> service.shutdownNow() }

        log.finer { "Service [Executor] initialized" }
    }

    override fun submit(task: Task<*>) {
        service.submit(task)
    }
}