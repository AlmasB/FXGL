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

package com.almasb.fxgl.app

import com.almasb.fxgl.gameplay.GameWorld
import com.almasb.fxgl.physics.PhysicsWorld
import com.jme3.network.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * API INCOMPLETE
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class ServerApplication {

    private val executor: ScheduledExecutorService
    private val server: Server

    init {
        server = Network.createServer("Test", 1, 55555, 55554)

        executor = Executors.newSingleThreadScheduledExecutor()
    }

    protected fun launch() {
        server.start()

        executor.scheduleAtFixedRate({ update() }, 0, 16, TimeUnit.MILLISECONDS)
    }

    //private val gameWorld = GameWorld()
    //private val physicsWorld = PhysicsWorld()

    protected fun addMessageListener(listener: MessageListener<HostedConnection>) {
        server.addMessageListener(listener)
    }

    protected fun broadcast(message: Message) {
        server.broadcast(message)
    }

    private fun update() {

        //gameWorld.onUpdate(tpf)
        //physicsWorld.onUpdate(tpf)
        val tpf = 0.016

        onUpdate(tpf)
    }

    abstract fun onUpdate(tpf: Double)

    protected fun exit() {
        executor.shutdownNow()

        server.close()
    }
}