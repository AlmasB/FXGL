/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.devtools

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.ecs.AbstractControl
import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.ecs.component.Required
import com.almasb.fxgl.entity.Entities
import com.almasb.fxgl.entity.component.PositionComponent
import com.almasb.fxgl.input.UserAction
import javafx.scene.input.KeyCode

/**
 * Uses numpad 8456 to replicate WASD for easier testing.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(PositionComponent::class)
class DeveloperWASDControl : AbstractControl() {

    private lateinit var position: PositionComponent

    override fun onAdded(entity: Entity) {
        position = Entities.getPosition(entity)

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

    override fun onUpdate(entity: Entity, tpf: Double) {
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