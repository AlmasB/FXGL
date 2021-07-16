/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.World;

import java.util.ArrayList;

/**
 * Definition for a {@link ConstantVolumeJoint}, which connects a group a bodies together so they
 * maintain a constant volume within them.
 */
public class ConstantVolumeJointDef extends JointDef<ConstantVolumeJoint> {
    public float frequencyHz;
    public float dampingRatio;

    ArrayList<Body> bodies;
    ArrayList<DistanceJoint> joints;

    public ConstantVolumeJointDef() {
        bodies = new ArrayList<Body>();
        joints = null;
        frequencyHz = 0.0f;
        dampingRatio = 0.0f;
    }

    /**
     * Adds a body to the group
     *
     * @param argBody
     */
    public void addBody(Body argBody) {
        bodies.add(argBody);
        if (bodies.size() == 1) {
            setBodyA(argBody);
        }
        if (bodies.size() == 2) {
            setBodyB(argBody);
        }
    }

    /**
     * Adds a body and the pre-made distance joint. Should only be used for deserialization.
     */
    public void addBodyAndJoint(Body argBody, DistanceJoint argJoint) {
        addBody(argBody);
        if (joints == null) {
            joints = new ArrayList<DistanceJoint>();
        }
        joints.add(argJoint);
    }

    @Override
    protected ConstantVolumeJoint createJoint(World world) {
        return new ConstantVolumeJoint(world, this);
    }
}
