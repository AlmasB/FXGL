/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.logging.Logger
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.geometry.Point2D

/**
 * This component moves the entity using provided Point2D waypoints.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class WaypointMoveComponent(
        var speed: Double,
        waypoints: List<Point2D>
) : Component() {

    private val log = Logger.get<WaypointMoveComponent>()

    private var points: MutableList<Point2D> = arrayListOf()
    private var nextPoint: Point2D = Point2D.ZERO

    private val isAtDestinationProp = ReadOnlyBooleanWrapper(true)

    private var isAllowRotation = false

    init {
        move(waypoints)
    }

    fun atDestinationProperty(): ReadOnlyBooleanProperty = isAtDestinationProp.readOnlyProperty

    /**
     * Allows enabling or disabling rotation towards the current movement direction.
     */
    fun allowRotation(allowRotation: Boolean): WaypointMoveComponent {
        isAllowRotation = allowRotation
        return this
    }

    fun move(waypoints: List<Point2D>) {
        points.clear()
        points.addAll(waypoints)

        if (points.isEmpty()) {
            log.warning("No waypoints given. Using Point2D.ZERO as dummy")
            points.add(Point2D.ZERO)
        }

        nextPoint = points.removeAt(0)

        isAtDestinationProp.value = false
    }

    override fun onAdded() {
        updateRotation()
    }

    override fun onUpdate(tpf: Double) {
        if (isAtDestinationProp.value)
            return

        val dist = tpf * speed

        if (nextPoint.distance(entity.anchoredPosition) <= dist) {
            entity.anchoredPosition = nextPoint

            if (points.isNotEmpty()) {
                nextPoint = points.removeAt(0)
                updateRotation()
            } else {
                isAtDestinationProp.value = true
            }
        } else {
            updateRotation()
            entity.translateTowards(nextPoint, dist)
        }
    }

    private fun updateRotation() {
        if (!isAllowRotation)
            return

        val movementVector = nextPoint.subtract(entity.anchoredPosition)

        if (movementVector.magnitude() > 0.0) {
            entity.rotateToVector(movementVector)
        }
    }

    override fun isComponentInjectionRequired(): Boolean = false
}