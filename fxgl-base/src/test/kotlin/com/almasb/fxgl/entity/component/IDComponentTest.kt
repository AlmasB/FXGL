/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component

import com.almasb.fxgl.entity.components.IDComponent
import com.almasb.fxgl.io.serialization.Bundle
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class IDComponentTest {

    @Test
    fun `Equality`() {
        val id1 = IDComponent("Test", 0)
        val id2 = IDComponent("Test", 1)

        assertThat(id1.name, `is`(id2.name))
        assertThat(id1.id, `is`(not(id2.id)))
        assertFalse(id1 === id2)
        assertThat(id1.fullID, `is`(not(id2.fullID)))
        assertThat(id1.hashCode(), `is`(not(id2.hashCode())))
    }

    @Test
    fun `Serialization`() {
        val id1 = IDComponent("Test", 0)

        val bundle = Bundle("test")
        id1.write(bundle)

        val id2 = IDComponent("name", 2)
        id2.read(bundle)

        assertFalse(id1 === id2)
        assertThat(id1.name, `is`(id2.name))
        assertThat(id1.id, `is`(id2.id))
        assertThat(id1.fullID, `is`(id2.fullID))
        assertThat(id1.hashCode(), `is`(id2.hashCode()))
    }
}