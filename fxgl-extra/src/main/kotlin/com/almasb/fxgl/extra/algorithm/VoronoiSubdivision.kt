/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.algorithm

import com.almasb.fxgl.extra.algorithm.voronoi.GraphEdge
import com.almasb.fxgl.extra.algorithm.voronoi.Voronoi
import com.almasb.fxgl.core.collection.Array
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.core.math.FXGLMath
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.shape.Polygon
import java.util.*

/**
 * Polygonal 2D space subdivision using Voronoi diagram.
 * Note: this is not thread-safe.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object VoronoiSubdivision {

    private val log = Logger.get(javaClass)

    private lateinit var bbox: Rectangle2D
    private lateinit var corners: List<Point2D>

    @JvmStatic fun divide(rect: Rectangle2D, numSamplePoints: Int, minDistance: Double): Array<Polygon> {
        bbox = rect
        corners = listOf(
                Point2D(bbox.minX, bbox.minY),
                Point2D(bbox.maxX, bbox.minY),
                Point2D(bbox.maxX, bbox.maxY),
                Point2D(bbox.minX, bbox.maxY)
        )

        val (pointsX, pointsY) = makeRandomPoints(rect, numSamplePoints)

        val edges = Voronoi(minDistance).generateVoronoi(pointsX, pointsY, rect.minX, rect.maxX, rect.minY, rect.maxY)

        val sites = makeSites(edges)

        return Array(makePolygons(sites))
    }

    private fun makeRandomPoints(rect: Rectangle2D, size: Int): Pair<DoubleArray, DoubleArray> {
        val pointsX = DoubleArray(size)
        val pointsY = DoubleArray(size)

        for (i in 0 until size) {
            val randomX = FXGLMath.random(rect.minX, rect.maxX)
            val randomY = FXGLMath.random(rect.minY, rect.maxY)

            pointsX[i] = randomX
            pointsY[i] = randomY
        }

        return pointsX.to(pointsY)
    }

    private class Site(val edges: List<GraphEdge>)

    private fun makeSites(edges: List<GraphEdge>): List<Site> {
        val siteMap = HashMap<Int, ArrayList<GraphEdge>>()

        edges.forEach { edge ->

            // filter "point" sized edges
            if (!(FXGLMath.isClose(edge.x1, edge.x2, EDGE_TOLERANCE) && FXGLMath.isClose(edge.y1, edge.y2, EDGE_TOLERANCE))) {

                val siteEdges1 = siteMap.getOrDefault(edge.site1, arrayListOf())
                siteEdges1.add(edge)
                siteMap.put(edge.site1, siteEdges1)

                val siteEdges2 = siteMap.getOrDefault(edge.site2, arrayListOf())
                siteEdges2.add(edge)
                siteMap.put(edge.site2, siteEdges2)
            }
        }

        return siteMap.map { Site(it.value) }
    }

    private fun makePolygons(sites: List<Site>): List<Polygon> {
        return sites.map { makePolygon(it) }
    }

    private fun makePolygon(site: Site): Polygon {
        val polygon = Polygon()

        val firstEdge = site.edges[0]

        polygon.points.addAll(firstEdge.x1, firstEdge.y1, firstEdge.x2, firstEdge.y2)

        var leftEdges = site.edges.minus(firstEdge)
        var nextVertex = Point2D(firstEdge.x2, firstEdge.y2)

        while (leftEdges.isNotEmpty()) {
            val (next, edge) = nextEdge(nextVertex, leftEdges) ?: return polygon

            polygon.points.addAll(next.x, next.y)

            nextVertex = next
            leftEdges = leftEdges.minus(edge)
        }

        // another edge case where the polygon is not properly closed

        val lastPointX = polygon.points[polygon.points.size - 2]
        val lastPointY = polygon.points[polygon.points.size - 1]

        if (!FXGLMath.isClose(firstEdge.x1, lastPointX, EDGE_TOLERANCE)
                && !FXGLMath.isClose(firstEdge.y1, lastPointY, EDGE_TOLERANCE)) {

            val closest = corners.sortedBy { it.distance(lastPointX, lastPointY) }.first()

            polygon.points.addAll(closest.x, closest.y)
        }

        return polygon
    }

    private val EDGE_TOLERANCE = 2.0
    private val DUMMY_EDGE = GraphEdge()

    private fun nextEdge(next: Point2D, edges: List<GraphEdge>): Pair<Point2D, GraphEdge>? {
        edges.forEach {
            if (FXGLMath.isClose(next.x, it.x1, EDGE_TOLERANCE) && FXGLMath.isClose(next.y, it.y1, EDGE_TOLERANCE)) {
                return Point2D(it.x2, it.y2).to(it)
            }

            if (FXGLMath.isClose(next.x, it.x2, EDGE_TOLERANCE) && FXGLMath.isClose(next.y, it.y2, EDGE_TOLERANCE)) {
                return Point2D(it.x1, it.y1).to(it)
            }
        }

        // if we are missing an edge (because of corners) return dummy edge and valid next vertex
        if (next.x.toInt() == bbox.minX.toInt() || next.x.toInt() == bbox.maxX.toInt()) {
            // x edge case

            edges.forEach {
                if (FXGLMath.isClose(next.x, it.x1, EDGE_TOLERANCE)) {
                    return Point2D(it.x1, it.y1).to(DUMMY_EDGE)
                }

                if (FXGLMath.isClose(next.x, it.x2, EDGE_TOLERANCE)) {
                    return Point2D(it.x2, it.y2).to(DUMMY_EDGE)
                }
            }

        }

        if (next.y.toInt() == bbox.minY.toInt() || next.y.toInt() == bbox.maxY.toInt()) {
            // y edge case

            edges.forEach {
                if (FXGLMath.isClose(next.y, it.y1, EDGE_TOLERANCE)) {
                    return Point2D(it.x1, it.y1).to(DUMMY_EDGE)
                }

                if (FXGLMath.isClose(next.y, it.y2, EDGE_TOLERANCE)) {
                    return Point2D(it.x2, it.y2).to(DUMMY_EDGE)
                }
            }
        }

        // terminating condition to avoid being stuck in loop
        // in case of miscalculation
        // shouldn't happen in normal scenario
        if (corners.contains(next)) {
            log.warning("Failed to find next edge. Left: $edges")
            return null
        }

        // we are dealing with a corner case
        // so we return dummy edge and closes vertex because 2 edges are missing
        // on next run the branch above will be executed to pick up a single missing edge
        val closest = corners.sortedBy { it.distance(next) }.first()

        return closest.to(DUMMY_EDGE)
    }
}