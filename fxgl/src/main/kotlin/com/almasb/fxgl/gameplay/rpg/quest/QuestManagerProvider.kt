/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.rpg.quest

import com.almasb.fxgl.saving.UserProfile
import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 * Keeps track of quests, allows addition and removal.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class QuestManagerProvider : QuestManager {

    private val quests = FXCollections.observableArrayList<Quest>()
    private val unmodifiableQuests = FXCollections.unmodifiableObservableList(quests)

    override fun questsProperty(): ObservableList<Quest> = unmodifiableQuests

    override fun addQuest(quest: Quest) {
        quests.add(quest)
    }

    override fun removeQuest(quest: Quest) {
        quests.remove(quest)
    }

    override fun save(profile: UserProfile) {
    }

    override fun load(profile: UserProfile) {
    }
}