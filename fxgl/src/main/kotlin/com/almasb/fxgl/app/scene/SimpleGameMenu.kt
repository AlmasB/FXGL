/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.getSettings
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.Duration

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SimpleGameMenu : FXGLMenu(MenuType.GAME_MENU) {

    private val masker = Rectangle(FXGL.getAppWidth().toDouble(), FXGL.getAppHeight().toDouble(), Color.color(0.0, 0.0, 0.0, 0.25))
    private val content: Pane

    private val animation: Animation<*>

    init {
        content = createContentPane()
        content.children.add(createContent())

        content.translateX = FXGL.getAppWidth() / 2.0 - 125
        content.translateY = FXGL.getAppHeight() / 2.0 - 200

        contentRoot.children.setAll(masker, content)

        animation = FXGL.animationBuilder()
                .duration(Duration.seconds(0.5))
                .interpolator(Interpolators.BACK.EASE_OUT())
                .translate(content)
                .from(Point2D(FXGL.getAppWidth() / 2.0 - 125, -400.0))
                .to(Point2D(FXGL.getAppWidth() / 2.0 - 125, FXGL.getAppHeight() / 2.0 - 200))
                .build()
    }

    override fun onCreate() {
        animation.onFinished = EmptyRunnable
        animation.start()
    }

    override fun onUpdate(tpf: Double) {
        animation.onUpdate(tpf)
    }

    private fun createContentPane(): StackPane {
        return StackPane(FXGL.texture("pause_menu_bg.png"))
    }

    private fun createContent(): Parent {
        val btnResume = FXGL.getUIFactoryService().newButton(FXGL.localizedStringProperty("menu.resume"))
        btnResume.setOnAction {
            fireResume()
        }

        val btnExit = FXGL.getUIFactoryService().newButton(FXGL.localizedStringProperty("menu.exit"))
        btnExit.setOnAction {
            if (getSettings().isMainMenuEnabled) {
                fireExitToMainMenu()
            } else {
                fireExit()
            }
        }

        val vbox = VBox(15.0, btnResume, btnExit)
        vbox.alignment = Pos.CENTER
        vbox.setPrefSize(250.0, 400.0)

        return vbox
    }
}