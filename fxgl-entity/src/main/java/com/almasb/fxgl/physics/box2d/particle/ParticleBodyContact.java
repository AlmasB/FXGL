/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.particle;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.dynamics.Body;

public class ParticleBodyContact {
    /**
     * Index of the particle making contact.
     */
    public int index;
    /**
     * The body making contact.
     */
    public Body body;
    /**
     * Weight of the contact. A value between 0.0f and 1.0f.
     */
    float weight;
    /**
     * The normalized direction from the particle to the body.
     */
    public final Vec2 normal = new Vec2();
    /**
     * The effective mass used in calculating force.
     */
    float mass;
}
