/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.quest

import com.almasb.fxgl.core.collection.PropertyMap
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestServiceTest {

    @Test
    fun `Quests lifecycle`() {
        val questService = QuestService()

        val quest = questService.newQuest("name")
        quest.addIntObjective("", "testInt", 1)
        quest.vars.setValue("testInt", 0)

        assertThat(quest.state, `is`(QuestState.NOT_STARTED))

        questService.startQuest(quest)
        assertThat(quest.state, `is`(QuestState.ACTIVE))
        assertThat(questService.questsProperty(), Matchers.contains(quest))

        questService.stopQuest(quest)
        assertThat(quest.state, `is`(QuestState.NOT_STARTED))
        assertTrue(questService.questsProperty().isEmpty())

        questService.startQuest(quest)
        assertThat(quest.state, `is`(QuestState.ACTIVE))
        assertThat(questService.questsProperty(), Matchers.contains(quest))

        questService.stopAllQuests()
        assertThat(quest.state, `is`(QuestState.NOT_STARTED))
        assertTrue(questService.questsProperty().isEmpty())
    }
}