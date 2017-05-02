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

package com.almasb.fxgl.core.math;

import com.almasb.fxgl.core.collection.Array;

/**
 * A simple data structure for a bezier spline made up of cubic bezier curves.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BezierSpline {

    private Array<BezierCurve> curves = new Array<>();

    public void addCurve(BezierCurve curve) {
        curves.add(curve);
    }

    public Array<BezierCurve> getCurves() {
        return curves;
    }

    /**
     * A cubic bezier curve.
     */
    public static class BezierCurve {

        private Vec2 start;
        private Vec2 end;
        private Vec2 control1;
        private Vec2 control2;

        public BezierCurve(Vec2 start, Vec2 end, Vec2 control1, Vec2 control2) {
            this.start = start;
            this.end = end;
            this.control1 = control1;
            this.control2 = control2;
        }

        public Vec2 getStart() {
            return start;
        }

        public Vec2 getEnd() {
            return end;
        }

        public Vec2 getControl1() {
            return control1;
        }

        public Vec2 getControl2() {
            return control2;
        }
    }
}
