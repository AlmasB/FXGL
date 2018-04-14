/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
/**
 * Created at 5:20:39 AM Jan 22, 2011
 */
package com.almasb.fxgl.physics.box2d.dynamics.joints;

/**
 * Gear joint definition. This definition requires two existing revolute or prismatic joints (any
 * combination will work). The provided joints must attach a dynamic body to a static body.
 *
 * @author Daniel Murphy
 */
public class GearJointDef extends JointDef {
    /**
     * The first revolute/prismatic joint attached to the gear joint.
     */
    public Joint joint1;

    /**
     * The second revolute/prismatic joint attached to the gear joint.
     */
    public Joint joint2;

    /**
     * Gear ratio.
     *
     * @see GearJoint
     */
    public float ratio;

    public GearJointDef() {
        super(JointType.GEAR);
        joint1 = null;
        joint2 = null;
    }
}
