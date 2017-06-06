/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.io.UIDialogHandler

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ProgressDialog(val message: String) : UIDialogHandler {

    private lateinit var handler: UIDialogHandler

    override fun show() {
        handler = FXGL.getDisplay().showProgressBox(message)
    }

    override fun dismiss() {
        handler.dismiss()
    }
}