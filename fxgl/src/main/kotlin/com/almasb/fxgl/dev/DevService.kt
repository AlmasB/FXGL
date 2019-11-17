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
import com.almasb.fxgl.physics.BoxShapeData
import com.almasb.fxgl.physics.ChainShapeData
import com.almasb.fxgl.physics.CircleShapeData
import com.almasb.fxgl.physics.PolygonShapeData
import com.almasb.sslogger.Logger
import com.almasb.sslogger.LoggerLevel
import com.almasb.sslogger.LoggerOutput
import javafx.scene.Group
import javafx.scene.shape.*

/**
 * TODO: all dev calls should point to dev service
 * TODO: ensure that calls are still valid and noop when app mode is release
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class DevService : EngineService {

    private val console by lazy { Console() }

    val isConsoleOpen: Boolean
        get() = console.isOpen()

    private val consoleOutput = object : LoggerOutput {
        override fun append(message: String) {
            console.pushMessage(message)
        }

        override fun close() { }
    }

    fun openConsole() {
        console.open()
    }

    fun closeConsole() {
        console.close()
    }

    override fun onMainLoopStarting() {
        Logger.addOutput(consoleOutput, LoggerLevel.DEBUG)

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

            val shape: Shape = when (val data = it.shape) {
                is CircleShapeData -> {
                    Circle(data.radius, data.radius, data.radius)
                }

                is BoxShapeData -> {
                    val bboxView = Rectangle()

                    bboxView.widthProperty().value = data.width
                    bboxView.heightProperty().value = data.height
                    bboxView.visibleProperty().bind(
                            bboxView.widthProperty().greaterThan(0).and(bboxView.heightProperty().greaterThan(0))
                    )

                    bboxView
                }

                is PolygonShapeData -> {
                    Polygon(*data.points.flatMap { listOf(it.x, it.y) }.toDoubleArray())
                }

                is ChainShapeData -> {
                    Polyline(*data.points.flatMap { listOf(it.x, it.y) }.toDoubleArray())
                }
            }

            shape.fill = null
            shape.translateX = it.minX
            shape.translateY = it.minY

            shape.strokeWidth = 2.0
            shape.strokeProperty().bind(FXGL.getSettings().devBBoxColor)

            group.children += shape
        }

        entity.viewComponent.addChild(group)

        val view = GameView(group, Int.MAX_VALUE)

        debugViews[entity] = view
    }

    private fun removeDebugView(entity: Entity) {
        debugViews.remove(entity)?.let { view ->
            entity.viewComponent.removeChild(view.node)
        }
    }
}