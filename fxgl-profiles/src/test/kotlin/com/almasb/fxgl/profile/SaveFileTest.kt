/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.profile

import com.almasb.fxgl.core.serialization.Bundle
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
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

        assertThat(dataFile.toString(), `is`("DataFile({test=Bundle test: {test.testInt=3}})"))
    }
}