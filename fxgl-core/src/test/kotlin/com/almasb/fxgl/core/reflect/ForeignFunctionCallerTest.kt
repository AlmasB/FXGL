/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.reflect

import com.almasb.fxgl.core.util.ResourceExtractor
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.ValueLayout
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
class ForeignFunctionCallerTest {

    @EnabledOnOs(OS.WINDOWS)
    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Downcall a native function in Windows`() {
        `Downcall a native function`("native-lib-test.dll")
    }

    @EnabledOnOs(OS.MAC)
    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Downcall a native function in MacOS`() {
        `Downcall a native function`("native-lib-test.dylib")
    }

    fun `Downcall a native function`(libName:String) {
        val file = ResourceExtractor.extractNativeLibAsPath(libName)

        val countDown = CountDownLatch(1)
        val count = AtomicInteger()

        val ffc = ForeignFunctionCaller(listOf(file))

        ffc.setOnLoaded {
            ffc.execute {
                val result = it.call(
                    "testDownCall",
                    FunctionDescriptor.of(
                        ValueLayout.JAVA_INT,
                        ValueLayout.JAVA_INT
                    ),
                    5
                ) as Int

                count.set(result)
            }

            ffc.unload()
        }

        ffc.setOnUnloaded {
            countDown.countDown()
        }

        ffc.load()

        countDown.await()

        assertThat(count.get(), `is`(25))
    }

}