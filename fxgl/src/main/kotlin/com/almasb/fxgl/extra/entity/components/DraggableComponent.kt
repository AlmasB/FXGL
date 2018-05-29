/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.components

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.entity.component.Component
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

/**
 * Only works on entities without PhysicsComponent.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DraggableComponent : Component() {

    var isDragging = false
        private set

    private var offsetX = 0.0
    private var offsetY = 0.0

    private val onPress = EventHandler<MouseEvent> {
        isDragging = true
        offsetX = FXGL.getInput().getMouseXWorld() - entity.x
        offsetY = FXGL.getInput().getMouseYWorld() - entity.y
    }

    private val onRelease = EventHandler<MouseEvent> { isDragging = false }

    override fun onAdded() {
        entity.view.onMousePressed = onPress
        entity.view.onMouseReleased = onRelease
    }

    override fun onUpdate(tpf: Double) {
        if (!isDragging)
            return

        entity.setPosition(FXGL.getInput().getMouseXWorld() - offsetX, FXGL.getInput().getMouseYWorld() - offsetX)
    }

    override fun onRemoved() {
        entity.view.onMousePressed = null
        entity.view.onMouseReleased = null
    }
}