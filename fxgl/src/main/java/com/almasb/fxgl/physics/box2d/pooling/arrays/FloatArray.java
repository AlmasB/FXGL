/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.pooling.arrays;

import java.util.HashMap;

/**
 * Not thread safe float[] pooling.
 * @author Daniel
 */
public class FloatArray {

    private final HashMap<Integer, float[]> map = new HashMap<Integer, float[]>();

    public float[] get(int argLength) {
        assert (argLength > 0);

        if (!map.containsKey(argLength)) {
            map.put(argLength, getInitializedArray(argLength));
        }

        assert (map.get(argLength).length == argLength) : "Array not built of correct length";
        return map.get(argLength);
    }

    protected float[] getInitializedArray(int argLength) {
        return new float[argLength];
    }
}
