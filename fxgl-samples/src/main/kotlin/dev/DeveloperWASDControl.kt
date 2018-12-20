/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.input.UserAction
import javafx.scene.input.KeyCode

/**
 * Uses numpad 8456 to replicate WASD for easier testing.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DeveloperWASDControl : Component() {

    companion object {
        var check = false
    }

    override fun onAdded() {
        if (check)
            return


        check = true

        with(FXGL.getInput()) {
            addAction(object : UserAction("Dev_UP") {
                override fun onAction() = up()
            }, KeyCode.W)

            addAction(object : UserAction("Dev_DOWN") {
                override fun onAction() = down()
            }, KeyCode.S)

            addAction(object : UserAction("Dev_LEFT") {
                override fun onAction() = left()
            }, KeyCode.A)

            addAction(object : UserAction("Dev_RIGHT") {
                override fun onAction() = right()
            }, KeyCode.D)
        }
    }

    private var speed = 0.0

    override fun onUpdate(tpf: Double) {
        speed = tpf * 60
    }

    fun up() {
        entity.translateY(-5 * speed)
    }

    fun down() {
        entity.translateY(5 * speed)
    }

    fun left() {
        entity.translateX(-5 * speed)
    }

    fun right() {
        entity.translateX(5 * speed)
    }
}