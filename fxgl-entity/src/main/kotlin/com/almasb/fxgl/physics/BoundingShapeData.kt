/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.components.BoundingBoxComponent
import com.almasb.fxgl.physics.box2d.collision.shapes.ChainShape
import com.almasb.fxgl.physics.box2d.collision.shapes.CircleShape
import com.almasb.fxgl.physics.box2d.collision.shapes.PolygonShape
import com.almasb.fxgl.physics.box2d.collision.shapes.Shape
import javafx.geometry.Point2D

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
sealed class BoundingShapeData {

    abstract fun toBox2DShape(box: HitBox, bboxComp: BoundingBoxComponent, conv: PhysicsUnitConverter): Shape

    override fun toString(): String = javaClass.simpleName.substringBefore("ShapeData")
}

class CircleShapeData(val radius: Double) : BoundingShapeData() {

    override fun toBox2DShape(box: HitBox, bboxComp: BoundingBoxComponent, conv: PhysicsUnitConverter): Shape {
        // take world center bounds and subtract from entity center (all in pixels) to get local center
        // because box2d operates on vector offsets from the body center, also in local coordinates
        val boundsCenterLocal = box.centerWorld.subtract(bboxComp.centerWorld)

        val shape = CircleShape()
        shape.radius = conv.toMetersF(radius)
        shape.center.set(conv.toVector(boundsCenterLocal))

        return shape
    }
}

class BoxShapeData(val width: Double, val height: Double) : BoundingShapeData() {

    override fun toBox2DShape(box: HitBox, bboxComp: BoundingBoxComponent, conv: PhysicsUnitConverter): Shape {
        val boundsCenterLocal = box.centerWorld.subtract(bboxComp.centerWorld)

        val shape = PolygonShape()
        shape.setAsBox(conv.toMetersF(width / 2), conv.toMetersF(height / 2), conv.toVector(boundsCenterLocal), 0f)

        return shape
    }
}

class PolygonShapeData(val points: Array<Point2D>) : BoundingShapeData() {

    override fun toBox2DShape(box: HitBox, bboxComp: BoundingBoxComponent, conv: PhysicsUnitConverter): Shape {
        val boundsCenterLocal = box.centerWorld.subtract(bboxComp.centerWorld)
        val bboxCenterLocal = bboxComp.centerLocal
        val t = bboxComp.transform

        val vertices = arrayOfNulls<Vec2>(points.size)

        val bboxCenterLocalNew = Point2D(
                bboxCenterLocal.x * t.scaleX + (1 - t.scaleX) * t.scaleOrigin.x,
                bboxCenterLocal.y * t.scaleY + (1 - t.scaleY) * t.scaleOrigin.y
        )

        val boundsCenterLocalNew = Point2D(
                boundsCenterLocal.x * t.scaleX + (1 - t.scaleX) * t.scaleOrigin.x,
                boundsCenterLocal.y * t.scaleY + (1 - t.scaleY) * t.scaleOrigin.y
        )

        for (i in vertices.indices) {

            val p = Point2D(
                    (points[i].x + box.minX) * t.scaleX + (1 - t.scaleX) * t.scaleOrigin.x,
                    (points[i].y + box.minY) * t.scaleY + (1 - t.scaleY) * t.scaleOrigin.y
            )

            vertices[i] = conv.toVector(p.subtract(boundsCenterLocalNew))
                    .subLocal(conv.toVector(bboxCenterLocalNew))
                    .addLocal(conv.toVector(boundsCenterLocalNew))
                    .subLocal(conv.toMeters(bboxComp.getMinXLocal()), -conv.toMeters(bboxComp.getMinYLocal()))
        }

        val shape = PolygonShape()
        shape.set(vertices)

        return shape
    }
}

/**
 * Effectively, a polyline.
 */
class ChainShapeData(val points: Array<Point2D>) : BoundingShapeData() {

    override fun toBox2DShape(box: HitBox, bboxComp: BoundingBoxComponent, conv: PhysicsUnitConverter): Shape {
        val boundsCenterLocal = box.centerWorld.subtract(bboxComp.centerWorld)

        val vertices = arrayOfNulls<Vec2>(points.size)

        for (i in vertices.indices) {
            vertices[i] = conv.toVector(points[i].subtract(boundsCenterLocal)).subLocal(conv.toVector(bboxComp.centerLocal))
        }

        val shape = ChainShape()
        shape.createLoop(vertices, vertices.size)

        return shape
    }
}