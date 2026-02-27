/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.collision.broadphase;

import com.almasb.fxgl.physics.box2d.collision.AABB;

final class DynamicTreeNode {
    /**
     * Enlarged AABB
     */
    final AABB aabb = new AABB();

    private Object userData;

    DynamicTreeNode parent;

    DynamicTreeNode child1;
    DynamicTreeNode child2;
    int height;
    
    final int id;

    DynamicTreeNode(int id) {
        this.id = id;
    }

    Object getUserData() {
        return userData;
    }

    void setUserData(Object argData) {
        userData = argData;
    }
}
