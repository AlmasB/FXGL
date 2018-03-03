/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.collision.broadphase;

// updated to rev 100

/**
 * Java note: at the "creation" of each node, a random key is given to that node, and that's what we
 * sort from.
 */
public class Pair implements Comparable<Pair> {
    public int proxyIdA;
    public int proxyIdB;

    @Override
    public int compareTo(Pair pair2) {
        if (this.proxyIdA < pair2.proxyIdA) {
            return -1;
        }

        if (this.proxyIdA == pair2.proxyIdA) {
            return proxyIdB < pair2.proxyIdB ? -1 : proxyIdB == pair2.proxyIdB ? 0 : 1;
        }

        return 1;
    }
}
