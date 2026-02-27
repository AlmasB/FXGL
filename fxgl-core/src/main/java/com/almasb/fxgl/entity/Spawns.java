/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is used for creating entities.
 * Note: the method signature must be <code>public Entity anyName(SpawnData)</code>.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Spawns {

    /**
     * @return name/type of the entity that this method spawns
     */
    String value();
}
