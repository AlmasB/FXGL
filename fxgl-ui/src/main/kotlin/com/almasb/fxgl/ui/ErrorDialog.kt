/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */


package com.almasb.fxgl.ui

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
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ErrorDialog(val error: Throwable) {

    private val dialog: Dialog<ButtonType>

    init {
        val root = GridPane()
        root.isVisible = false
        root.add(Label("Exception stacktrace:"), 0, 0)
        root.add(makeStackTraceArea(), 0, 1)

        dialog = Dialog()
        dialog.title = "Error Reporter"
        dialog.initModality(Modality.APPLICATION_MODAL)

        val dialogPane = dialog.dialogPane
        dialogPane.buttonTypes.addAll(ButtonType.OK)
        dialogPane.content = makeErrorMessageArea()
        dialogPane.expandableContent = root
    }

    fun showAndWait() {
        dialog.showAndWait()
    }

    private fun makeErrorMessageArea() = TextArea(makeErrorMessage()).apply {
        isEditable = false
    }

    private fun makeErrorMessage(): String {
        val name: String
        val line: String

        if (error.stackTrace.isEmpty()) {
            name = "Empty stack trace"
            line = "Empty stack trace"
        } else {
            val trace = error.stackTrace.first()
            name = trace.className.substringAfterLast('.') + "." + trace.methodName + "()"
            line = trace.toString().substringAfter('(').substringBefore(')')
        }

        return "Message:  ${error.message}\n" +
                "Type:  ${error.javaClass.simpleName}\n" +
                "Method:  $name\n" +
                "Line:  $line"
    }

    private fun makeStackTraceArea() = TextArea(makeStackTrace()).apply {
        isEditable = false
        isWrapText = true

        // guesstimate size
        setPrefSize((text.split('\n').maxBy { it.length }?.length ?: 60) * 6.5, (text.count { it == '\n' } + 1) * 20.0)

        GridPane.setVgrow(this, Priority.ALWAYS)
        GridPane.setHgrow(this, Priority.ALWAYS)
    }

    private fun makeStackTrace(): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        error.printStackTrace(pw)
        pw.close()

        return sw.toString()
    }
}