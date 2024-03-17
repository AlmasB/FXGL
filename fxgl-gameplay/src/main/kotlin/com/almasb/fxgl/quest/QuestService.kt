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
 * Allows constructing new quests.
 * Keeps track of started (active) quests.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestService : EngineService() {

    private val quests = FXCollections.observableArrayList<Quest>()
    private val unmodifiableQuests = FXCollections.unmodifiableObservableList(quests)

    private var vars = PropertyMap()

    /**
     * @return unmodifiable list of currently tracked quests
     */
    fun questsProperty(): ObservableList<Quest> = unmodifiableQuests

    override fun onVarsInitialized(vars: PropertyMap) {
        this.vars = vars
    }

    /**
     * Constructs a new quest with given [name] and variables data [varsMap].
     * By default, the variables data is taken from the game variables.
     */
    @JvmOverloads fun newQuest(name: String, varsMap: PropertyMap = vars): Quest {
        return Quest(name, varsMap)
    }

    /**
     * Start the [quest] and adds it to tracked list.
     */
    fun startQuest(quest: Quest) {
        quests.add(quest)
        quest.start()
    }

    /**
     * Stops the [quest] and removes it from tracked list.
     */
    fun stopQuest(quest: Quest) {
        quests.remove(quest)
        quest.stop()
    }

    /**
     * Stops all quests and removes them from being tracked.
     */
    fun stopAllQuests() {
        quests.toList().forEach { stopQuest(it) }
    }

    override fun onGameUpdate(tpf: Double) {
        quests.forEach { it.onUpdate(tpf) }
    }

    override fun onGameReset() {
        stopAllQuests()
    }
}