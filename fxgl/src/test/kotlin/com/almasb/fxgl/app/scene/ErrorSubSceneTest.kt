/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.app.scene

import com.almasb.fxgl.test.RunWithFX
import com.almasb.fxgl.ui.MDIWindow
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.RuntimeException

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class ErrorSubSceneTest {

    @Test
    fun `Error subscene contains crash information and runs given post-action`() {
        var count = 0
        val error = RuntimeException("Test Error Message")

        val subScene = ErrorSubScene(800.0, 600.0, error) { count++ }

        val window = subScene.contentRoot.children[0] as MDIWindow
        val btn = (window.contentPane.children[0] as VBox).children[0] as Button
        val textArea = ((window.contentPane.children[0] as VBox).children[1] as ScrollPane).content as TextArea

        assertThat(textArea.text, containsString("Test Error Message"))

        // check post-action

        btn.fire()

        assertThat(count, `is`(1))
    }
}