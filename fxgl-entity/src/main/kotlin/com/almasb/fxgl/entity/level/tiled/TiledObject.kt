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
class TiledObject(var id: Int = 0,
                  var width: Int = 0,
                  var height: Int = 0,
                  var name: String = "",
                  var type: String = "",
                  var visible: Boolean = true,
                  var x: Int = 0,
                  var y: Int = 0,
                  var rotation: Float = 0.0f,
                  var gid: Int = 0,
                  var isFlippedHorizontal: Boolean = false,
                  var isFlippedVertical: Boolean = false,
                  var properties: Map<String, Any> = hashMapOf(),
                  var propertytypes: Map<String, String> = hashMapOf()) {
}