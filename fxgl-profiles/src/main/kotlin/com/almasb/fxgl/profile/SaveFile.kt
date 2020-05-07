/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.profile

import com.almasb.fxgl.core.serialization.Bundle
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

interface SaveLoadHandler {

    fun onSave(data: DataFile)

    fun onLoad(data: DataFile)
}

/**
 * Data structure for save files.
 * The actual data saved is in [DataFile].
 */
data class SaveFile
@JvmOverloads constructor(

        /**
         * Save file name, for example "file1.sav", or "myprofile/file1.sav".
         */
        val name: String,

        /**
         * Date and time of the save.
         * By default the moment this save file object was created.
         */
        val dateTime: LocalDateTime = LocalDateTime.now(),

        val data: DataFile = DataFile()

        ) : Serializable {

    companion object RECENT_FIRST : Comparator<SaveFile> {
        private val serialVersionUid: Long = 1

        override fun compare(o1: SaveFile, o2: SaveFile) = o2.dateTime.compareTo(o1.dateTime)
    }

    // TODO: this should be outside
    override fun toString() = "%-25.25s %s".format(name, dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm")))
}

class DataFile : Serializable {

    companion object {
        private val serialVersionUid: Long = 2
    }

    /**
     * K - bundle name, V - bundle.
     */
    private val bundles = hashMapOf<String, Bundle>()

    /**
     * Stores a bundle. Bundles with same name are not allowed.
     */
    fun putBundle(bundle: Bundle) {
        require(!bundles.containsKey(bundle.name)) {
            "Bundle \"" + bundle.name + "\" already exists!"
        }

        bundles[bundle.name] = bundle
    }

    fun getBundle(name: String): Bundle {
        return bundles[name] ?: throw IllegalArgumentException("Bundle \"$name\" doesn't exist!")
    }

    override fun toString(): String {
        return "DataFile($bundles)"
    }
}