/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.rpg.quest

import javafx.animation.FillTransition
import javafx.animation.ParallelTransition
import javafx.animation.RotateTransition
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.Duration

/**
 * A check box with three states.
 * Cannot be activated from UI.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestCheckBox : Rectangle(18.0, 18.0, Color.TRANSPARENT) {

    private val state = SimpleObjectProperty<QuestState>(QuestState.ACTIVE)

    init {
        arcWidth = 12.0
        arcHeight = 12.0
        stroke = Color.WHITESMOKE
        strokeWidth = 1.0

        state.addListener { o, oldState, newState ->
            val fill = FillTransition(Duration.seconds(0.35), this, fill as Color, newState.color)
            val rotation = RotateTransition(Duration.seconds(0.35), this)
            rotation.byAngle = 180.0

            ParallelTransition(fill, rotation).play()
        }
    }

    fun stateProperty() = state

    fun getState() = state.get()

    fun setState(state: QuestState) {
        this.state.set(state)
    }
}