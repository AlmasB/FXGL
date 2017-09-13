/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math;

/**
 * Adapted from https://www.codeproject.com/Articles/33776/Draw-Closed-Smooth-Curve-with-Bezier-Spline
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ClosedBezierSplineFactory {

    /**
     * @param knots Input Knot Bezier spline points to pass through
     * @return Closed Bezier Spline Control Points
     */
    static BezierSpline newBezierSpline(Vec2[] knots) {
        int n = knots.length;

        // The matrix.
        double[] a = new double[n];
        double[] b = new double[n];
        double[] c = new double[n];

        for (int i = 0; i < n; ++i) {
            a[i] = 1;
            b[i] = 4;
            c[i] = 1;
        }

        // Right hand side vector for points X coordinates.
        double[] rhs = new double[n];
        for (int i = 0; i < n; ++i) {
            int j = (i == n - 1) ? 0 : i + 1;
            rhs[i] = 4 * knots[i].x + 2 * knots[j].x;
        }

        // Solve the system for X.
        double[] x = Cyclic.solve(a, b, c, 1, 1, rhs);

        // Right hand side vector for points Y coordinates.
        for (int i = 0; i < n; ++i) {
            int j = (i == n - 1) ? 0 : i + 1;
            rhs[i] = 4 * knots[i].y + 2 * knots[j].y;
        }

        // Solve the system for Y.
        double[] y = Cyclic.solve(a, b, c, 1, 1, rhs);

        BezierSpline spline = new BezierSpline();

        for (int i = 0; i < n; i++) {
            int j = i+1 < n ? i+1 : 0;

            spline.addCurve(new BezierSpline.BezierCurve(
                    knots[i],
                    knots[j],
                    new Vec2(x[i], y[i]),
                    new Vec2(2 * knots[j].x - x[j], 2 * knots[j].y - y[j])
            ));
        }

        return spline;
    }

    /**
     * Solves the cyclic set of linear equations.
     *
     * The cyclic set of equations have the form
     * ---------------------------
     * b0 c0  0 · · · · · · ß
     * a1 b1 c1 · · · · · · ·
     * · · · · · · · · · · ·
     * · · · a[n-2] b[n-2] c[n-2]
     * a  · · · · 0  a[n-1] b[n-1]
     * ---------------------------
     *
     * This is a tridiagonal system, except for the matrix elements
     * a and ß in the corners.
     */
    private static class Cyclic {

        /**
         * Solves the cyclic set of linear equations.
         *
         * All vectors have size of n although some elements are not used.
         * The input is not modified.
         *
         * @param a Lower diagonal vector of size n; a[0] not used
         * @param b Main diagonal vector of size n
         * @param c Upper diagonal vector of size n; c[n-1] not used
         * @param alpha Bottom-left corner value
         * @param beta Top-right corner value
         * @param rhs Right hand side vector
         * @return The solution vector of size n
         */
        static double[] solve(double[] a, double[] b, double[] c, double alpha, double beta, double[] rhs) {

            // a, b, c and rhs vectors must have the same size.
            if (a.length != b.length || c.length != b.length || rhs.length != b.length)
                throw new IllegalArgumentException("Diagonal and rhs vectors must have the same size");

            int n = b.length;
            if (n <= 2)
                throw new IllegalArgumentException("n too small in Cyclic; must be greater than 2");

            double gamma = -b[0]; // Avoid subtraction error in forming bb[0].

            // Set up the diagonal of the modified tridiagonal system.
            double[] bb = new double[n];
            bb[0] = b[0] - gamma;
            bb[n-1] = b[n - 1] - alpha * beta / gamma;
            for (int i = 1; i < n - 1; ++i)
                bb[i] = b[i];

            // Solve A · x = rhs.
            double[] solution = Tridiagonal.solve(a, bb, c, rhs);
            double[] x = new double[n];
            for (int k = 0; k < n; ++k)
                x[k] = solution[k];

            // Set up the vector u.
            double[] u = new double[n];
            u[0] = gamma;
            u[n-1] = alpha;
            for (int i = 1; i < n - 1; ++i)
                u[i] = 0.0;

            // Solve A · z = u.
            solution = Tridiagonal.solve(a, bb, c, u);
            double[] z = new double[n];
            for (int k = 0; k < n; ++k)
                z[k] = solution[k];

            // Form v · x/(1 + v · z).
            double fact = (x[0] + beta * x[n - 1] / gamma)
                    / (1.0 + z[0] + beta * z[n - 1] / gamma);

            // Now get the solution vector x.
            for (int i = 0; i < n; ++i)
                x[i] -= fact * z[i];
            return x;
        }
    }

    /**
     * Tridiagonal system solution.
     */
    private static class Tridiagonal {

        /**
         * Solves a tridiagonal system.
         * All vectors have size of n although some elements are not used.
         *
         * @param a Lower diagonal vector; a[0] not used
         * @param b Main diagonal vector
         * @param c Upper diagonal vector; c[n-1] not used
         * @param rhs Right hand side vector
         * @return system solution vector
         */
        static double[] solve(double[] a, double[] b, double[] c, double[] rhs) {

            // a, b, c and rhs vectors must have the same size.
            if (a.length != b.length || c.length != b.length || rhs.length != b.length)
                throw new IllegalArgumentException("Diagonal and rhs vectors must have the same size");

            if (b[0] == 0.0)
                throw new IllegalArgumentException("Singular matrix");

            // If this happens then you should rewrite your equations as a set of
            // order N - 1, with u2 trivially eliminated.

            int n = rhs.length;
            double[] u = new double[n];
            double[] gam = new double[n]; 	// One vector of workspace,
            // gam is needed.

            double bet = b[0];
            u[0] = rhs[0] / bet;

            // Decomposition and forward substitution
            for (int j = 1; j < n; j++) {
                gam[j] = c[j-1] / bet;
                bet = b[j] - a[j] * gam[j];

                if (bet == 0.0)
                    throw new IllegalArgumentException("Singular matrix");

                u[j] = (rhs[j] - a[j] * u[j - 1]) / bet;
            }

            // Backsubstitution
            for (int j = 1;j < n;j++)
                u[n - j - 1] -= gam[n - j] * u[n - j];

            return u;
        }
    }
}
