/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.core.concurrent

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/**
 *
 * @author Jean-Rene Lavoie (jeanrlavoie@gmail.com)
 */
class AsyncServiceTest {

    @Test
    fun `Async Service with T (String)`() {
        var postUpdateValue = ""
        val service = object : AsyncService<String>() {
            override fun onGameUpdateAsync(tpf: Double): String {
                return "Done"
            }

            override fun onPostGameUpdateAsync(result: String) {
                postUpdateValue = result
            }
        }

        // On first call, we don't have the postUpdateValue yet
        service.onGameUpdate(1.0)
        assertEquals(postUpdateValue, "")

        // On second update, we updated the postUpdateValue
        service.onGameUpdate(1.0)
        assertEquals(postUpdateValue, "Done")
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Async Service parallel`() {
        val count = AtomicInteger(0)

        // Processing of each service takes more time than a normal tick
        val services = listOf(
            object : AsyncService<Unit>() {
                override fun onGameUpdateAsync(tpf: Double) {
                    Thread.sleep(100)
                    count.incrementAndGet()
                }
            },
            object : AsyncService<Unit>() {
                override fun onGameUpdateAsync(tpf: Double) {
                    Thread.sleep(100)
                    count.incrementAndGet()
                }
            },
            object : AsyncService<Unit>() {
                override fun onGameUpdateAsync(tpf: Double) {
                    Thread.sleep(100)
                    count.incrementAndGet()
                }
            }
        )

        // 3 services started faster than their combined execution time
        assertThat(measureTimeMillis {
            services.forEach { service ->
                service.onGameUpdate(1.0)
            }
        }.toDouble(), lessThan(300.0))

        assertThat(count.get(), `is`(0))

        // resolve services
        services.forEach { service ->
            service.onGameUpdate(1.0)
        }

        assertThat(count.get(), `is`(greaterThan(2)))
    }
}