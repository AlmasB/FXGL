/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics;

/**
 * This holds contact filtering data.
 *
 * @author daniel
 */
public class Filter {
    /**
     * The collision category bits. Normally you would just set one bit.
     */
    public int categoryBits = 0x0001;

    /**
     * The collision mask bits. This states the categories that this
     * shape would accept for collision.
     */
    public int maskBits = 0xFFFF;

    /**
     * Collision groups allow a certain group of objects to never collide (negative)
     * or always collide (positive). Zero means no collision group. Non-zero group
     * filtering always wins against the mask bits.
     */
    public int groupIndex = 0;

    public void set(Filter argOther) {
        categoryBits = argOther.categoryBits;
        maskBits = argOther.maskBits;
        groupIndex = argOther.groupIndex;
    }
}
