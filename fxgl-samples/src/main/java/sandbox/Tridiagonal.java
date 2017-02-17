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
/// Tridiagonal system solution.
/// </summary>
public class Tridiagonal
{
    /// <summary>
    /// Solves a tridiagonal system.
    /// </summary>
    /// <remarks>
    /// All vectors have size of n although some elements are not used.
    /// </remarks>
    /// <param name="a">Lower diagonal vector; a[0] not used.</param>
    /// <param name="b">Main diagonal vector.</param>
    /// <param name="c">Upper diagonal vector; c[n-1] not used.</param>
    /// <param name="rhs">Right hand side vector</param>
    /// <returns>system solution vector</returns>
    public static double[] Solve(double[] a, double[] b, double[] c, double[] rhs)
    {
        // a, b, c and rhs vectors must have the same size.
        if (a.length != b.length || c.length != b.length ||
                rhs.length != b.length)
            throw new IllegalArgumentException
                    ("Diagonal and rhs vectors must have the same size.");
        if (b[0] == 0.0)
            throw new IllegalArgumentException("Singular matrix.");
        // If this happens then you should rewrite your equations as a set of
        // order N - 1, with u2 trivially eliminated.

        int n = rhs.length;
        double[] u = new double[n];
        double[] gam = new double[n]; 	// One vector of workspace,
        // gam is needed.

        double bet = b[0];
        u[0] = rhs[0] / bet;
        for (int j = 1;j < n;j++) // Decomposition and forward substitution.
        {
            gam[j] = c[j-1] / bet;
            bet = b[j] - a[j] * gam[j];
            if (bet == 0.0)
                // Algorithm fails.
                throw new RuntimeException("Singular matrix.");
            u[j] = (rhs[j] - a[j] * u[j - 1]) / bet;
        }
        for (int j = 1;j < n;j++)
            u[n - j - 1] -= gam[n - j] * u[n - j]; // Backsubstitution.

        return u;
    }
}
