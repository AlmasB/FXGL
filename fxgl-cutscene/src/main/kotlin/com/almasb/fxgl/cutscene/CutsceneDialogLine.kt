/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class CutsceneDialogLine(

        /**
         * Who says this line.
         */
        val owner: String,

        /**
         * The line text.
         */
        val data: String)