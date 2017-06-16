/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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

        log.debug("Default pool size for objects = $initialSize")
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