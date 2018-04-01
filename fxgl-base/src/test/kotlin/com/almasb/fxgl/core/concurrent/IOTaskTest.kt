/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent

import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.core.math.FXGLMath.*
import com.almasb.fxgl.entity.Entity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.number.IsCloseTo
import org.hamcrest.number.IsCloseTo.closeTo
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class IOTaskTest {

    @Test
    fun `Task run`() {
        var result = ""
        var exceptionMessage = "NoMessage"

        val task = IOTask.of { "MyString" }
        task.onSuccess {
            result = it
        }
        task.onFailure {
            exceptionMessage = it.message.orEmpty()
        }

        assertThat(task.run(), `is`("MyString"))
        assertThat(result, `is`("MyString"))
        assertThat(exceptionMessage, `is`("NoMessage"))
    }

    @Test
    fun `Async runs in a different thread`() {
        var count = 0
        var threadID = -333L

        val task = IOTask.of {
            count++

            threadID = Thread.currentThread().id

            "MyString"
        }

        var result = ""

        val executor = Executor { Thread(it).run() }

        task.onSuccess {
            count++

            assertThat(threadID, `is`(not(-333L)))
            assertThat(threadID, `is`(Thread.currentThread().id))

            result = it
        }

        task.runAsync(executor)

        assertThat(count, `is`(2))
        assertThat(result, `is`("MyString"))
    }
}