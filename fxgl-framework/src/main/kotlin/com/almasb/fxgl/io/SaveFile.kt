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

package com.almasb.fxgl.io

import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Data structure for save files.
 * The actual data saved is in [DataFile].
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class SaveFile(

        /**
         * Save file name without the extension.
         * So "file1.sav" becomes "file1".
         */
        val name: String,

        /**
         * Date and time of the save.
         */
        val dateTime: LocalDateTime) : Serializable {

    companion object RECENT_FIRST : Comparator<SaveFile> {
        private val serialVersionUid: Long = 1

        override fun compare(o1: SaveFile, o2: SaveFile) = o2.dateTime.compareTo(o1.dateTime)
    }

    override fun toString() = name + " " + dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm"))
}