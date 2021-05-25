/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.collision;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.collision.Distance.DistanceProxy;
import com.almasb.fxgl.physics.box2d.collision.Distance.SimplexCache;
import com.almasb.fxgl.physics.box2d.common.Rotation;
import com.almasb.fxgl.physics.box2d.common.Sweep;
import com.almasb.fxgl.physics.box2d.common.Transform;

class SeparationFunction {

    private enum Type {
        POINTS, FACE_A, FACE_B;
    }

    private DistanceProxy m_proxyA;
    private DistanceProxy m_proxyB;
    private Type m_type;
    private final Vec2 m_localPoint = new Vec2();
    private final Vec2 m_axis = new Vec2();
    private Sweep m_sweepA;
    private Sweep m_sweepB;

    // djm pooling
    private final Vec2 localPointA = new Vec2();
    private final Vec2 localPointB = new Vec2();
    private final Vec2 pointA = new Vec2();
    private final Vec2 pointB = new Vec2();
    private final Vec2 localPointA1 = new Vec2();
    private final Vec2 localPointA2 = new Vec2();
    private final Vec2 normal = new Vec2();
    private final Vec2 localPointB1 = new Vec2();
    private final Vec2 localPointB2 = new Vec2();
    private final Vec2 temp = new Vec2();
    private final Transform xfa = new Transform();
    private final Transform xfb = new Transform();

    void initialize(final SimplexCache cache,
                     final DistanceProxy proxyA,
                     final Sweep sweepA,
                     final DistanceProxy proxyB,
                     final Sweep sweepB,
                     float t1) {

        m_proxyA = proxyA;
        m_proxyB = proxyB;
        int count = cache.count;
        assert 0 < count && count < 3;

        m_sweepA = sweepA;
        m_sweepB = sweepB;

        m_sweepA.getTransform(xfa, t1);
        m_sweepB.getTransform(xfb, t1);

        if (count == 1) {
            m_type = Type.POINTS;

            localPointA.set(m_proxyA.getVertex(cache.indexA[0]));
            localPointB.set(m_proxyB.getVertex(cache.indexB[0]));
            Transform.mulToOutUnsafe(xfa, localPointA, pointA);
            Transform.mulToOutUnsafe(xfb, localPointB, pointB);
            m_axis.set(pointB).subLocal(pointA);
            m_axis.getLengthAndNormalize();

        } else if (cache.indexA[0] == cache.indexA[1]) {
            // Two points on B and one on A.
            m_type = Type.FACE_B;

            localPointB1.set(m_proxyB.getVertex(cache.indexB[0]));
            localPointB2.set(m_proxyB.getVertex(cache.indexB[1]));

            temp.set(localPointB2).subLocal(localPointB1);
            Vec2.crossToOutUnsafe(temp, 1f, m_axis);
            m_axis.getLengthAndNormalize();

            Rotation.mulToOutUnsafe(xfb.q, m_axis, normal);

            m_localPoint.set(localPointB1).addLocal(localPointB2).mulLocal(.5f);
            Transform.mulToOutUnsafe(xfb, m_localPoint, pointB);

            localPointA.set(proxyA.getVertex(cache.indexA[0]));
            Transform.mulToOutUnsafe(xfa, localPointA, pointA);

            temp.set(pointA).subLocal(pointB);
            float s = Vec2.dot(temp, normal);
            if (s < 0.0f) {
                m_axis.negateLocal();
            }

        } else {
            // Two points on A and one or two points on B.
            m_type = Type.FACE_A;

            localPointA1.set(m_proxyA.getVertex(cache.indexA[0]));
            localPointA2.set(m_proxyA.getVertex(cache.indexA[1]));

            temp.set(localPointA2).subLocal(localPointA1);
            Vec2.crossToOutUnsafe(temp, 1.0f, m_axis);
            m_axis.getLengthAndNormalize();

            Rotation.mulToOutUnsafe(xfa.q, m_axis, normal);

            m_localPoint.set(localPointA1).addLocal(localPointA2).mulLocal(.5f);
            Transform.mulToOutUnsafe(xfa, m_localPoint, pointA);

            localPointB.set(m_proxyB.getVertex(cache.indexB[0]));
            Transform.mulToOutUnsafe(xfb, localPointB, pointB);

            temp.set(pointB).subLocal(pointA);
            float s = Vec2.dot(temp, normal);
            if (s < 0.0f) {
                m_axis.negateLocal();
            }
        }
    }

    private final Vec2 axisA = new Vec2();
    private final Vec2 axisB = new Vec2();

    float findMinSeparation(int[] indexes, float t) {
        m_sweepA.getTransform(xfa, t);
        m_sweepB.getTransform(xfb, t);

        switch (m_type) {
            case POINTS: {
                Rotation.mulTransUnsafe(xfa.q, m_axis, axisA);
                Rotation.mulTransUnsafe(xfb.q, m_axis.negateLocal(), axisB);
                m_axis.negateLocal();

                indexes[0] = m_proxyA.getSupport(axisA);
                indexes[1] = m_proxyB.getSupport(axisB);

                localPointA.set(m_proxyA.getVertex(indexes[0]));
                localPointB.set(m_proxyB.getVertex(indexes[1]));

                Transform.mulToOutUnsafe(xfa, localPointA, pointA);
                Transform.mulToOutUnsafe(xfb, localPointB, pointB);

                return Vec2.dot(pointB.subLocal(pointA), m_axis);
            }

            case FACE_A: {
                Rotation.mulToOutUnsafe(xfa.q, m_axis, normal);
                Transform.mulToOutUnsafe(xfa, m_localPoint, pointA);

                Rotation.mulTransUnsafe(xfb.q, normal.negateLocal(), axisB);
                normal.negateLocal();

                indexes[0] = -1;
                indexes[1] = m_proxyB.getSupport(axisB);

                localPointB.set(m_proxyB.getVertex(indexes[1]));
                Transform.mulToOutUnsafe(xfb, localPointB, pointB);

                return Vec2.dot(pointB.subLocal(pointA), normal);
            }

            case FACE_B: {
                Rotation.mulToOutUnsafe(xfb.q, m_axis, normal);
                Transform.mulToOutUnsafe(xfb, m_localPoint, pointB);

                Rotation.mulTransUnsafe(xfa.q, normal.negateLocal(), axisA);
                normal.negateLocal();

                indexes[1] = -1;
                indexes[0] = m_proxyA.getSupport(axisA);

                localPointA.set(m_proxyA.getVertex(indexes[0]));
                Transform.mulToOutUnsafe(xfa, localPointA, pointA);

                return Vec2.dot(pointA.subLocal(pointB), normal);
            }

            default:
                assert false;
                indexes[0] = -1;
                indexes[1] = -1;
                return 0f;
        }
    }

    float evaluate(int indexA, int indexB, float t) {
        m_sweepA.getTransform(xfa, t);
        m_sweepB.getTransform(xfb, t);

        switch (m_type) {
            case POINTS: {
                Rotation.mulTransUnsafe(xfa.q, m_axis, axisA);
                Rotation.mulTransUnsafe(xfb.q, m_axis.negateLocal(), axisB);
                m_axis.negateLocal();

                localPointA.set(m_proxyA.getVertex(indexA));
                localPointB.set(m_proxyB.getVertex(indexB));

                Transform.mulToOutUnsafe(xfa, localPointA, pointA);
                Transform.mulToOutUnsafe(xfb, localPointB, pointB);

                return Vec2.dot(pointB.subLocal(pointA), m_axis);
            }

            case FACE_A: {
                Rotation.mulToOutUnsafe(xfa.q, m_axis, normal);
                Transform.mulToOutUnsafe(xfa, m_localPoint, pointA);

                Rotation.mulTransUnsafe(xfb.q, normal.negateLocal(), axisB);
                normal.negateLocal();

                localPointB.set(m_proxyB.getVertex(indexB));
                Transform.mulToOutUnsafe(xfb, localPointB, pointB);

                return Vec2.dot(pointB.subLocal(pointA), normal);
            }

            case FACE_B: {
                Rotation.mulToOutUnsafe(xfb.q, m_axis, normal);
                Transform.mulToOutUnsafe(xfb, m_localPoint, pointB);

                Rotation.mulTransUnsafe(xfa.q, normal.negateLocal(), axisA);
                normal.negateLocal();

                localPointA.set(m_proxyA.getVertex(indexA));
                Transform.mulToOutUnsafe(xfa, localPointA, pointA);

                return Vec2.dot(pointA.subLocal(pointB), normal);
            }

            default:
                assert false;
                return 0f;
        }
    }
}