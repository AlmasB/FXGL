/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.components.BoundingBoxComponent
import com.almasb.fxgl.physics.box2d.collision.shapes.*
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D

/**
 * Defines bounding shapes to be used for hit boxes in local coord system.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
sealed class BoundingShape(val size: Dimension2D) {

    abstract fun toBox2DShape(box: HitBox, bboxComp: BoundingBoxComponent, conv: PhysicsUnitConverter): Shape

    override fun toString(): String = javaClass.simpleName.substringBefore("ShapeData")

    companion object {

        /**
         * Constructs new circular bounding shape with given radius.
         *
         * @param radius circle radius
         * @return circular bounding shape
         */
        @JvmStatic fun circle(radius: Double): BoundingShape {
            return CircleShapeData(radius)
        }

        /**
         * Constructs new rectangular bounding shape with given width and height.
         *
         * @param width box width
         * @param height box height
         * @return rectangular bounding shape
         */
        @JvmStatic fun box(width: Double, height: Double): BoundingShape {
            return BoxShapeData(width, height)
        }

        /**
         * Constructs new chain shaped bounding shape.
         * Note: chain shape can only be used with static objects.
         * Note: chain shape must have at least 2 points
         *
         * @param points points to use in a chain
         * @return closed chain bounding shape
         * @throws IllegalArgumentException if number of points is less than 2
         */
        @JvmStatic fun chain(vararg points: Point2D): BoundingShape {
            if (points.size < 2)
                throw IllegalArgumentException("Chain shape requires at least 2 points. Given points: " + points.size)

            var maxX = points[0].x
            var maxY = points[0].y

            for (p in points) {
                if (p.x > maxX) {
                    maxX = p.x
                }

                if (p.y > maxY) {
                    maxY = p.y
                }
            }

            return ChainShapeData(Dimension2D(maxX, maxY), points as Array<Point2D>)
        }

        @JvmStatic fun polygon(points: List<Point2D>): BoundingShape {
            return polygon(*points.toTypedArray())
        }

        @JvmStatic fun polygonFromDoubles(points: List<Double>): BoundingShape {
            val array = DoubleArray(points.size)

            for (i in points.indices) {
                array[i] = points[i]
            }

            return polygon(*points.toDoubleArray())
        }

        @JvmStatic fun polygon(vararg points: Double): BoundingShape {
            val array = Array<Point2D>(points.size / 2) { Point2D.ZERO }
            for (i in array.indices) {
                val x = points[i * 2]
                val y = points[i * 2 + 1]

                array[i] = Point2D(x, y)
            }

            return polygon(*array)
        }

        @JvmStatic fun polygon(vararg points: Point2D): BoundingShape {
            if (points.size < 3)
                throw IllegalArgumentException("Polygon shape requires at least 3 points. Given points: " + points.size)

            var maxX = points[0].x
            var maxY = points[0].y

            for (p in points) {
                if (p.x > maxX) {
                    maxX = p.x
                }

                if (p.y > maxY) {
                    maxY = p.y
                }
            }

            return PolygonShapeData(Dimension2D(maxX, maxY), points as Array<Point2D>)
        }
    }
}

class CircleShapeData(val radius: Double) : BoundingShape(Dimension2D(radius * 2, radius * 2)) {

    override fun toBox2DShape(box: HitBox, bboxComp: BoundingBoxComponent, conv: PhysicsUnitConverter): Shape {
        // take world center bounds and subtract from entity center (all in pixels) to get local center
        // because box2d operates on vector offsets from the body center, also in local coordinates
        val boundsCenterLocal = box.centerWorld.subtract(bboxComp.centerWorld)

        val shape = CircleShape()
        shape.radius = conv.toMetersF(box.width / 2.0)
        shape.center.set(conv.toVector(boundsCenterLocal))

        return shape
    }
}

class BoxShapeData(val width: Double, val height: Double) : BoundingShape(Dimension2D(width, height)) {

    override fun toBox2DShape(box: HitBox, bboxComp: BoundingBoxComponent, conv: PhysicsUnitConverter): Shape {
        val boundsCenterLocal = box.centerWorld.subtract(bboxComp.centerWorld)

        val shape = PolygonShape()
        shape.setAsBox(conv.toMetersF(box.width / 2), conv.toMetersF(box.height / 2), conv.toVector(boundsCenterLocal), 0f)

        return shape
    }
}

class PolygonShapeData(size: Dimension2D, val points: Array<Point2D>) : BoundingShape(size) {

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
class ChainShapeData(size: Dimension2D, val points: Array<Point2D>) : BoundingShape(size) {

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