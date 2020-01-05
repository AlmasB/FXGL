/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.common;

/**
 * Global tuning constants based on MKS units and various integer maximums (vertices per shape,
 * pairs, etc.).
 */
public class JBoxSettings {

    /**
     * A "close to zero" float epsilon value for use.
     **/
    public static final float EPSILON = 1.1920928955078125E-7f;

    /**
     * Pi.
     */
    public static final float PI = (float) Math.PI;

    /**
     * smaller the precision, the larger the table. If a small table is used (eg, precision is .006 or
     * greater), make sure you set the table to lerp it's results. Accuracy chart is in the JBoxUtils
     * source. Good lerp precision values:
     * <ul>
     * <li>.0092</li>
     * <li>.008201</li>
     * <li>.005904</li>
     * <li>.005204</li>
     * <li>.004305</li>
     * <li>.002807</li>
     * <li>.001508</li>
     * <li>9.32500E-4</li>
     * <li>7.48000E-4</li>
     * <li>8.47000E-4</li>
     * <li>.0005095</li>
     * <li>.0001098</li>
     * <li>9.50499E-5</li>
     * <li>6.08500E-5</li>
     * <li>3.07000E-5</li>
     * <li>1.53999E-5</li>
     * </ul>
     */
    public static final float SINCOS_LUT_PRECISION = .00011f;
    public static final int SINCOS_LUT_LENGTH = (int) Math.ceil(Math.PI * 2 / SINCOS_LUT_PRECISION);

    // Collision

    /**
     * The maximum number of contact points between two convex shapes.
     */
    public static int maxManifoldPoints = 2;

    /**
     * The maximum number of vertices on a convex polygon.
     */
    public static int maxPolygonVertices = 8;

    /**
     * This is used to fatten AABBs in the dynamic tree. This allows proxies to move by a small amount
     * without triggering a tree adjustment. This is in meters.
     */
    public static float aabbExtension = 0.1f;

    /**
     * This is used to fatten AABBs in the dynamic tree. This is used to predict the future position
     * based on the current displacement. This is a dimensionless multiplier.
     */
    public static float aabbMultiplier = 2.0f;

    /**
     * A small length used as a collision and constraint tolerance. Usually it is chosen to be
     * numerically significant, but visually insignificant.
     */
    public static float linearSlop = 0.005f;

    /**
     * A small angle used as a collision and constraint tolerance. Usually it is chosen to be
     * numerically significant, but visually insignificant.
     */
    public static float angularSlop = 2.0f / 180.0f * PI;

    /**
     * The radius of the polygon/edge shape skin. This should not be modified. Making this smaller
     * means polygons will have and insufficient for continuous collision. Making it larger may create
     * artifacts for vertex collision.
     */
    public static float polygonRadius = 2.0f * linearSlop;

    /**
     * Maximum number of sub-steps per contact in continuous physics simulation.
     **/
    public static int maxSubSteps = 8;

    // Dynamics

    /**
     * Maximum number of contacts to be handled to solve a TOI island.
     */
    public static int maxTOIContacts = 32;

    /**
     * A velocity threshold for elastic collisions. Any collision with a relative linear velocity
     * below this threshold will be treated as inelastic.
     */
    public static float velocityThreshold = 1.0f;

    /**
     * The maximum linear position correction used when solving constraints. This helps to prevent
     * overshoot.
     */
    public static float maxLinearCorrection = 0.2f;

    /**
     * The maximum angular position correction used when solving constraints. This helps to prevent
     * overshoot.
     */
    public static float maxAngularCorrection = 8.0f / 180.0f * PI;

    /**
     * The maximum linear velocity of a body. This limit is very large and is used to prevent
     * numerical problems. You shouldn't need to adjust this.
     */
    public static float maxTranslation = 2.0f;
    public static float maxTranslationSquared = maxTranslation * maxTranslation;

    /**
     * The maximum angular velocity of a body. This limit is very large and is used to prevent
     * numerical problems. You shouldn't need to adjust this.
     */
    public static float maxRotation = 0.5f * PI;
    public static float maxRotationSquared = maxRotation * maxRotation;

    /**
     * This scale factor controls how fast overlap is resolved. Ideally this would be 1 so that
     * overlap is removed in one time step. However using values close to 1 often lead to overshoot.
     */
    public static float baumgarte = 0.2f;
    public static float toiBaugarte = 0.75f;

    // Sleep

    /**
     * The time that a body must be still before it will go to sleep.
     */
    public static float timeToSleep = 0.5f;

    /**
     * A body cannot sleep if its linear velocity is above this tolerance.
     */
    public static float linearSleepTolerance = 0.01f;

    /**
     * A body cannot sleep if its angular velocity is above this tolerance.
     */
    public static float angularSleepTolerance = 2.0f / 180.0f * PI;

    // Particle

    /**
     * A symbolic constant that stands for particle allocation error.
     */
    public static final int invalidParticleIndex = -1;

    /**
     * The standard distance between particles, divided by the particle radius.
     */
    public static final float particleStride = 0.75f;

    /**
     * The minimum particle weight that produces pressure.
     */
    public static final float minParticleWeight = 1.0f;

    /**
     * The upper limit for particle weight used in pressure calculation.
     */
    public static final float maxParticleWeight = 5.0f;

    /**
     * The maximum distance between particles in a triad, divided by the particle radius.
     */
    public static final int maxTriadDistance = 2;
    public static final int maxTriadDistanceSquared = maxTriadDistance * maxTriadDistance;

    /**
     * The initial size of particle data buffers.
     */
    public static final int minParticleBufferCapacity = 256;
}
