/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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
