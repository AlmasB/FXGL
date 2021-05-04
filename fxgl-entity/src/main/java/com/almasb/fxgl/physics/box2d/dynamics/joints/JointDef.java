/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.physics.box2d.dynamics.Body;

/**
 * Joint definitions are used to construct joints.
 * @author Daniel Murphy
 */
public class JointDef {

    /**
     * The joint type is set automatically for concrete joint types.
     */
    public final JointType type;

    /**
     * Use this to attach application specific data to your joints.
     */
    public Object userData = null;

    /**
     * The first attached body.
     */
    public Body bodyA = null;

    /**
     * The second attached body.
     */
    public Body bodyB = null;

    /**
     * Set this flag to true if the attached bodies should collide.
     */
    public boolean collideConnected = false;

    public JointDef(JointType type) {
        this.type = type;
    }
}
