/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.ui

import com.almasb.fxgl.test.RunWithFX
import javafx.scene.Group
import javafx.scene.Scene
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class UITest {

    // for now just initialize and see if they are fine in a scene graph
    @Test
    fun `FXGL UI Controls`() {
        val uiFactory = FXGLUIFactoryServiceProvider()

        val button = FXGLButton()
        val choiceBox = FXGLChoiceBox<String>()
        val scrollPane = FXGLScrollPane()
        val spinner = FXGLSpinner<String>()
        val progressBar = ProgressBar()
        val checkBox = FXGLCheckBox()
        val listView = FXGLListView<String>()
        val textFlow = FXGLTextFlow(uiFactory)

        val group = Group(
                button,
                choiceBox,
                scrollPane,
                spinner,
                progressBar,
                checkBox,
                listView,
                textFlow
        )

        val scene = Scene(group)
    }
}