/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.parser.tiled

/**
 * JSON map format from the Tiled map editor.
 * Specification: https://github.com/bjorn/tiled/wiki/JSON-Map-Format
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TiledMap(var width: Int = 0,
               var height: Int = 0,
               var tilewidth: Int = 0,
               var tileheight: Int = 0,
               var orientation: String = "",
               var layers: List<Layer> = arrayListOf(),
               var tilesets: List<Tileset> = arrayListOf(),
               var backgroundcolor: String = "",
               var renderorder: String = "",
               var nextobjectid: Int = 0,
               var version: Int = 0) {
}