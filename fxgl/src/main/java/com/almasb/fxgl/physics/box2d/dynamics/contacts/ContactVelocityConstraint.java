/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.contacts;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;
import com.almasb.fxgl.physics.box2d.common.Mat22;

public class ContactVelocityConstraint {
    public VelocityConstraintPoint[] points = new VelocityConstraintPoint[JBoxSettings.maxManifoldPoints];
    public final Vec2 normal = new Vec2();
    public final Mat22 normalMass = new Mat22();
    public final Mat22 K = new Mat22();
    public int indexA;
    public int indexB;
    public float invMassA, invMassB;
    public float invIA, invIB;
    public float friction;
    public float restitution;
    public float tangentSpeed;
    public int pointCount;
    public int contactIndex;

    public ContactVelocityConstraint() {
        for (int i = 0; i < points.length; i++) {
            points[i] = new VelocityConstraintPoint();
        }
    }

    public static class VelocityConstraintPoint {
        public final Vec2 rA = new Vec2();
        public final Vec2 rB = new Vec2();
        public float normalImpulse;
        public float tangentImpulse;
        public float normalMass;
        public float tangentMass;
        public float velocityBias;
    }
}
