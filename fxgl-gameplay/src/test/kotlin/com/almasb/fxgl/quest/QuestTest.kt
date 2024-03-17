/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.quest

import com.almasb.fxgl.core.collection.PropertyMap
import javafx.util.Duration
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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
        assertThat(quest.stateProperty().value, `is`(quest.state))
    }

    @Test
    fun `Add and remove objectives`() {
        val vars = PropertyMap()
        vars.setValue("varBoolean", false)
        vars.setValue("varInt", 0)

        val quest = Quest("", vars)

        val obj1 = quest.addBooleanObjective("Desc", "varBoolean", true, Duration.seconds(15.0))
        val obj2 = quest.addIntObjective("Desc", "varInt", 5)

        assertThat(quest.objectivesProperty(), Matchers.contains(obj1, obj2))

        quest.removeObjective(obj1)
        assertThat(quest.objectivesProperty(), Matchers.contains(obj2))

        quest.removeObjective(obj2)
        assertTrue(quest.objectivesProperty().isEmpty())
    }

    @Test
    fun `Valid transitions and objective completion`() {
        val vars = PropertyMap()
        vars.setValue("testKey", false)
        vars.setValue("testInt", 4)
        vars.setValue("testInt2", 0)

        val quest = Quest("", vars)

        assertThat(quest.state, `is`(QuestState.NOT_STARTED))

        val obj = quest.addBooleanObjective("test obj", "testKey", true)

        assertFalse(quest.isStarted)

        quest.start()

        assertTrue(quest.isStarted)
        assertThat(quest.state, `is`(QuestState.ACTIVE))

        obj.fail()

        assertThat(quest.state, `is`(QuestState.FAILED))

        obj.reactivate()

        assertThat(quest.state, `is`(QuestState.ACTIVE))

        vars.setValue("testKey", true)

        assertThat(quest.state, `is`(QuestState.COMPLETED))

        // add new objective, quest is now active again
        val obj2 = quest.addIntObjective("Desc", "testInt", 5)

        assertThat(quest.state, `is`(QuestState.ACTIVE))

        vars.setValue("testInt", 5)
        assertThat(quest.state, `is`(QuestState.COMPLETED))

        // add another new objective
        val obj3 = quest.addIntObjective("Desc", "testInt2", 5)

        assertThat(quest.state, `is`(QuestState.ACTIVE))

        // remove objective, making quest complete
        quest.removeObjective(obj3)

        assertThat(quest.state, `is`(QuestState.COMPLETED))
    }

    @Test
    fun `Objective fails if timer expired`() {
        val vars = PropertyMap()
        vars.setValue("testInt", 0)

        val quest = Quest("", vars)

        val obj = quest.addIntObjective("Desc", "testInt", 5, Duration.seconds(2.0))
        assertThat(obj.timeRemainingProperty().value, `is`(2.0))

        quest.start()
        assertThat(obj.state, `is`(QuestState.ACTIVE))
        assertThat(quest.state, `is`(QuestState.ACTIVE))

        quest.onUpdate(1.0)
        assertThat(obj.timeRemainingProperty().value, `is`(1.0))
        assertThat(obj.state, `is`(QuestState.ACTIVE))
        assertThat(quest.state, `is`(QuestState.ACTIVE))

        // this pushes the objective beyond the 2 sec expiry duration, so obj should fail
        quest.onUpdate(1.5)
        assertThat(obj.timeRemainingProperty().value, `is`(0.0))
        assertThat(obj.state, `is`(QuestState.FAILED))
        assertThat(quest.state, `is`(QuestState.FAILED))

    }

    @Test
    fun `Objective can be completed directly`() {
        val vars = PropertyMap()
        vars.setValue("testInt", 0)

        val quest = Quest("", vars)

        val obj = quest.addIntObjective("Desc", "testInt", 5, Duration.seconds(2.0))

        assertThat(obj.state, `is`(QuestState.ACTIVE))

        obj.complete()
        assertThat(obj.state, `is`(QuestState.COMPLETED))
    }

    @Test
    fun `Throws exception if objective has no variables to bind to`() {
        val quest = Quest("")
        quest.addIntObjective("Desc", "testInt", 5)

        assertThrows<IllegalArgumentException> {
            quest.start()
        }
    }
}