package com.almasb.fxgl.dsl

import com.almasb.fxgl.app.Engine
import com.almasb.fxgl.app.FXGLApplication
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.time.Timer
import javafx.scene.Group
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class FXGLTest {

    @Test
    fun addPropText() {
        val app = object : GameApplication() {
            override fun initSettings(settings: GameSettings) { }
        }

        val settings = GameSettings()
        val engine = Engine(settings.toReadOnly())
        engine.initServices()

        FXGL.inject(engine, app, FXGLApplication())
        val worldProperties = FXGL.getWorldProperties()
    }
}