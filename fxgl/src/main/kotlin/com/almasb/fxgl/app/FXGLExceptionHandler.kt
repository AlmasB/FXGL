/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.logging.FXGLLogger
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
        Thread.setDefaultUncaughtExceptionHandler({ _, e -> handleFatal(e) })
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
        log.fatal(FXGLLogger.errorTraceAsString(e));
        log.fatal("Application will now exit");

        val app = FXGL.getApp()

        // stop main loop from running as we cannot continue
        app.stopMainLoop()

        // block with error dialog so that user can read the error
        newErrorDialog(e).showAndWait()

        app.exit()
    }

    private fun newErrorDialog(e: Throwable): Dialog<*> {
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

        val dialog = Dialog<ButtonType>()
        dialog.title = "Uncaught Exception"
        dialog.initModality(Modality.APPLICATION_MODAL)

        val dialogPane = dialog.dialogPane
        dialogPane.buttonTypes.addAll(ButtonType.OK)
        dialogPane.contentText = e.toString()
        dialogPane.expandableContent = root

        return dialog
    }
}