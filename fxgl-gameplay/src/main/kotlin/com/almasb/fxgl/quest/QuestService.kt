/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.quest

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.collection.PropertyMap
import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 * Keeps track of quests, allows adding, removing and starting quests.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestService : EngineService() {

    private val quests = FXCollections.observableArrayList<Quest>()
    private val unmodifiableQuests = FXCollections.unmodifiableObservableList(quests)

    private lateinit var vars: PropertyMap

    /**
     * @return unmodifiable list of currently tracked quests
     */
    fun questsProperty(): ObservableList<Quest> = unmodifiableQuests

    /**
     * Add a quest to be tracked by the service.
     */
    fun addQuest(quest: Quest) {
        quests.add(quest)

        bindToVars(quest)
    }

    /**
     * Remove a quest from being tracked by the service.
     */
    fun removeQuest(quest: Quest) {
        quests.remove(quest)

        quest.objectives.forEach { it.unbind() }
    }

    /**
     * Start given quest. Will automatically track it.
     */
    fun startQuest(quest: Quest) {
        addQuest(quest)

        quest.start()
    }

    override fun onGameReady(vars: PropertyMap) {
        this.vars = vars

        // TODO: only bind to vars those that are active
        quests.forEach {
            bindToVars(it)
        }
    }

    private fun bindToVars(quest: Quest) {
        quest.objectives.forEach {
            it.unbind()
            it.bindTo(vars)
        }
    }
}