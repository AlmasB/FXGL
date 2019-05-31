/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.collision.broadphase;

import com.almasb.fxgl.physics.box2d.collision.AABB;

public class DynamicTreeNode {
    /**
     * Enlarged AABB
     */
    public final AABB aabb = new AABB();

    public Object userData;

    protected DynamicTreeNode parent;

    protected DynamicTreeNode child1;
    protected DynamicTreeNode child2;
    protected final int id;
    protected int height;

    DynamicTreeNode(int id) {
        this.id = id;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object argData) {
        userData = argData;
    }
}
