/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Definition for a {@link ConstantVolumeJoint}.
 */
public class ConstantVolumeJointDef extends JointDef<ConstantVolumeJoint> {
    public float frequencyHz = 0.0f;
    public float dampingRatio = 0.0f;

    List<Body> bodies = new ArrayList<>();
    List<DistanceJoint> joints = new ArrayList<>();

    /**
     * Adds a body to the group.
     */
    public void addBody(Body body) {
        bodies.add(body);

        if (bodies.size() == 1) {
            setBodyA(body);
        }

        if (bodies.size() == 2) {
            setBodyB(body);
        }
    }

    /**
     * Adds a body and the pre-made distance joint. Should only be used for deserialization.
     */
    public void addBodyAndJoint(Body body, DistanceJoint joint) {
        addBody(body);
        joints.add(joint);
    }

    @Override
    protected ConstantVolumeJoint createJoint(World world) {
        return new ConstantVolumeJoint(world, this);
    }
}
