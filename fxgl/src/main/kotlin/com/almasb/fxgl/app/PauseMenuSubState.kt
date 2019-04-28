/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.core.local.Local
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.scene.SubScene
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import java.util.Collections.addAll

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal object PauseMenuSubState : SubScene() {

    private val masker = Rectangle(FXGL.getAppWidth().toDouble(), FXGL.getAppHeight().toDouble(), Color.color(0.0, 0.0, 0.0, 0.25))
    private val content: Pane

    private var canSwitchGameMenu = true

    private val animation: Animation<*>

    init {
        input.addAction(object : UserAction("Resume") {
            override fun onActionBegin() {
                requestHide()
            }

            override fun onActionEnd() {
                unlockSwitch()
            }
        }, FXGL.getSettings().menuKey)

        content = createContentPane()
        content.children.add(createContent())

        content.translateX = FXGL.getAppWidth() / 2.0 - 125
        content.translateY = FXGL.getAppHeight() / 2.0 - 200

        contentRoot.children.addAll(masker, content)

        animation = FXGL.animationBuilder()
                .duration(Duration.seconds(0.5))
                .translate(content)
                .from(Point2D(FXGL.getAppWidth() / 2.0 - 125, -400.0))
                .to(Point2D(FXGL.getAppWidth() / 2.0 - 125, FXGL.getAppHeight() / 2.0 - 200))
                .build()

        animation.animatedValue.interpolator = Interpolators.BACK.EASE_OUT()
    }

    override fun onEnter(prevState: Scene) {
        animation.onFinished = EmptyRunnable
        animation.start()
    }

    override fun onUpdate(tpf: Double) {
        animation.onUpdate(tpf)
    }

    internal fun requestShow(onShow: () -> Unit) {
        if (canSwitchGameMenu) {
            canSwitchGameMenu = false
            onShow()
        }
    }

    private fun requestHide() {
        if (animation.isAnimating)
            return

        if (canSwitchGameMenu) {
            canSwitchGameMenu = false

            animation.onFinished = Runnable {
                FXGL.getGameController().popSubScene()
            }
            animation.startReverse()
        }
    }

    internal fun unlockSwitch() {
        canSwitchGameMenu = true
    }

    private fun createContentPane(): StackPane {
        return StackPane(FXGL.texture("pause_menu_bg.png"))
    }

    private fun createContent(): Parent {
        val btnResume = FXGL.getUIFactory().newButton(Local.localizedStringProperty("menu.resume"))
        btnResume.setOnAction {
            requestHide()
            unlockSwitch()
        }

        val btnExit = FXGL.getUIFactory().newButton(Local.localizedStringProperty("menu.exit"))
        btnExit.setOnAction {
            FXGL.getGameController().exit()
        }

        val vbox = VBox(15.0, btnResume, btnExit)
        vbox.alignment = Pos.CENTER
        vbox.setPrefSize(250.0, 400.0)

        return vbox
    }
}