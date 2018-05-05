/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.devtools

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.Required
import com.almasb.fxgl.entity.components.PositionComponent
import com.almasb.fxgl.input.UserAction
import javafx.scene.input.KeyCode

/**
 * Uses numpad 8456 to replicate WASD for easier testing.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(PositionComponent::class)
class DeveloperWASDControl : Component() {

    private lateinit var position: PositionComponent

    override fun onAdded() {
        with(FXGL.getInput()) {
            addAction(object : UserAction("Dev_UP") {
                override fun onAction() = up()
            }, KeyCode.NUMPAD8)

            addAction(object : UserAction("Dev_DOWN") {
                override fun onAction() = down()
            }, KeyCode.NUMPAD5)

            addAction(object : UserAction("Dev_LEFT") {
                override fun onAction() = left()
            }, KeyCode.NUMPAD4)

            addAction(object : UserAction("Dev_RIGHT") {
                override fun onAction() = right()
            }, KeyCode.NUMPAD6)
        }
    }

    private var speed = 0.0

    override fun onUpdate(tpf: Double) {
        speed = tpf * 60
    }

    fun up() {
        position.translateY(-5 * speed)
    }

    fun down() {
        position.translateY(5 * speed)
    }

    fun left() {
        position.translateX(-5 * speed)
    }

    fun right() {
        position.translateX(5 * speed)
    }
}