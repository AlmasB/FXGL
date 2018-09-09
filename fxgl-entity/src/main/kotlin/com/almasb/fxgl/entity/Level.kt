/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

/**
 * Represents a game level.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Level(
        /**
         * Level width in pixels.
         */
        val width: Int,

        /**
         * Level height in pixels.
         */
        val height: Int,

        /**
         * Level entities.
         */
        val entities: List<Entity>) {

}