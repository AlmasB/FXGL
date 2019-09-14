/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.ui.DialogBox

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ProgressDialog(val message: String) : IOTask.UIDialogHandler {

    private lateinit var dialog: DialogBox

    override fun show() {
        dialog = FXGL.getDisplay().showProgressBox(message)
    }

    override fun dismiss() {
        dialog.close()
    }
}