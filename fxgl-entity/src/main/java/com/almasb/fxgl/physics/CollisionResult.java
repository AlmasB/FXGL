/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics;

/**
 * Data structure for holding info about collision.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class CollisionResult {

    private HitBox boxA;
    private HitBox boxB;

    public void init(HitBox boxA, HitBox boxB) {
        this.boxA = boxA;
        this.boxB = boxB;
    }

    public HitBox getBoxA() {
        return boxA;
    }

    public HitBox getBoxB() {
        return boxB;
    }
}