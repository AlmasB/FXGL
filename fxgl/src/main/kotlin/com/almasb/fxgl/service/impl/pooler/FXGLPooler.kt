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

package com.almasb.fxgl.service.impl.pooler

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.core.pool.Pool
import com.almasb.fxgl.core.pool.Pools
import com.almasb.fxgl.service.Pooler
import com.google.inject.Inject
import com.google.inject.name.Named

/**
 * FXGL provider for [Pooler] service.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLPooler
@Inject private constructor(@Named("pooling.initialSize") initialSize: Int): Pooler {

    private val log = FXGL.getLogger(javaClass)

    init {
        // pool commonly used types
        registerPool(Vec2::class.java, object : Pool<Vec2>(initialSize) {
            override fun newObject(): Vec2 {
                return Vec2()
            }
        })

        log.debug("Service [Pooler] initialized with default size = $initialSize")
    }

    override fun <T : Any> get(type: Class<T>): T {
        return Pools.obtain(type)
    }

    override fun put(`object`: Any) {
        Pools.free(`object`)
    }

    override fun <T : Any> registerPool(type: Class<T>, pool: Pool<T>) {
        Pools.set(type, pool)
    }
}