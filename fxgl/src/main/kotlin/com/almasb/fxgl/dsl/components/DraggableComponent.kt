/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.component.Component
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

/**
 * Allows entities to be dragged using mouse input.
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
        offsetX = FXGL.getInput().mouseXWorld - entity.x
        offsetY = FXGL.getInput().mouseYWorld - entity.y
    }

    private val onRelease = EventHandler<MouseEvent> { isDragging = false }

    override fun onAdded() {
        entity.viewComponent.addEventHandler(MouseEvent.MOUSE_PRESSED, onPress)
        entity.viewComponent.addEventHandler(MouseEvent.MOUSE_RELEASED, onRelease)
    }

    override fun onUpdate(tpf: Double) {
        if (!isDragging)
            return

        entity.setPosition(FXGL.getInput().mouseXWorld - offsetX, FXGL.getInput().mouseYWorld - offsetY)
    }

    override fun onRemoved() {
        entity.viewComponent.removeEventHandler(MouseEvent.MOUSE_PRESSED, onPress)
        entity.viewComponent.removeEventHandler(MouseEvent.MOUSE_RELEASED, onRelease)
    }
}