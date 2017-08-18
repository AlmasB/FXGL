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
        val label = Label("Exception stacktrace:")
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        error.printStackTrace(pw)
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

        dialog = Dialog<ButtonType>()
        dialog.title = "Uncaught Exception"
        dialog.initModality(Modality.APPLICATION_MODAL)

        val dialogPane = dialog.dialogPane
        dialogPane.buttonTypes.addAll(ButtonType.OK)
        dialogPane.contentText = error.toString()
        dialogPane.expandableContent = root
    }

    fun showAndWait() {
        dialog.showAndWait()
    }
}