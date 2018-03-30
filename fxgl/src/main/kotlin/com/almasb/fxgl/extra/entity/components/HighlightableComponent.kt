/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.components

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.SequentialAnimation
import com.almasb.fxgl.app.translate
import com.almasb.fxgl.devtools.DeveloperTools
import com.almasb.fxgl.entity.component.Component
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.Duration

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class HighlightableComponent : Component() {

    companion object {
        private val SIZE = 2.0

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

                val particle = makeParticle(0.0, 0.0)

                children.addAll(light, particle)

                val speed = 240.0

                val dist1 = Point2D(-SIZE, -SIZE).distance(Point2D(view.layoutBounds.maxX, (-SIZE)))
                val dist2 = Point2D(view.layoutBounds.maxX, (-SIZE)).distance(Point2D(view.layoutBounds.maxX, view.layoutBounds.maxY))
                val dist3 = Point2D(view.layoutBounds.maxX, view.layoutBounds.maxY).distance(Point2D((-SIZE), view.layoutBounds.maxY))
                val dist4 = Point2D((-SIZE), view.layoutBounds.maxY).distance(Point2D(-SIZE, -SIZE))

                SequentialAnimation(1,
                                translate(particle, Point2D(-SIZE, -SIZE), Point2D(view.layoutBounds.maxX, (-SIZE)), Duration.seconds(dist1 / speed)),
                                translate(particle, Point2D(view.layoutBounds.maxX, (-SIZE)), Point2D(view.layoutBounds.maxX, view.layoutBounds.maxY), Duration.seconds(dist2 / speed)),
                                translate(particle, Point2D(view.layoutBounds.maxX, view.layoutBounds.maxY), Point2D((-SIZE), view.layoutBounds.maxY), Duration.seconds(dist3 / speed)),
                                translate(particle, Point2D((-SIZE), view.layoutBounds.maxY), Point2D(-SIZE, -SIZE), Duration.seconds(dist4 / speed))

                ).startInPlayState()

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
                //val particle = FXGL.getAssetLoader().loadTexture("highlight_particle.png", SIZE, SIZE).multiplyColor(Color.DARKRED)
                val particle = Rectangle(SIZE, SIZE, Color.color(0.7, 0.5, 0.3, 0.75))
                particle.translateX = x
                particle.translateY = y
                return particle
            }
        }

        private val HIGHLIGHT = HighlightView()
    }

    override fun onAdded() {
        val view = entity.viewComponent

        view.view.setOnMouseEntered {
            if (HIGHLIGHT.scene != null) {
                DeveloperTools.removeFromParent(HIGHLIGHT)
            }

            HIGHLIGHT.startForView(view.view)
            view.view.addNode(HIGHLIGHT)
        }

        view.view.setOnMouseExited {
            view.view.removeNode(HIGHLIGHT)
        }
    }
}