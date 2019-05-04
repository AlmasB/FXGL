/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dev

import com.almasb.fxgl.app.GameView
import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityWorldListener
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DevService : EngineService {

    override fun onMainLoopStarting() {
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

    override fun onExit() {
    }

    override fun onUpdate(tpf: Double) {
    }

    override fun onGameReady(vars: PropertyMap) {
    }

    override fun write(bundle: Bundle) {
    }

    override fun read(bundle: Bundle) {
    }

    private val debugViews = hashMapOf<Entity, GameView>()

    private fun addDebugView(entity: Entity) {
        val group = Group()

        entity.boundingBoxComponent.hitBoxesProperty().forEach {


            if (it.shape.isCircle) {

                val bboxView = Circle(it.width / 2, it.width / 2, it.width / 2)
                bboxView.fill = null

                bboxView.translateX = it.minX
                bboxView.translateY = it.minY

                bboxView.strokeWidth = 2.0
                bboxView.strokeProperty().bind(FXGL.getSettings().devBBoxColor)

                group.children += bboxView

            } else if (it.shape.isPolygon) {

                val data = it.shape.data

                // TODO: clean up
                if (data is Dimension2D) {
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
                } else {
                    val polygonPoints = data as Array<Point2D>

                    val polygonView = Polygon(*polygonPoints.flatMap { listOf(it.x, it.y) }.toDoubleArray())

                    polygonView.translateX = it.minX
                    polygonView.translateY = it.minY

                    polygonView.fill = null
                    polygonView.strokeWidth = 2.0
                    polygonView.strokeProperty().bind(FXGL.getSettings().devBBoxColor)

                    group.children += polygonView
                }
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