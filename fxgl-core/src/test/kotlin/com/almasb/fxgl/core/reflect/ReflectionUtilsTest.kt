/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.reflect

import com.almasb.fxgl.core.util.Function
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ReflectionUtilsTest {

    @Test
    fun `Get a method of a class`() {
        val obj = TestClass1()

        val result = ReflectionUtils.getMethod(obj.javaClass, "foo").invoke(obj) as String

        assertThat(result, `is`("Hello"))
    }

    @Test
    fun `Get a method throws if no such method`() {
        assertThrows<ReflectionException> {
            val obj = TestClass1()

            ReflectionUtils.getMethod(obj.javaClass, "bla-bla")
        }
    }

    @Test
    fun `Call a method reflectively`() {
        val obj = TestClass1()

        val result = ReflectionUtils.call<String>(obj, obj.javaClass.getDeclaredMethod("foo"))

        assertThat(result, `is`("Hello"))
    }

    @Test
    fun `Call an inaccessible method reflectively`() {
        val obj = TestClass1()

        val result = ReflectionUtils.callInaccessible<String>(obj, obj.javaClass.getDeclaredMethod("fooInaccessible"))

        assertThat(result, `is`("Hello4"))
    }

    @Test
    fun `Call a method reflectively throws if invalid params`() {
        assertThrows<ReflectionException> {
            val obj = TestClass1()

            ReflectionUtils.call<String>(obj, obj.javaClass.getDeclaredMethod("foo"), "")
        }
    }

    @Test
    fun `New instance`() {
        val obj = ReflectionUtils.newInstance(TestClass1::class.java)

        val result = obj.foo()

        assertThat(result, `is`("Hello"))
    }

    @Test
    fun `New instance throws if cant create`() {
        assertThrows<ReflectionException> {
            ReflectionUtils.newInstance(TestClass2::class.java)
        }
    }

    @Test
    fun `Map a method to a function`() {
        val obj = TestClass1()

        val func = ReflectionUtils.mapToFunction<String, String>(obj, obj.javaClass.getDeclaredMethod("fooMapable", String::class.java))

        val result = func.apply(" world")

        assertThat(result, `is`("Hello3 world"))
    }

    @Test
    fun `Find methods using annotations`() {
        val obj = TestClass1()

        val map = ReflectionUtils.findMethods(obj, Ann::class.java)

        assertThat(map.size, `is`(1))
        assertThat(map.values.first().name, `is`("fooAnnotated"))
    }

    @Test
    fun `Find methods map to general functions`() {
        val obj = TestClass1()

        val map = ReflectionUtils.findMethodsMapToFunctions<String, String, Ann2>(obj, Ann2::class.java)

        assertThat(map.size, `is`(1))
        assertThat(map.values.first().apply(" world"), `is`("Hello3 world"))
    }

    @Test
    fun `Find methods map to specific functions`() {
        val obj = TestClass1()

        val map = ReflectionUtils.findMethodsMapToFunctions(obj, Ann2::class.java, MyFunction::class.java)

        assertThat(map.size, `is`(1))
        assertThat(map.values.first().apply(" world"), `is`("Hello3 world"))
    }

    @Test
    fun `Find fields by annotation`() {
        val obj = TestClass1()

        val fields = ReflectionUtils.findFieldsByAnnotation(obj, FieldAnn::class.java)

        assertThat(fields.size(), `is`(1))
    }

    @Test
    fun `Find fields by type`() {
        val obj = TestClass1()

        val fields = ReflectionUtils.findDeclaredFieldsByType(obj, Int::class.java)

        assertThat(fields.size(), `is`(1))
    }

    @Test
    fun `Inject a field`() {
        val obj = TestClass1()

        ReflectionUtils.inject(ReflectionUtils.getDeclaredField("name", obj).get(), obj, "Hello world!")

        assertThat(obj.name, `is`("Hello world!"))
    }

    @Test
    fun `Get field returns optional empty if no such field`() {
        assertFalse(ReflectionUtils.getDeclaredField("name", "").isPresent)
    }

    @Test
    fun `Is anonymous class`() {
        assertFalse(ReflectionUtils.isAnonymousClass(TestClass1::class.java))
        assertTrue(ReflectionUtils.isAnonymousClass(Runnable { }.javaClass))
    }

    @Test
    fun `Get calling class using superclass and method name`() {
        val c = TestSuperClass.TestSubClass.highLevelFunction()
        assertTrue(TestSuperClass.TestSubClass::class.java == c)
    }

    @Test
    fun `Get calling class throws if called not from subclass`() {
        assertThrows<ReflectionException> {
            ReflectionUtils.getCallingClass(TestSuperClass::class.java, "lowLevelFunction")
        }
    }

    @Retention(AnnotationRetention.RUNTIME)
    annotation class Ann

    @Retention(AnnotationRetention.RUNTIME)
    annotation class Ann2

    @Target(AnnotationTarget.FIELD)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class FieldAnn

    interface MyFunction : Function<String, String>

    class TestClass1 {

        @FieldAnn
        private val fieldInt = -1

        lateinit var name: String
            private set

        fun foo() = "Hello"

        @Ann
        fun fooAnnotated() = "Hello2"

        @Ann2
        fun fooMapable(s: String) = "Hello3$s"

        private fun fooInaccessible() = "Hello4"
    }

    class TestClass2(val s: String)
}