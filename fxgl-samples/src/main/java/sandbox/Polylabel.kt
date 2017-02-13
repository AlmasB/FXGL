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

package sandbox

import javafx.geometry.Point2D
import math.geom2d.line.LineSegment2D
import math.geom2d.polygon.Polygon2D
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Polylabel {

    companion object {
        val precision = 1.0

        // a priority queue of cells in order of their "potential" (max distance to polygon)
        private val cellQueue = PriorityQueue<Cell>(Comparator { a, b -> (b.max - a.max).toInt() })

        @JvmStatic fun findCenter(polygon: Polygon2D): Point2D {

            // find the bounding box of the outer ring
            val bbox = polygon.boundingBox()

            val minX = bbox.minX
            val minY = bbox.minY
            val maxX = bbox.maxX
            val maxY = bbox.maxY

//            for (var i = 0; i < polygon[0].length; i++) {
//                var p = polygon[0][i];
//                if (!i || p[0] < minX) minX = p[0];
//                if (!i || p[1] < minY) minY = p[1];
//                if (!i || p[0] > maxX) maxX = p[0];
//                if (!i || p[1] > maxY) maxY = p[1];
//            }

            var width = maxX - minX;
            var height = maxY - minY;
            var cellSize = Math.min(width, height);
            var h = cellSize / 2;


            if (cellSize == 0.0)
                return Point2D(minX, minY)

            var x = minX
            var y = minY

            // cover polygon with initial cells
            while (x < maxX) {
                while (y < maxY) {
                    cellQueue.add(Cell(x + h, y + h, h, polygon))

                    y += cellSize
                }

                x += cellSize
            }

            // take centroid as the first best guess
            var bestCell = getCentroidCell(polygon)

            // special case for rectangular polygons
            var bboxCell = Cell(minX + width / 2, minY + height / 2, 0.0, polygon)

            if (bboxCell.d > bestCell.d)
                bestCell = bboxCell

            var numProbes = cellQueue.size

            while (cellQueue.isNotEmpty()) {
                // pick the most promising cell from the queue
                var cell = cellQueue.poll()

                // update the best cell if we found a better one
                if (cell.d > bestCell.d) {
                    bestCell = cell;
                }

                // do not drill down further if there's no chance of a better solution
                if (cell.max - bestCell.d <= precision)
                    continue;

                // split the cell into four cells
                h = cell.h / 2;

                cellQueue.add(Cell(cell.x - h, cell.y - h, h, polygon))
                cellQueue.add(Cell(cell.x + h, cell.y - h, h, polygon))
                cellQueue.add(Cell(cell.x - h, cell.y + h, h, polygon))
                cellQueue.add(Cell(cell.x + h, cell.y + h, h, polygon))

                numProbes += 4;
            }

            return Point2D(bestCell.x, bestCell.y)
        }



//        function compareMax(a, b) {
//            return b.max - a.max;
//        }
//

        // signed distance from point to polygon outline (negative if point is outside)
        fun pointToPolygonDist(x: Double, y: Double, polygon: Polygon2D): Double {
//            var inside = false;
//            var minDistSq = Double.MAX_VALUE;
//
//            for (ring in polygon.contours()) {
//                var i = 0
//                var len = ring.vertexNumber()
//                var j = len - 1
//
//                while (i < len) {
//                    var a = ring.vertex(i)
//                    var b = ring.vertex(j)
//
//                    if ((a.y() > y !== b.y() > y) &&
//                            (x < (b.x() - a.x()) * (y - a.y()) / (b.y() - a.y()) + a.x())) inside = !inside;
//
//                    minDistSq = Math.min(minDistSq, getSegDistSq(x, y, Point2D(a.x(), a.y()), Point2D(b.x(), b.y())));
//
//                    j = i
//                    i++
//                }
//            }


//            for (var k = 0; k < polygon.length; k++) {
//                var ring = polygon[k];
//
//                for (var i = 0, len = ring.length, j = len - 1; i < len; j = i++) {
//                var a = ring[i];
//                var b = ring[j];
//

//
//
//            }
//            }











//            val result = (if (inside) 1 else -1) * Math.sqrt(minDistSq)
//            val result2 = -polygon.boundary().signedDistance(x, y)
//
//            if (result.toInt() == result2.toInt()) {
//                println("EQUALS")
//            } else {
//                println("Poly: $result")
//                println("Comp: $result2")
//            }

            return -polygon.boundary().signedDistance(x, y)
        }

        // get polygon centroid
        private fun getCentroidCell(polygon: Polygon2D): Cell {
            var area = 0;
            var x = 0;
            var y = 0;
            //var points = polygon[0];

            val centroid = polygon.centroid()

//            for (var i = 0, len = points.length, j = len - 1; i < len; j = i++) {
//                var a = points[i];
//                var b = points[j];
//                var f = a[0] * b[1] - b[0] * a[1];
//                x += (a[0] + b[0]) * f;
//                y += (a[1] + b[1]) * f;
//                area += f * 3;
//            }
//            if (area === 0)
//                return Cell(points[0][0], points[0][1], 0.0, polygon);

            return Cell(centroid.x(), centroid.y(), 0.0, polygon);
        }

        // get squared distance from a point to a segment
        fun getSegDistSq(px: Double, py: Double, a: Point2D, b: Point2D): Double {

            var x = a.x;
            var y = a.y;
            var dx = b.x - x;
            var dy = b.y - y;

            if (dx !== 0.0 || dy !== 0.0) {

                var t = ((px - x) * dx + (py - y) * dy) / (dx * dx + dy * dy);

                if (t > 1) {
                    x = b.x
                    y = b.y

                } else if (t > 0) {
                    x += dx * t;
                    y += dy * t;
                }
            }

            dx = px - x;
            dy = py - y;

            return dx * dx + dy * dy;
        }
    }

    private class Cell(
            // cell center
            val x: Double,
            val y: Double,

            // half cell size
            val h: Double,
            val polygon: Polygon2D) {

        // distance from cell center to polygon
        val d: Double

        // max distance to polygon within a cell
        val max: Double

        init {
            d = pointToPolygonDist(x, y, polygon)
            max = d + h * Math.sqrt(2.0)
        }
    }
}