/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics


import com.almasb.fxgl.entity.components.BoundingBoxComponent
import com.almasb.fxgl.physics.box2d.collision.shapes.Shape
import com.almasb.fxgl.physics.box2d.collision.shapes.ShapeType
import javafx.geometry.Dimension2D
import javafx.geometry.Point2D

/**
 * Defines bounding shapes to be used for hit boxes.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class BoundingShape private constructor(
        internal val type: ShapeType,

        /**
         * The data is in local coord system.
         */
        internal val data: BoundingShapeData,

        val size: Dimension2D) {

    /**
     * @return true if the type of this shape is a circle
     */
    val isCircle: Boolean
        get() = data is CircleShapeData

    val isBox: Boolean
        get() = data is BoxShapeData

    /**
     * @return true if the type of this shape is a polygon
     */
    val isPolygon: Boolean
        get() = data is PolygonShapeData

    val isChain: Boolean
        get() = data is ChainShapeData

    fun toBox2DShape(box: HitBox, bbox: BoundingBoxComponent, converter: PhysicsUnitConverter): Shape {
        return data.toBox2DShape(box, bbox, converter)
    }

    companion object {

        /**
         * Constructs new circular bounding shape with given radius.
         *
         * @param radius circle radius
         * @return circular bounding shape
         */
        @JvmStatic fun circle(radius: Double): BoundingShape {
            return BoundingShape(ShapeType.CIRCLE, CircleShapeData(radius), Dimension2D(radius*2, radius*2))
        }

        /**
         * Constructs new rectangular bounding shape with given width and height.
         *
         * @param width box width
         * @param height box height
         * @return rectangular bounding shape
         */
        @JvmStatic fun box(width: Double, height: Double): BoundingShape {
            return BoundingShape(ShapeType.POLYGON, BoxShapeData(width, height), Dimension2D(width, height))
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

            return BoundingShape(ShapeType.CHAIN, ChainShapeData(points as Array<Point2D>), Dimension2D(maxX, maxY))
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

            return BoundingShape(ShapeType.POLYGON, PolygonShapeData(points as Array<Point2D>), Dimension2D(maxX, maxY))
        }
    }

    override fun toString(): String {
        return type.toString()
    }
}
