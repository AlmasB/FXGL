/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.profile

import com.almasb.fxgl.core.serialization.Bundle
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsIterableContainingInOrder.contains
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.time.LocalDateTime

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SaveFileTest {

    @Test
    fun `SaveFile basics`() {
        val saveFile = SaveFile("saveName.dat")

        assertThat(saveFile.name, `is`("saveName.dat"))
        assertNotNull(saveFile.data)

        val dateTime = LocalDateTime.now()

        assertTrue(saveFile.dateTime.isBefore(dateTime) || saveFile.dateTime.isEqual(dateTime))

        assertThat(saveFile.toString(), `is`("SaveFile(saveName.dat)"))
    }

    @Test
    fun `DataFile basics`() {
        val dataFile = DataFile()
        val bundle = Bundle("test")
        bundle.put("testInt", 3)

        dataFile.putBundle(bundle)

        val bundle2 = dataFile.getBundle("test")
        val value = bundle2.get<Int>("testInt")

        assertThat(value, `is`(3))

        assertThat(dataFile.toString(), `is`("DataFile({test=Bundle test: {testInt=3}})"))
    }

    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    @Test
    fun `RECENT_FIRST correctly sorts by timestamp`() {
        val save1 = SaveFile("1")

        // make sure there is a big difference in timestamps
        Thread.sleep(1000)

        val save2 = SaveFile("2")

        Thread.sleep(1000)

        val save3 = SaveFile("3")

        val list = mutableListOf(save1, save3, save2)
        list.sortWith(SaveFile.RECENT_FIRST)

        assertThat(list, contains(save3, save2, save1))
    }

    @Test
    fun `No bundle with same name is allowed`() {
        val dataFile = DataFile()
        dataFile.putBundle(Bundle("test"))

        assertThrows<IllegalArgumentException> {
            dataFile.putBundle(Bundle("test"))
        }
    }

    @Test
    fun `Throws if data file does not have a bundle with given name`() {
        assertThrows<IllegalArgumentException> {
            DataFile().getBundle("test")
        }
    }
}