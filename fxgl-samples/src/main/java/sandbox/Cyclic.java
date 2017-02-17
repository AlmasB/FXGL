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

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
/// <summary>
/// Solves the cyclic set of linear equations.
/// </summary>
/// <remarks>
/// The cyclic set of equations have the form
/// ---------------------------
/// b0 c0  0 · · · · · · ß
///	a1 b1 c1 · · · · · · ·
///  · · · · · · · · · · ·
///  · · · a[n-2] b[n-2] c[n-2]
/// a  · · · · 0  a[n-1] b[n-1]
/// ---------------------------
/// This is a tridiagonal system, except for the matrix elements
/// a and ß in the corners.
/// </remarks>
public class Cyclic
{
    /// <summary>
    /// Solves the cyclic set of linear equations.
    /// </summary>
    /// <remarks>
    /// All vectors have size of n although some elements are not used.
    /// The input is not modified.
    /// </remarks>
    /// <param name="a">Lower diagonal vector of size n; a[0] not used.</param>
    /// <param name="b">Main diagonal vector of size n.</param>
    /// <param name="c">Upper diagonal vector of size n; c[n-1] not used.</param>
    /// <param name="alpha">Bottom-left corner value.</param>
    /// <param name="beta">Top-right corner value.</param>
    /// <param name="rhs">Right hand side vector.</param>
    /// <returns>The solution vector of size n.</returns>
    public static double[] Solve(double[] a, double[] b,
                                 double[] c, double alpha, double beta, double[] rhs)
    {
        // a, b, c and rhs vectors must have the same size.
        if (a.length != b.length || c.length != b.length ||
                rhs.length != b.length)
            throw new IllegalArgumentException
                    ("Diagonal and rhs vectors must have the same size.");
        int n = b.length;
        if (n <= 2)
            throw new IllegalArgumentException
                    ("n too small in Cyclic; must be greater than 2.");

        double gamma = -b[0]; // Avoid subtraction error in forming bb[0].
        // Set up the diagonal of the modified tridiagonal system.
        double[] bb = new double[n];
        bb[0] = b[0] - gamma;
        bb[n-1] = b[n - 1] - alpha * beta / gamma;
        for (int i = 1; i < n - 1; ++i)
            bb[i] = b[i];
        // Solve A · x = rhs.
        double[] solution = Tridiagonal.Solve(a, bb, c, rhs);
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
        solution = Tridiagonal.Solve(a, bb, c, u);
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
