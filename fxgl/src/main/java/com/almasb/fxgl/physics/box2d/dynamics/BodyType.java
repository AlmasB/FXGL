/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics;

/**
 * The body type.
 * <ul>
 *     <li>static: zero mass, zero velocity, may be manually moved</li>
 *     <li>kinematic: zero mass, non-zero velocity set by user, moved by solver</li>
 *     <li>dynamic: positive mass, non-zero velocity determined by forces, moved by solver</li>
 * </ul>
 *
 * @author daniel
 */
public enum BodyType {

    /**
     * Zero mass, zero velocity, may be manually moved.
     */
    STATIC,

    /**
     * Zero mass, non-zero velocity set by user, moved by solver.
     */
    KINEMATIC,

    /**
     * Positive mass, non-zero velocity determined by forces, moved by solver.
     */
    DYNAMIC
}
