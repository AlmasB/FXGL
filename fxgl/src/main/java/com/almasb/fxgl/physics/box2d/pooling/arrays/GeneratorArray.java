/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.pooling.arrays;

import com.almasb.fxgl.physics.box2d.particle.VoronoiDiagram;

import java.util.HashMap;

public class GeneratorArray {

    private final HashMap<Integer, VoronoiDiagram.Generator[]> map =
            new HashMap<Integer, VoronoiDiagram.Generator[]>();

    public VoronoiDiagram.Generator[] get(int length) {
        assert (length > 0);

        if (!map.containsKey(length)) {
            map.put(length, getInitializedArray(length));
        }

        assert (map.get(length).length == length) : "Array not built of correct length";
        return map.get(length);
    }

    protected VoronoiDiagram.Generator[] getInitializedArray(int length) {
        final VoronoiDiagram.Generator[] ray = new VoronoiDiagram.Generator[length];
        for (int i = 0; i < ray.length; i++) {
            ray[i] = new VoronoiDiagram.Generator();
        }
        return ray;
    }
}
