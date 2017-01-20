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

package com.almasb.fxgl.scene.lighting

import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.entity.Entities
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.effect.BlendMode
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import java.util.*

/**
 * API INCOMPLETE
 *
 * Adapted from https://github.com/timyates/ShadowFX
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LightingSystem {

    private lateinit var shadow: Shape
    private lateinit var gameRoot: Group

    //private val rays = ArrayList<Shape>()

    private val obstaclePoints = HashSet<Point2D>()
    private val obstacleSegments = HashSet<Segment>()

    private val objectPoints = arrayListOf<Point2D>()
    private val objectSegments = arrayListOf<Segment>()

    // TODO: do game root objects count as walls?
    private val walls = Pane()
    private val light = Pane()

    fun init(lightRoot: Group, gameRoot: Group) {
        this.gameRoot = gameRoot

        shadow = Rectangle(FXGL.getApp().width, FXGL.getApp().height, Color.color(0.0, 0.0, 0.0, 0.5))
        shadow.blendMode = BlendMode.DARKEN

        lightRoot.children.addAll(walls, shadow, light)

        addObstacle(Point2D(0.0, 0.0),
                Point2D(FXGL.getApp().width, 0.0),
                Point2D(FXGL.getApp().width, FXGL.getApp().height),
                Point2D(0.0, FXGL.getApp().height))
    }

    fun addObstacle(node: Node) {
        //gameScene.addGameView(EntityView(node, RenderLayer.LIGHTING_OBSTACLES))
        walls.children.add(node)
    }

    var f = 0

    fun addObstacle(vararg points: Point2D) {
        if (points.size < 3)
            throw IllegalArgumentException("Cannot create a polygon with < 3 points")

        obstaclePoints.addAll(points)

        val segments = points.zip(points.drop(1).plus(points[0]))
                .map { Segment(it.first, it.second) }

        obstacleSegments.addAll(segments)

        // TODO: remove this
        if (f == 0) {
            f++
            return
        }

        val polygon = Polygon()
        points.forEach {
            polygon.points.add(it.x)
            polygon.points.add(it.y)
        }

        addObstacle(polygon)
    }

    fun onAddEntity(entity: Entity) {
        if (!entity.hasComponent(com.almasb.fxgl.entity.component.BoundingBoxComponent::class.java))
            return

        val points = arrayListOf(entity.topLeft(), entity.topRight(), entity.botRight(), entity.botLeft())

        objectPoints.addAll(points)

        var i = 0
        while (i < points.size) {
            objectSegments.add(Segment(points[i], points[i + 1]))
            objectSegments.add(Segment(points[i + 1], points[i + 2]))
            objectSegments.add(Segment(points[i + 2], points[i + 3]))
            objectSegments.add(Segment(points[i + 3], points[i]))
            i += 4
        }
    }

    fun update() {
        renderRays()
    }

    private fun renderRay(mp: Point2D, points: Set<Point2D>, segmentSet: Set<Segment>): Polygon {
        val lightPoly = Polygon()

        points.map({ p -> Math.atan2(p.y - mp.y, p.x - mp.x) })
                .flatMap({ a -> arrayListOf(a - 0.0001, a, a + 0.0001) })
                .sorted()
                .map({ a ->
                    val s = Segment(mp, Point2D(mp.x + Math.cos(a), mp.y + Math.sin(a)))

                    return@map segmentSet.map({ ss -> Intersection.intersect(s, ss) })
                            .minWith(Comparator { o1, o2 -> java.lang.Double.compare(o1.distance, o2.distance) })
                })
                .filter({ i -> i !== Intersection.NONE })
                .map { it!!.point }
                .forEach {
                    lightPoly.points.add(it.x)
                    lightPoly.points.add(it.y)
                }

        lightPoly.fill = RadialGradient(0.0, 0.0, mp.x, mp.y,
                700.0, false, CycleMethod.NO_CYCLE,
                Stop(0.0, Color.valueOf("#FFFFFFFF")),
                Stop(1.0, Color.valueOf("#33333300")))

        lightPoly.blendMode = BlendMode.SOFT_LIGHT

        return lightPoly
    }

    fun renderRays() {
        light.children.clear()

        val rays = arrayListOf<Shape>()

        val mx = FXGL.getInput().mouseXUI
        val my = FXGL.getInput().mouseYUI

        for (i in 0..7) {
            val beam = renderRay(Point2D(mx + Math.cos(i / 8.0 * (Math.PI * 2.0)) * 7.0,
                    my + Math.sin(i / 8.0 * (Math.PI * 2.0)) * 7.0), obstaclePoints, obstacleSegments)
            rays.add(beam)
        }

        val clip = rays.reduce { shape1, shape2 -> Shape.union(shape1, shape2) }


        gameRoot.clip = clip
        //walls.clip = Shape.subtract(walls.children.map { it as Shape }.reduce { shape1, shape2 -> Shape.union(shape1, shape2) }, clip)

        rays.clear()

        val combinedPoints = HashSet(obstaclePoints)
        combinedPoints.addAll(objectPoints)

        val combinedSegments = HashSet(obstacleSegments)
        combinedSegments.addAll(objectSegments)

        for (i in 0..7) {
            val beam = renderRay(Point2D(mx + Math.cos(i / 8.0 * (Math.PI * 2.0)) * 7.0,
                    my + Math.sin(i / 8.0 * (Math.PI * 2.0)) * 7.0), combinedPoints, combinedSegments)
            rays.add(beam)
        }



        //val vision = rays.reduce { shape1, shape2 -> Shape.union(shape1, shape2) }


        //gameRoot.clip = vision
        //gameRoot.clip = Shape.subtract(clip, largeClip)

        //shadow.clip = Shape.subtract(shadow, largeClip)
        //shadow.clip = Shape.subtract(shadow, Shape.intersect(Circle(mx, my, 75.0), largeClip))
                //Shape.union(Shape.subtract(shadow, Circle(mx, my, 75.0)), largeClip)

        //gameRoot.clip = Shape.



        light.children.addAll(rays)
    }
}

fun Entity.topLeft(): Point2D {
    return Point2D(Entities.getBBox(this).minXWorld, Entities.getBBox(this).minYWorld)
}

fun Entity.topRight(): Point2D {
    return Point2D(Entities.getBBox(this).maxXWorld, Entities.getBBox(this).minYWorld)
}

fun Entity.botRight(): Point2D {
    return Point2D(Entities.getBBox(this).maxXWorld, Entities.getBBox(this).maxYWorld)
}

fun Entity.botLeft(): Point2D {
    return Point2D(Entities.getBBox(this).minXWorld, Entities.getBBox(this).maxYWorld)
}