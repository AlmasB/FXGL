/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.profile

import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Data structure for save files.
 * The actual data saved is in [DataFile].
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class SaveFile
@JvmOverloads constructor(

        /**
         * Save file name without the extension.
         * So "file1.sav" becomes "file1".
         */
        val name: String,

        val profileName: String,

        val saveFileExt: String,

        /**
         * Date and time of the save.
         */
        val dateTime: LocalDateTime = LocalDateTime.now(),

        val data: DataFile = DataFile.EMPTY

        ) : Serializable {

    companion object RECENT_FIRST : Comparator<SaveFile> {
        private val serialVersionUid: Long = 1

        override fun compare(o1: SaveFile, o2: SaveFile) = o2.dateTime.compareTo(o1.dateTime)
    }

    val relativePathName: String = "$profileName/$name.$saveFileExt"

    override fun toString() = "%-25.25s %s".format(name, dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm")))
}

data class DataFile(

        /**
         * The actual serializable game data structure.
         */
        val data: Serializable) : Serializable {

    companion object {
        private val serialVersionUid: Long = 2

        @JvmStatic val EMPTY = DataFile("")
    }
}

///**
// * K - bundle name, V - bundle
// */
//private val bundles: MutableMap<String, Bundle> = HashMap()
//
///**
// *
// * @param appTitle app title
// * @param appVersion app version
// * @return true iff title and version are compatible with the app
// */
//fun isCompatible(appTitle: String, appVersion: String): Boolean {
//    return this.appTitle == appTitle && this.appVersion == appVersion
//}
//
///**
// * https://github.com/AlmasB/FXGL/issues/576
// * Stores a bundle in the user profile. Bundles with same
// * name are not allowed.
// *
// * @param bundle the bundle to store
// */
//fun putBundle(bundle: Bundle) {
//    require(!bundles.containsKey(bundle.name)) {
//        "Bundle \"" + bundle.name + "\" already exists!"
//    }
//
//    bundles[bundle.name] = bundle
//}
//
///**
// * https://github.com/AlmasB/FXGL/issues/576
// * @param name bundle name
// * @return bundle with given name
// */
//fun getBundle(name: String): Bundle {
//    return bundles[name] ?: throw IllegalArgumentException("Bundle \"$name\" doesn't exist!")
//}
//
//fun log(logger: Logger) {
//    logger.info("Logging profile data")
//    logger.info(bundles.toString())
//}