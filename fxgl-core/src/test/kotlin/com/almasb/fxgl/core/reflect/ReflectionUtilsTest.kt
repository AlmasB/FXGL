/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.reflect

import com.almasb.fxgl.core.util.Function
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.Callable

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ReflectionUtilsTest {

    @Test
    fun `Call a method reflectively`() {
        val obj = TestClass1()

        val result = ReflectionUtils.call<String>(obj, obj.javaClass.getDeclaredMethod("foo"))

        assertThat(result, `is`("Hello"))
    }

    @Test
    fun `New instance`() {
        val obj = ReflectionUtils.newInstance(TestClass1::class.java)

        val result = obj.foo()

        assertThat(result, `is`("Hello"))
    }

    @Test
    fun `Find methods using annotations`() {
        val obj = TestClass1()

        val map = ReflectionUtils.findMethods(obj, Ann::class.java)

        assertThat(map.size, `is`(1))
        assertThat(map.values.first().name, `is`("fooAnnotated"))
    }

    @Test
    fun `Find methods map to functions`() {
        val obj = TestClass1()

        val map = ReflectionUtils.findMethodsMapToFunctions(obj, Ann2::class.java, MyFunction::class.java)

        assertThat(map.size, `is`(1))
        assertThat(map.values.first().apply(" world"), `is`("Hello3 world"))
    }

    @Test
    fun `Inject a field`() {
        val obj = TestClass1()

        ReflectionUtils.inject(ReflectionUtils.getDeclaredField("name", obj).get(), obj, "Hello world!")

        assertThat(obj.name, `is`("Hello world!"))
    }

    @Test
    fun `Is anonymous class`() {
        assertFalse(ReflectionUtils.isAnonymousClass(TestClass1::class.java))
        assertTrue(ReflectionUtils.isAnonymousClass(Runnable { }.javaClass))
    }

    @Retention(AnnotationRetention.RUNTIME)
    annotation class Ann

    @Retention(AnnotationRetention.RUNTIME)
    annotation class Ann2

    interface MyFunction : Function<String, String>

    class TestClass1 {
        lateinit var name: String
            private set

        fun foo() = "Hello"

        @Ann
        fun fooAnnotated() = "Hello2"

        @Ann2
        fun fooMapable(s: String) = "Hello3$s"
    }
}