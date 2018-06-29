/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.saving

import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    override fun toString() = "%-25.25s %s".format(name, dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm")))
}