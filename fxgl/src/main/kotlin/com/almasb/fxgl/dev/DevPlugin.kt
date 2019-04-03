/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dev

import com.almasb.fxgl.app.GameView
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityWorldListener
import javafx.scene.Group
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle

/**
 * TODO: engine-plugin architecture?
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DevPlugin {

    private val scene = FXGL.getGameScene()

    private val debugViews = hashMapOf<Entity, GameView>()

    fun activate() {
        FXGL.getSettings().devShowBBox.addListener { _, _, isSelected ->
            if (isSelected) {
                FXGL.getGameWorld().entities.forEach {
                    addDebugView(it)
                }
            } else {
                FXGL.getGameWorld().entities.forEach {
                    removeDebugView(it)
                }
            }
        }

        FXGL.getGameWorld().addWorldListener(object : EntityWorldListener {
            override fun onEntityAdded(entity: Entity) {
                if (FXGL.getSettings().devShowBBox.value) {
                    addDebugView(entity)
                }
            }

            override fun onEntityRemoved(entity: Entity) {
                if (FXGL.getSettings().devShowBBox.value) {
                    removeDebugView(entity)
                }
            }
        })
    }

    private fun addDebugView(entity: Entity) {
        val group = Group()

        entity.boundingBoxComponent.hitBoxesProperty().forEach {

            // TODO: add polygon
            if (it.shape.isCircle) {

                val bboxView = Circle(it.width / 2, it.width / 2, it.width / 2)
                bboxView.fill = null

                bboxView.translateX = it.minX
                bboxView.translateY = it.minY

                bboxView.strokeWidth = 2.0
                bboxView.strokeProperty().bind(FXGL.getSettings().devBBoxColor)

                group.children += bboxView

            } else if (it.shape.isRectangle) {
                val bboxView = Rectangle()
                bboxView.fill = null

                bboxView.translateX = it.minX
                bboxView.translateY = it.minY

                bboxView.strokeWidth = 2.0
                bboxView.strokeProperty().bind(FXGL.getSettings().devBBoxColor)

                bboxView.widthProperty().value = it.width
                bboxView.heightProperty().value = it.height
                bboxView.visibleProperty().bind(
                        bboxView.widthProperty().greaterThan(0).and(bboxView.heightProperty().greaterThan(0))
                )

                group.children += bboxView
            }



        }

        entity.viewComponent.parent.children += group

        val view = GameView(group, Int.MAX_VALUE)

        debugViews[entity] = view
    }

    private fun removeDebugView(entity: Entity) {
        val view = debugViews.remove(entity)!!

        entity.viewComponent.parent.children.remove(view.node)
    }
}