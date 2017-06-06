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

    public JointDef(JointType type) {
        this.type = type;
        userData = null;
        bodyA = null;
        bodyB = null;
        collideConnected = false;
    }

    /**
     * The joint type is set automatically for concrete joint types.
     */
    public JointType type;

    /**
     * Use this to attach application specific data to your joints.
     */
    public Object userData;

    /**
     * The first attached body.
     */
    public Body bodyA;

    /**
     * The second attached body.
     */
    public Body bodyB;

    /**
     * Set this flag to true if the attached bodies should collide.
     */
    public boolean collideConnected;
}
