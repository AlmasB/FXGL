/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.pooling.arrays;

import com.almasb.fxgl.core.math.Vec2;

import java.util.HashMap;

/**
 * not thread safe Vec2[] pool
 * @author dmurph
 *
 */
public class Vec2Array {

    private final HashMap<Integer, Vec2[]> map = new HashMap<Integer, Vec2[]>();

    public Vec2[] get(int argLength) {
        assert (argLength > 0);

        if (!map.containsKey(argLength)) {
            map.put(argLength, getInitializedArray(argLength));
        }

        assert (map.get(argLength).length == argLength) : "Array not built of correct length";
        return map.get(argLength);
    }

    protected Vec2[] getInitializedArray(int argLength) {
        final Vec2[] ray = new Vec2[argLength];
        for (int i = 0; i < ray.length; i++) {
            ray[i] = new Vec2();
        }
        return ray;
    }
}
