/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.profile

import com.almasb.fxgl.core.serialization.Bundle
import java.io.Serializable
import java.time.LocalDateTime

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

interface SaveLoadHandler {

    /**
     * Called to save game state into [data].
     * This does not perform any IO.
     */
    fun onSave(data: DataFile)

    /**
     * Called to load game state from [data].
     * This does not perform any IO.
     */
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
         * By default, it is the moment this save file object was created.
         */
        val dateTime: LocalDateTime = LocalDateTime.now(),

        /**
         * The save data.
         */
        val data: DataFile = DataFile()

        ) : Serializable {

    /**
     * A comparator that sorts save files in chronological order with recent files first.
     */
    companion object RECENT_FIRST : Comparator<SaveFile> {
        private val serialVersionUid: Long = 1

        override fun compare(o1: SaveFile, o2: SaveFile) = o2.dateTime.compareTo(o1.dateTime)
    }

    override fun toString() = "SaveFile($name)"
}

/**
 * Carries the data that needs to be saved (serialized) using [Bundle].
 */
class DataFile : Serializable {

    companion object {
        private val serialVersionUid: Long = 2
    }

    /**
     * K - bundle name, V - bundle.
     */
    private val bundles = hashMapOf<String, Bundle>()

    /**
     * Stores the given [bundle]. Bundles with same name are not allowed.
     */
    fun putBundle(bundle: Bundle) {
        require(!bundles.containsKey(bundle.name)) {
            "Bundle \"" + bundle.name + "\" already exists!"
        }

        bundles[bundle.name] = bundle
    }

    /**
     * @return bundle by [name] or throws IAE if the bundle does not exist
     */
    fun getBundle(name: String): Bundle {
        return bundles[name] ?: throw IllegalArgumentException("Bundle \"$name\" doesn't exist!")
    }

    override fun toString() = "DataFile($bundles)"
}