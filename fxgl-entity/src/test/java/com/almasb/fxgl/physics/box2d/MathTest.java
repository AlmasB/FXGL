/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d;

/**
 * @author Daniel Murphy
 */
public class MathTest {
//
//    private final static int MAX = (int) (Float.MAX_VALUE / 1000);
//    private final static int RAND_ITERS = 100;
//
//    public void testFastMath() {
//        Random r = new Random();
//        for (int i = 0; i < RAND_ITERS; i++) {
//            float a = r.nextFloat() * MAX - MAX / 2;
//            assertEquals((int) Math.floor(a), JBoxUtils.floor(a));
//        }
//
//        for (int i = 0; i < RAND_ITERS; i++) {
//            float a = r.nextFloat() * MAX - MAX / 2;
//            assertEquals((int) Math.ceil(a), JBoxUtils.ceil(a));
//        }
//
//        for (int i = 0; i < RAND_ITERS; i++) {
//            float a = r.nextFloat() * MAX - MAX / 2;
//            float b = r.nextFloat() * MAX - MAX / 2;
//            assertEquals(Math.max(a, b), Math.max(a, b));
//        }
//
//        for (int i = 0; i < RAND_ITERS; i++) {
//            float a = r.nextFloat() * MAX - MAX / 2;
//            float b = r.nextFloat() * MAX - MAX / 2;
//            assertEquals(Math.min(a, b), Math.min(a, b));
//        }
//
//        for (int i = 0; i < RAND_ITERS; i++) {
//            float a = r.nextFloat() * MAX - MAX / 2;
//            assertEquals(Math.round(a), JBoxUtils.round(a));
//        }
//
//        for (int i = 0; i < RAND_ITERS; i++) {
//            float a = r.nextFloat() * MAX - MAX / 2;
//            assertEquals(Math.abs(a), FXGLMath.abs(a));
//        }
//    }
//
//    public void testVec2() {
//        Vec2 v = new Vec2();
//        v.x = 0;
//        v.y = 1;
//        v.subLocal(new Vec2(10, 10));
//        assertEquals(-10f, v.x);
//        assertEquals(-9f, v.y);
//
//        Vec2 v2 = v.add(new Vec2(1, 1));
//        assertEquals(-9f, v2.x);
//        assertEquals(-8f, v2.y);
//        assertFalse(v.equals(v2));
//    }
//
//    // Note this is specific for UI, so top-left is the origin
//    public void testVec2Angle() {
//        Vec2 v = new Vec2(1, 0);
//        assertEquals(0, v.angle(), 0.0);
//
//        v.set(0, 1);
//        assertEquals(90, v.angle(), 0.0);
//
//        v.set(-1, 0);
//        assertEquals(180, v.angle(), 0.0);
//
//        v.set(0, -1);
//        assertEquals(-90, v.angle(), 0.0);
//
//        v.set(1, 1);
//        assertEquals(45, v.angle(), 0.0);
//
//        v.set(-1, 1);
//        assertEquals(135, v.angle(), 0.0);
//
//        v.set(-1, -1);
//        assertEquals(-135, v.angle(), 0.0);
//
//        v.set(1, -1);
//        assertEquals(-45, v.angle(), 0.0);
//    }
//
//    public void testMat22Unsafes() {
//        Vec2 v1 = new Vec2(10, -1.3f);
//        Mat22 m1 = new Mat22(1, 34, -3, 3);
//        Mat22 m2 = new Mat22(2, -1, 4.1f, -4);
//        Vec2 vo = new Vec2();
//        Mat22 mo = new Mat22();
//
//        Mat22.mulToOutUnsafe(m1, m2, mo);
//        assertEquals(Mat22.mul(m1, m2), mo);
//
//        Mat22.mulToOutUnsafe(m1, v1, vo);
//        assertEquals(Mat22.mul(m1, v1), vo);
//
//        Mat22.mulTransToOutUnsafe(m1, m2, mo);
//        assertEquals(Mat22.mulTrans(m1, m2), mo);
//
//        Mat22.mulTransToOutUnsafe(m1, v1, vo);
//        assertEquals(Mat22.mulTrans(m1, v1), vo);
//    }
//
//    public void testMat33() {
//        Mat33 mat = new Mat33();
//
//        mat.ex.set(3, 19, -5);
//        mat.ey.set(-1, 1, 4);
//        mat.ez.set(-10, 4, 4);
//
//        Vec3 b = new Vec3(4, 1, 2);
//        assertEquals(new Vec3(0.096f, 1.1013334f, -.48133332f), mat.solve33(b));
//
//        Vec2 b2 = new Vec2(4, 1);
//        assertEquals(new Vec2(0.22727273f, -3.318182f), mat.solve22(b2));
//    }
//
//    public void testVec3() {
//        Vec3 v1 = new Vec3();
//        Vec3 v2 = new Vec3();
//
//        assertEquals(new Vec3(1, -15, 36), Vec3.cross(v1.set(9, 3, 1), v2.set(3, 5, 2)));
//    }
}
