/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.collision;

import com.almasb.fxgl.physics.box2d.collision.shapes.Shape;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class GenericCollision {

    private static final DistanceInput input = new DistanceInput();
    private static final Distance.SimplexCache cache = new Distance.SimplexCache();
    private static final DistanceOutput output = new DistanceOutput();

    /**
     * @return true if two generic shapes (shapeA and shapeB) overlap
     */
    public static boolean testOverlap(
            IWorldPool pool,
            Shape shapeA, int indexA,
            Shape shapeB, int indexB,
            Transform xfA, Transform xfB) {

        input.proxyA.set(shapeA, indexA);
        input.proxyB.set(shapeB, indexB);
        input.transformA.set(xfA);
        input.transformB.set(xfB);
        input.useRadii = true;

        cache.count = 0;

        pool.getDistance().distance(output, cache, input);

        return output.distance < 10.0f * JBoxSettings.EPSILON;
    }
}
