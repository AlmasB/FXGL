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

package sandbox;

import javafx.geometry.Point2D;
import javafx.util.Pair;

/**
 * From https://www.codeproject.com/Articles/33776/Draw-Closed-Smooth-Curve-with-Bezier-Spline
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ClosedBezierSpline
{
    /// <summary>
    /// Get Closed Bezier Spline Control Points.
    /// </summary>
    /// <param name="knots">Input Knot Bezier spline points.</param>
    /// <param name="firstControlPoints">
    /// Output First Control points array of the same
    /// length as the <paramref name="knots"> array.</param>
    /// <param name="secondControlPoints">
    /// Output Second Control points array of the same
    /// length as the <paramref name="knots"> array.</param>
    public static Pair<Point2D[], Point2D[]> GetCurveControlPoints(Point2D[] knots)
    {
        int n = knots.length;

        // Calculate first Bezier control points

        // The matrix.
        double[] a = new double[n], b = new double[n], c = new double[n];
        for (int i = 0; i < n; ++i)
        {
            a[i] = 1;
            b[i] = 4;
            c[i] = 1;
        }

        // Right hand side vector for points X coordinates.
        double[] rhs = new double[n];
        for (int i = 0; i < n; ++i)
        {
            int j = (i == n - 1) ? 0 : i + 1;
            rhs[i] = 4 * knots[i].getX() + 2 * knots[j].getX();
        }
        // Solve the system for X.
        double[] x = Cyclic.Solve(a, b, c, 1, 1, rhs);

        // Right hand side vector for points Y coordinates.
        for (int i = 0; i < n; ++i)
        {
            int j = (i == n - 1) ? 0 : i + 1;
            rhs[i] = 4 * knots[i].getY() + 2 * knots[j].getY();
        }
        // Solve the system for Y.
        double[] y = Cyclic.Solve(a, b, c, 1, 1, rhs);

        // Fill output arrays.
        Point2D[] firstControlPoints = new Point2D[n];
        Point2D[] secondControlPoints = new Point2D[n];
        for (int i = 0; i < n; ++i)
        {
            // First control point.
            firstControlPoints[i] = new Point2D(x[i], y[i]);
            // Second control point.
            secondControlPoints[i] = new Point2D
                    (2 * knots[i].getX() - x[i], 2 * knots[i].getY() - y[i]);
        }

        return new Pair<>(firstControlPoints, secondControlPoints);
    }
}
