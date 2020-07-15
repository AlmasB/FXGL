/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.dsl.getAppHeight
import com.almasb.fxgl.dsl.getAppWidth
import com.almasb.fxgl.logging.stackTraceToString
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.ui.MDIWindow
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox

class ErrorSubScene(val error: Throwable, val action: Runnable) : SubScene() {

    init {
        val btnOK = Button("Exit game")
        btnOK.setOnAction {
            action.run()
        }

        val scrollPane = ScrollPane(makeStackTraceArea())
        scrollPane.setPrefSize(getAppWidth().toDouble(), getAppHeight().toDouble())

        val window = MDIWindow()
        window.canClose = false
        window.title = "Error Reporter"
        window.contentPane.children += VBox(btnOK, scrollPane)

        contentRoot.children += window
    }

    private fun makeStackTraceArea() = TextArea(makeErrorMessage() + "\n\n" + makeStackTrace()).apply {
        isEditable = false
        isWrapText = true

        // guesstimate size
        setPrefSize(getAppWidth().toDouble(), (text.count { it == '\n' } + 1) * 20.0)
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

    private fun makeStackTrace() = error.stackTraceToString()
}