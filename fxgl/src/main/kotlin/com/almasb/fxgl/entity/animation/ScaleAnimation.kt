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

package com.almasb.fxgl.entity.animation

import javafx.animation.Animation
import javafx.animation.ScaleTransition
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.shape.Rectangle

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ScaleAnimation(animationBuilder: AnimationBuilder,
                     val startScale: Point2D, val endScale: Point2D) : EntityAnimation(animationBuilder) {

    private lateinit var node: Node

    init {
        initAnimation()
    }

    override fun buildAnimation(): Animation {
        node = Rectangle()

        val anim = ScaleTransition(animationBuilder.duration, node)
        anim.fromX = startScale.x
        anim.fromY = startScale.y
        anim.toX = endScale.x
        anim.toY = endScale.y

        return anim
    }

    override fun bindProperties() {
        animationBuilder.entities.map { it.mainViewComponent }.forEach {
            it.view.scaleXProperty().bind(node.scaleXProperty())
            it.view.scaleYProperty().bind(node.scaleYProperty())
        }
    }

    override fun unbindProperties() {
        animationBuilder.entities.map { it.mainViewComponent }.forEach {
            it.view.scaleXProperty().unbind()
            it.view.scaleYProperty().unbind()
        }
    }
}