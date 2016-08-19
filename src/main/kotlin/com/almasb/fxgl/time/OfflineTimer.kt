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

package com.almasb.fxgl.time

import com.almasb.fxgl.app.FXGL
import javafx.util.Duration
import java.time.LocalDateTime

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class OfflineTimer(val name: String) : LocalTimer {

    override fun capture() {
        FXGL.getSystemBundle().put("offline.timer.$name", LocalDateTime.now())
    }

    override fun elapsed(duration: Duration): Boolean {
        val dateTime = FXGL.getSystemBundle().get<LocalDateTime?>("offline.timer.$name")

        if (dateTime == null) {
            capture()
            return true
        }

        return LocalDateTime.now().minusSeconds(duration.toSeconds().toLong()).isAfter(dateTime)
    }
}