/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.rpg.quest

import com.almasb.fxgl.app.FXGL
import javafx.collections.ListChangeListener
import javafx.scene.layout.VBox

/**
 * Convenient pane that contains quests in a vertical layout.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestPane(width: Double, height: Double) : VBox() {

    private val changeListener = ListChangeListener<Quest> { c ->
        while (c.next()) {

            if (c.wasAdded()) {
                c.addedSubList.forEach { children.add(QuestView(it, prefWidth)) }
            } else if (c.wasRemoved()) {
                c.removed.map { getView(it) }.forEach { children.remove(it) }
            }
        }
    }

    init {
        prefWidth = width
        prefHeight = height

        children.addAll(FXGL.getApp().gameplay.questManager.questsProperty().map { QuestView(it, prefWidth) })

        FXGL.getApp().gameplay.questManager.questsProperty().addListener(changeListener)
    }

    private fun getView(quest: Quest) = children.map { it as QuestView }.filter { it.quest === quest }.firstOrNull()
}