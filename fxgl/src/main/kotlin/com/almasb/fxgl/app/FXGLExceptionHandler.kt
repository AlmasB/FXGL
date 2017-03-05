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

package com.almasb.fxgl.app

import com.almasb.fxgl.logging.SystemLogger
import com.almasb.fxgl.service.ExceptionHandler
import com.google.inject.Inject
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.stage.Modality
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Default exception handler service provider.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLExceptionHandler
@Inject private constructor() : ExceptionHandler {

    private val log = FXGL.getLogger(javaClass)

    init {
        Thread.setDefaultUncaughtExceptionHandler({ thread, e -> handleFatal(e) })

        log.debug("Service [ExceptionHandler] initialized")
    }

    override fun handle(e: Throwable) {
        log.warning("Caught Exception: $e")
        FXGL.getDisplay().showErrorBox(e)
    }

    private var handledOnce = false

    override fun handleFatal(e: Throwable) {
        if (handledOnce) {
            // just ignore to avoid spamming dialogs
            return
        }

        handledOnce = true

        log.fatal("Uncaught Exception:");
        log.fatal(SystemLogger.errorTraceAsString(e));
        log.fatal("Application will now exit");

        val app = FXGL.getApp()
        app.pause()

        val dialog = Dialog<ButtonType>()
        dialog.title = "Uncaught Exception"

        val dialogPane = dialog.dialogPane
        dialogPane.contentText = "Exception details:"
        dialogPane.buttonTypes.addAll(ButtonType.OK)

        dialogPane.contentText = e.toString()
        dialog.initModality(Modality.APPLICATION_MODAL)

        val label = Label("Exception stacktrace:")
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        pw.close()

        val textArea = TextArea(sw.toString())
        textArea.isEditable = false
        textArea.isWrapText = true
        textArea.setPrefSize(600.0, 600.0)
        textArea.maxWidth = java.lang.Double.MAX_VALUE
        textArea.maxHeight = java.lang.Double.MAX_VALUE

        GridPane.setVgrow(textArea, Priority.ALWAYS)
        GridPane.setHgrow(textArea, Priority.ALWAYS)

        val root = GridPane()
        root.isVisible = false
        root.maxWidth = java.lang.Double.MAX_VALUE
        root.add(label, 0, 0)
        root.add(textArea, 0, 1)

        dialogPane.expandableContent = root
        dialog.showAndWait()

        app.exit()
    }
}