/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.view

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.getGameScene
import javafx.scene.paint.Color

class PropertyMapTest : GameApplication() {

    override fun initSettings(settings: GameSettings) {
        settings.width = 800
        settings.height = 600
        settings.title = "PropertyMap value display sample"
    }

    override fun initUI() {
        getGameScene().setBackgroundColor(Color.BLACK)
        FXGL.getWorldProperties().setValue("String property", "string value")
        FXGL.getWorldProperties().setValue("Integer property", 100)
        FXGL.getWorldProperties().setValue("Double property", 1001.1001)
        FXGL.getWorldProperties().setValue("Boolean property", true)
        FXGL.getWorldProperties().setValue("Object property", this)

        FXGL.addPropText("String property", 10.0, 20.0)
        FXGL.addPropText("Integer property", 10.0, 40.0)
        FXGL.addPropText("Double property", 10.0, 60.0)
        FXGL.addPropText("Boolean property", 10.0, 80.0)
        FXGL.addPropText("Object property", 10.0, 100.0)

    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(PropertyMapTest::class.java, args)
        }
    }
}