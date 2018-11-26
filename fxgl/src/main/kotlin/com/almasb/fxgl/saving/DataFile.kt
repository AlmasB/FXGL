/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.saving

import java.io.Serializable

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class DataFile(

        /**
         * The actual serializable game data structure.
         */
        val data: Serializable) : Serializable {

    companion object {
        private val serialVersionUid: Long = 1

        @JvmStatic val EMPTY = DataFile("")
    }
}