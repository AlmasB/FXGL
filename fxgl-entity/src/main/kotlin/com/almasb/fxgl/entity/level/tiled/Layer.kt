/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.level.tiled

/**
 * Specification: https://github.com/bjorn/tiled/wiki/JSON-Map-Format
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class Layer(var width: Int = 0,
            var height: Int = 0,
            var name: String = "",
            var type: String = "",
            var visible: Boolean = false,
            var x: Int = 0,
            var y: Int = 0,
            var data: List<Int> = arrayListOf(),
            var objects: List<TiledObject> = arrayListOf(),
            var opacity: Float = 0.0f,
            var draworder: String = "") {

}