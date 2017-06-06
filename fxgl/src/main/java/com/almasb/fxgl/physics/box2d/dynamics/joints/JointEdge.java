/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.joints;

import com.almasb.fxgl.physics.box2d.dynamics.Body;

/**
 * A joint edge is used to connect bodies and joints together
 * in a joint graph where each body is a node and each joint
 * is an edge. A joint edge belongs to a doubly linked list
 * maintained in each attached body. Each joint has two joint
 * nodes, one for each attached body.
 * @author Daniel
 */
public class JointEdge {

    /**
     * Provides quick access to the other body attached
     */
    public Body other = null;

    /**
     * the joint
     */
    public Joint joint = null;

    /**
     * the previous joint edge in the body's joint list
     */
    public JointEdge prev = null;

    /**
     * the next joint edge in the body's joint list
     */
    public JointEdge next = null;
}
