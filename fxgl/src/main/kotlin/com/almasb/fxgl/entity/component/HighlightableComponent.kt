/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.ecs.Component
import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.ecs.component.Required
import com.almasb.fxgl.entity.Entities
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import jfxtras.util.NodeUtil

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(ViewComponent::class)
class HighlightableComponent : Component() {

    companion object {
        private val SIZE = 15.0

        private class HighlightView : Parent() {

            private val animations = arrayListOf<Animation<*>>()

            fun startForView(view: Node) {
                children.clear()

                val light = Rectangle(view.layoutBounds.maxX, view.layoutBounds.maxY, null)
                with(light) {
                    translateX = -1.0
                    translateY = -1.0
                    stroke = Color.YELLOW
                    strokeWidth = 2.0
                }


                children.add(light)

//                animations.forEach { it.stop() }
//                animations.clear()
//
//                children.addAll(
//                        makeParticle((-SIZE), (-SIZE)),
//                        makeParticle(view.layoutBounds.maxX, (-SIZE)),
//                        makeParticle(view.layoutBounds.maxX, view.layoutBounds.maxY),
//                        makeParticle((-SIZE), view.layoutBounds.maxY)
//                )
//
//                for (i in 0..children.size-1) {
//                    val animation = FXGL.getUIFactory()
//                            .translate(children[i],
//                                    Point2D(children[if (i == children.size-1) 0 else i + 1].translateX, children[if (i == children.size-1) 0 else i + 1].translateY),
//                                    Duration.seconds(1.0))
//
//                    animation.cycleCount = Integer.MAX_VALUE
//                    animations.add(animation)
//                    animation.startInPlayState()
//                }
            }

            private fun makeParticle(x: Double, y: Double): Node {
                val particle = FXGL.getAssetLoader().loadTexture("highlight_particle.png", SIZE, SIZE).multiplyColor(Color.DARKRED)
                particle.translateX = x
                particle.translateY = y
                return particle
            }
        }

        private val HIGHLIGHT = HighlightView()
    }

    override fun onAdded(entity: Entity) {
        val view = Entities.getView(entity)

        view.view.setOnMouseEntered {
            if (HIGHLIGHT.scene != null) {
                NodeUtil.removeFromParent(HIGHLIGHT)
            }

            HIGHLIGHT.startForView(view.view)
            view.view.addNode(HIGHLIGHT)
        }

        view.view.setOnMouseExited {
            view.view.removeNode(HIGHLIGHT)
        }
    }
}