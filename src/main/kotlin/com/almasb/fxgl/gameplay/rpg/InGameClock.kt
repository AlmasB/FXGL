/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.gameplay.rpg

/**
 * TODO: API INCOMPLETE
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InGameClock {

    /**
     * The range is [0, 23].
     */
    private val hours = 0

    /**
     * The range is [0, 59].
     */
    private val minutes = 0

    /**
     * How many realtime seconds is 1 in-game hour.
     * The default value is 3600 seconds, i.e. by default
     * 1 realtime hour = 1 in-game hour.
     */
    var realtimeEquivalence = 3600

    var dayTimeStart = 8
    var nightTimeStart = 20

    fun isDayTime() = hours in dayTimeStart..nightTimeStart

    fun isNightTime() = hours in nightTimeStart..23 || hours in 0..dayTimeStart
}