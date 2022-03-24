/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.quest

import com.almasb.fxgl.core.collection.PropertyMap
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestTest {

    @Test
    fun `Creation`() {
        val quest = Quest("First test quest")

        assertThat(quest.name, `is`("First test quest"))
        assertTrue(quest.objectivesProperty().isEmpty())
    }

    @Test
    fun `Valid transitions`() {
        val quest = Quest("")

        assertThat(quest.state, `is`(QuestState.NOT_STARTED))

        val obj = quest.addBooleanObjective("test obj", "testKey", true)

        val map = PropertyMap()
        map.setValue("testKey", false)

        obj.bindTo(map)

        quest.start()

        assertThat(quest.state, `is`(QuestState.ACTIVE))

        obj.fail()

        assertThat(quest.state, `is`(QuestState.FAILED))

        obj.reactivate(map)

        assertThat(quest.state, `is`(QuestState.ACTIVE))

        map.setValue("testKey", true)

        assertThat(quest.state, `is`(QuestState.COMPLETED))
    }
}