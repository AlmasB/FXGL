/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.test.RunWithFX
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.time.Duration
import java.util.concurrent.CountDownLatch

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class EngineTest {

    private lateinit var engine: Engine

    companion object {
        lateinit var settings: GameSettings

        var latch = CountDownLatch(1)

        @BeforeAll
        @JvmStatic fun before() {
            settings = GameSettings().also {
                it.engineServices.clear()
                it.engineServices.addAll(arrayOf(TestService1::class.java, TestService2::class.java))
            }
        }
    }

    @BeforeEach
    fun setUp() {
        engine = Engine(settings.toReadOnly())
    }

    @Test
    fun `Fail if given service not found`() {
        assertThrows<IllegalArgumentException> {
            engine.getService(TestService1::class.java)
        }
    }

    @Test
    fun `Check dependencies are injected into services`() {
        engine.environmentVars["name"] = "Hello World FXGL"

        engine.initServicesAndStartLoop()

        assertTimeoutPreemptively(Duration.ofSeconds(3)) {
            latch.await()

            val service1 = engine.getService(TestService1::class.java)

            assertThat(service1.name, `is`("Hello World FXGL"))
            assertTrue(service1.service === engine.getService(TestService2::class.java))
            assertThat(service1.count, `is`(1))

            engine.stopLoopAndExitServices()

            assertThat(service1.count, `is`(2))
        }
    }

    @Test
    fun `Failed dependencies are thrown to JavaFX thread`() {
        engine = Engine(GameSettings().also {
            it.engineServices.clear()
            it.engineServices.addAll(arrayOf(FailService::class.java))
        }.toReadOnly())

        var error: Throwable? = null

        var oldHandler: Thread.UncaughtExceptionHandler? = null

        val l = CountDownLatch(1)

        Async.startAsyncFX {
            oldHandler = Thread.getDefaultUncaughtExceptionHandler()

            Thread.setDefaultUncaughtExceptionHandler { _, e ->
                error = e
                l.countDown()
            }
        }.await()

        engine.initServicesAndStartLoop()

        assertTimeoutPreemptively(Duration.ofSeconds(3)) {
            l.await()

            Async.startAsyncFX {
                Thread.setDefaultUncaughtExceptionHandler(oldHandler)
            }.await()

            assertNotNull(error)
        }
    }

    class TestService1 : EngineService() {

        lateinit var service: TestService2

        @Inject("name")
        lateinit var name: String

        var count = 0

        override fun onInit() {
            count = 1
        }

        override fun onExit() {
            count = 2
        }
    }

    class TestService2 : EngineService() {

        override fun onInit() {
            latch.countDown()
        }
    }

    class FailService : EngineService() {
        lateinit var service: TestService2
    }
}