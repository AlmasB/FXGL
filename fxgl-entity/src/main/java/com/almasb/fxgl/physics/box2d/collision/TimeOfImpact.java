/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.collision;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.physics.box2d.collision.Distance.DistanceProxy;
import com.almasb.fxgl.physics.box2d.collision.Distance.SimplexCache;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;
import com.almasb.fxgl.physics.box2d.common.Sweep;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

/**
 * Class used for computing the time of impact. This class should not be constructed usually, just
 * retrieve from the pool.
 *
 * @author daniel
 */
public class TimeOfImpact {
    private static final int MAX_ITERATIONS = 1000;

    /**
     * Input parameters for TOI
     *
     * @author Daniel Murphy
     */
    public static class TOIInput {
        public final DistanceProxy proxyA = new DistanceProxy();
        public final DistanceProxy proxyB = new DistanceProxy();
        public final Sweep sweepA = new Sweep();
        public final Sweep sweepB = new Sweep();
        /**
         * defines sweep interval [0, tMax]
         */
        public float tMax;
    }

    public enum TOIOutputState {
        UNKNOWN, FAILED, OVERLAPPED, TOUCHING, SEPARATED
    }

    /**
     * Output parameters for TimeOfImpact
     *
     * @author daniel
     */
    public static class TOIOutput {
        public TOIOutputState state;
        public float t;
    }

    // djm pooling
    private final SimplexCache cache = new SimplexCache();
    private final DistanceInput distanceInput = new DistanceInput();
    private final Transform xfA = new Transform();
    private final Transform xfB = new Transform();
    private final DistanceOutput distanceOutput = new DistanceOutput();
    private final SeparationFunction fcn = new SeparationFunction();
    private final int[] indexes = new int[2];
    private final Sweep sweepA = new Sweep();
    private final Sweep sweepB = new Sweep();

    private final IWorldPool pool;

    public TimeOfImpact(IWorldPool pool) {
        this.pool = pool;
    }

    /**
     * Compute the upper bound on time before two shapes penetrate. Time is represented as a fraction
     * between [0,tMax]. This uses a swept separating axis and may miss some intermediate,
     * non-tunneling collision. If you change the time interval, you should call this function again.
     * Note: use Distance to compute the contact point and normal at the time of impact.
     */
    public final void timeOfImpact(TOIOutput output, TOIInput input) {
        // CCD via the local separating axis method. This seeks progression
        // by computing the largest time at which separation is maintained.

        output.state = TOIOutputState.UNKNOWN;
        output.t = input.tMax;

        final DistanceProxy proxyA = input.proxyA;
        final DistanceProxy proxyB = input.proxyB;

        sweepA.set(input.sweepA);
        sweepB.set(input.sweepB);

        // Large rotations can make the root finder fail, so we normalize the sweep angles.
        sweepA.normalize();
        sweepB.normalize();

        float tMax = input.tMax;

        float totalRadius = proxyA.m_radius + proxyB.m_radius;
        // djm: whats with all these constants?
        float target = Math.max(JBoxSettings.linearSlop, totalRadius - 3.0f * JBoxSettings.linearSlop);
        float tolerance = 0.25f * JBoxSettings.linearSlop;

        assert target > tolerance;

        float t1 = 0f;
        int iter = 0;

        cache.count = 0;
        distanceInput.proxyA = input.proxyA;
        distanceInput.proxyB = input.proxyB;
        distanceInput.useRadii = false;

        // The outer loop progressively attempts to compute new separating axes.
        // This loop terminates when an axis is repeated (no progress is made).
        for (; ; ) {
            sweepA.getTransform(xfA, t1);
            sweepB.getTransform(xfB, t1);

            // Get the distance between shapes.
            // We can also use the results to get a separating axis
            distanceInput.transformA = xfA;
            distanceInput.transformB = xfB;
            pool.getDistance().distance(distanceOutput, cache, distanceInput);

            // If the shapes are overlapped, we give up on continuous collision.
            if (distanceOutput.distance <= 0f) {
                // Failure!
                output.state = TOIOutputState.OVERLAPPED;
                output.t = 0f;
                break;
            }

            if (distanceOutput.distance < target + tolerance) {
                // Victory!
                output.state = TOIOutputState.TOUCHING;
                output.t = t1;
                break;
            }

            // Initialize the separating axis.
            fcn.initialize(cache, proxyA, sweepA, proxyB, sweepB, t1);

            // Compute the TOI on the separating axis.
            // We do this by successively resolving the deepest point.
            // This loop is bounded by the number of vertices.
            boolean done = false;
            float t2 = tMax;
            int pushBackIter = 0;
            for (; ; ) {

                // Find the deepest point at t2. Store the witness point indices.
                float s2 = fcn.findMinSeparation(indexes, t2);

                // Is the final configuration separated?
                if (s2 > target + tolerance) {
                    // Victory!
                    output.state = TOIOutputState.SEPARATED;
                    output.t = tMax;
                    done = true;
                    break;
                }

                // Has the separation reached tolerance?
                if (s2 > target - tolerance) {
                    // Advance the sweeps
                    t1 = t2;
                    break;
                }

                // Compute the initial separation of the witness points.
                float s1 = fcn.evaluate(indexes[0], indexes[1], t1);

                // Check for initial overlap.
                // This might happen if the root finder runs out of iterations.
                if (s1 < target - tolerance) {
                    output.state = TOIOutputState.FAILED;
                    output.t = t1;
                    done = true;
                    break;
                }

                // Check for touching
                if (s1 <= target + tolerance) {
                    // Victory! t1 should hold the TOI (could be 0.0).
                    output.state = TOIOutputState.TOUCHING;
                    output.t = t1;
                    done = true;
                    break;
                }

                // Compute 1D root of: f(x) - target = 0
                int rootIterCount = 0;
                float a1 = t1, a2 = t2;
                for (; ; ) {
                    // Use a mix of the secant rule and bisection.
                    float t;
                    if ((rootIterCount & 1) == 1) {
                        // Secant rule to improve convergence.
                        t = a1 + (target - s1) * (a2 - a1) / (s2 - s1);
                    } else {
                        // Bisection to guarantee progress.
                        t = 0.5f * (a1 + a2);
                    }

                    float s = fcn.evaluate(indexes[0], indexes[1], t);

                    if (FXGLMath.abs(s - target) < tolerance) {
                        // t2 holds a tentative value for t1
                        t2 = t;
                        break;
                    }

                    // Ensure we continue to bracket the root.
                    if (s > target) {
                        a1 = t;
                        s1 = s;
                    } else {
                        a2 = t;
                        s2 = s;
                    }

                    ++rootIterCount;

                    // djm: whats with this? put in settings?
                    if (rootIterCount == 50) {
                        break;
                    }
                }

                ++pushBackIter;

                if (pushBackIter == JBoxSettings.maxPolygonVertices) {
                    break;
                }
            }

            ++iter;

            if (done) {
                break;
            }

            if (iter == MAX_ITERATIONS) {
                // Root finder got stuck. Semi-victory.
                output.state = TOIOutputState.FAILED;
                output.t = t1;
                break;
            }
        }
    }
}