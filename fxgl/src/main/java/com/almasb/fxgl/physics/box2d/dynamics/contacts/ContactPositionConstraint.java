/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.contacts;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.collision.Manifold.ManifoldType;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;

public class ContactPositionConstraint {
    Vec2[] localPoints = new Vec2[JBoxSettings.maxManifoldPoints];
    final Vec2 localNormal = new Vec2();
    final Vec2 localPoint = new Vec2();
    int indexA;
    int indexB;
    float invMassA, invMassB;
    final Vec2 localCenterA = new Vec2();
    final Vec2 localCenterB = new Vec2();
    float invIA, invIB;
    ManifoldType type;
    float radiusA, radiusB;
    int pointCount;

    public ContactPositionConstraint() {
        for (int i = 0; i < localPoints.length; i++) {
            localPoints[i] = new Vec2();
        }
    }
}
