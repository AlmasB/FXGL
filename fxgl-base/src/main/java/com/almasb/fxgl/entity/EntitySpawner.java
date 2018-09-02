/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

import com.almasb.fxgl.core.util.Function;

/**
 * A function that spawns an entity given spawn data.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface EntitySpawner extends Function<SpawnData, Entity> {

}
