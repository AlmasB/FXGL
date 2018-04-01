/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.concurrent

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface UIDialogHandler {

    fun show()

    fun dismiss()
}

val NONE = object : UIDialogHandler {
    override fun show() {

    }

    override fun dismiss() {

    }
}