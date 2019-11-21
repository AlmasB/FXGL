/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package sandbox.view

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.FXGL.Companion.debug
import com.almasb.fxgl.dsl.FXGL.Companion.entityBuilder
import com.almasb.fxgl.dsl.FXGL.Companion.getGameTimer
import com.almasb.fxgl.dsl.FXGL.Companion.onKeyDown
import com.almasb.fxgl.dsl.components.view.GenericBarViewComponent
import dev.DeveloperWASDControl
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class BasicEntityViewApp : GameApplication() {
    override fun initSettings(settings: GameSettings) {}
    override fun initInput() {
        onKeyDown(KeyCode.F, "test", Runnable { debug("Game time: " + getGameTimer().now) })
    }

    override fun initGame() {
        entityBuilder()
                .at(100.0, 100.0)
                .view(Rectangle(100.0, 100.0, Color.BLUE))
                .with(DeveloperWASDControl())
                .with(GenericBarViewComponent(0.0, 100.0, Color.GREEN, 2999.0, 4003.0))
                .buildAndAttach()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(BasicEntityViewApp::class.java, args)
        }
    }
}