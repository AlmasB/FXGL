/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.collision;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;
import com.almasb.fxgl.physics.box2d.common.JBoxUtils;
import com.almasb.fxgl.physics.box2d.common.Rotation;
import com.almasb.fxgl.physics.box2d.common.Transform;

/**
 * This is used to compute the current state of a contact manifold.
 *
 * @author daniel
 */
public final class WorldManifold {
    /**
     * World vector pointing from A to B
     */
    public final Vec2 normal = new Vec2();

    /**
     * World contact point (point of intersection)
     */
    public final Vec2[] points = new Vec2[JBoxSettings.maxManifoldPoints];

    public WorldManifold() {
        for (int i = 0; i < JBoxSettings.maxManifoldPoints; i++) {
            points[i] = new Vec2();
        }
    }

    private final Vec2 pool3 = new Vec2();
    private final Vec2 pool4 = new Vec2();

    @SuppressWarnings("PMD.UselessParentheses")
    public void initialize(final Manifold manifold,
                                 final Transform xfA, float radiusA,
                                 final Transform xfB, float radiusB) {

        if (manifold.pointCount == 0) {
            return;
        }

        switch (manifold.type) {
            case CIRCLES: {
                final Vec2 pointA = pool3;
                final Vec2 pointB = pool4;

                normal.x = 1;
                normal.y = 0;
                Vec2 v = manifold.localPoint;
                // Transform.mulToOutUnsafe(xfA, manifold.localPoint, pointA);
                // Transform.mulToOutUnsafe(xfB, manifold.points[0].localPoint, pointB);
                pointA.x = (xfA.q.c * v.x - xfA.q.s * v.y) + xfA.p.x;
                pointA.y = (xfA.q.s * v.x + xfA.q.c * v.y) + xfA.p.y;
                Vec2 mp0p = manifold.points[0].localPoint;
                pointB.x = (xfB.q.c * mp0p.x - xfB.q.s * mp0p.y) + xfB.p.x;
                pointB.y = (xfB.q.s * mp0p.x + xfB.q.c * mp0p.y) + xfB.p.y;

                if (JBoxUtils.distanceSquared(pointA, pointB) > JBoxSettings.EPSILON * JBoxSettings.EPSILON) {
                    normal.x = pointB.x - pointA.x;
                    normal.y = pointB.y - pointA.y;
                    normal.getLengthAndNormalize();
                }

                final float cAx = normal.x * radiusA + pointA.x;
                final float cAy = normal.y * radiusA + pointA.y;

                final float cBx = -normal.x * radiusB + pointB.x;
                final float cBy = -normal.y * radiusB + pointB.y;

                points[0].x = (cAx + cBx) * .5f;
                points[0].y = (cAy + cBy) * .5f;
            }
            break;
            case FACE_A: {
                final Vec2 planePoint = pool3;

                Rotation.mulToOutUnsafe(xfA.q, manifold.localNormal, normal);
                Transform.mulToOut(xfA, manifold.localPoint, planePoint);

                final Vec2 clipPoint = pool4;

                for (int i = 0; i < manifold.pointCount; i++) {
                    // b2Vec2 clipPoint = b2Mul(xfB, manifold->points[i].localPoint);
                    // b2Vec2 cA = clipPoint + (radiusA - b2Dot(clipPoint - planePoint,
                    // normal)) * normal;
                    // b2Vec2 cB = clipPoint - radiusB * normal;
                    // points[i] = 0.5f * (cA + cB);
                    Transform.mulToOut(xfB, manifold.points[i].localPoint, clipPoint);
                    // use cA as temporary for now
                    // cA.set(clipPoint).subLocal(planePoint);
                    // float scalar = radiusA - Vec2.dot(cA, normal);
                    // cA.set(normal).mulLocal(scalar).addLocal(clipPoint);
                    // cB.set(normal).mulLocal(radiusB).subLocal(clipPoint).negateLocal();
                    // points[i].set(cA).addLocal(cB).mulLocal(0.5f);

                    final float scalar =
                            radiusA
                                    - ((clipPoint.x - planePoint.x) * normal.x + (clipPoint.y - planePoint.y)
                                    * normal.y);

                    final float cAx = normal.x * scalar + clipPoint.x;
                    final float cAy = normal.y * scalar + clipPoint.y;

                    final float cBx = -normal.x * radiusB + clipPoint.x;
                    final float cBy = -normal.y * radiusB + clipPoint.y;

                    points[i].x = (cAx + cBx) * .5f;
                    points[i].y = (cAy + cBy) * .5f;
                }
            }
            break;
            case FACE_B:
                final Vec2 planePoint = pool3;
                Rotation.mulToOutUnsafe(xfB.q, manifold.localNormal, normal);
                Transform.mulToOut(xfB, manifold.localPoint, planePoint);

                // final Mat22 R = xfB.q;
                // normal.x = R.ex.x * manifold.localNormal.x + R.ey.x * manifold.localNormal.y;
                // normal.y = R.ex.y * manifold.localNormal.x + R.ey.y * manifold.localNormal.y;
                // final Vec2 v = manifold.localPoint;
                // planePoint.x = xfB.p.x + xfB.q.ex.x * v.x + xfB.q.ey.x * v.y;
                // planePoint.y = xfB.p.y + xfB.q.ex.y * v.x + xfB.q.ey.y * v.y;

                final Vec2 clipPoint = pool4;

                for (int i = 0; i < manifold.pointCount; i++) {
                    // b2Vec2 clipPoint = b2Mul(xfA, manifold->points[i].localPoint);
                    // b2Vec2 cB = clipPoint + (radiusB - b2Dot(clipPoint - planePoint,
                    // normal)) * normal;
                    // b2Vec2 cA = clipPoint - radiusA * normal;
                    // points[i] = 0.5f * (cA + cB);

                    Transform.mulToOut(xfA, manifold.points[i].localPoint, clipPoint);
                    // cB.set(clipPoint).subLocal(planePoint);
                    // float scalar = radiusB - Vec2.dot(cB, normal);
                    // cB.set(normal).mulLocal(scalar).addLocal(clipPoint);
                    // cA.set(normal).mulLocal(radiusA).subLocal(clipPoint).negateLocal();
                    // points[i].set(cA).addLocal(cB).mulLocal(0.5f);

                    // points[i] = 0.5f * (cA + cB);

                    //
                    // clipPoint.x = xfA.p.x + xfA.q.ex.x * manifold.points[i].localPoint.x + xfA.q.ey.x *
                    // manifold.points[i].localPoint.y;
                    // clipPoint.y = xfA.p.y + xfA.q.ex.y * manifold.points[i].localPoint.x + xfA.q.ey.y *
                    // manifold.points[i].localPoint.y;

                    final float scalar =
                            radiusB
                                    - ((clipPoint.x - planePoint.x) * normal.x + (clipPoint.y - planePoint.y)
                                    * normal.y);

                    final float cBx = normal.x * scalar + clipPoint.x;
                    final float cBy = normal.y * scalar + clipPoint.y;

                    final float cAx = -normal.x * radiusA + clipPoint.x;
                    final float cAy = -normal.y * radiusA + clipPoint.y;

                    points[i].x = (cAx + cBx) * .5f;
                    points[i].y = (cAy + cBy) * .5f;
                }
                // Ensure normal points from A to B.
                normal.x = -normal.x;
                normal.y = -normal.y;
                break;
        }
    }
}
