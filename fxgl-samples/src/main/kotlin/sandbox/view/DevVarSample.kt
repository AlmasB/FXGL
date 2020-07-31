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
import com.almasb.fxgl.dsl.getSettings
import com.almasb.fxgl.dsl.onKeyDown
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color

const val STRING_PROPERTY = "String property"
const val INTEGER_PROPERTY = "Integer property"
const val DOUBLE_PROPERTY = "Double property"
const val BOOLEAN_PROPERTY = "Boolean property"
const val OBJECT_PROPERTY = "Object property"

class DevVarSample : GameApplication() {

    private var clickCounter = 0

    override fun initSettings(settings: GameSettings) {
        settings.width = 800
        settings.height = 600
        settings.title = "PropertyMap value display sample"
    }

    private fun propertiesInit1() {
        FXGL.getWorldProperties().setValue(STRING_PROPERTY, "string value")
        FXGL.getWorldProperties().setValue(INTEGER_PROPERTY, 100)
        FXGL.getWorldProperties().setValue(DOUBLE_PROPERTY, 1001.1001)
        FXGL.getWorldProperties().setValue(BOOLEAN_PROPERTY, true)
        FXGL.getWorldProperties().setValue(OBJECT_PROPERTY, this)
    }

    private fun propertiesInit2() {
        FXGL.getWorldProperties().setValue(STRING_PROPERTY, "STRING VALUE")
        FXGL.getWorldProperties().setValue(INTEGER_PROPERTY, 100100)
        FXGL.getWorldProperties().setValue(DOUBLE_PROPERTY, 1111.1111)
        FXGL.getWorldProperties().setValue(BOOLEAN_PROPERTY, false)
        FXGL.getWorldProperties().setValue(OBJECT_PROPERTY, getSettings())
    }

    override fun initInput() {
        onKeyDown(KeyCode.ENTER) {
            if ((clickCounter++) % 2 == 0)
                propertiesInit2()
            else
                propertiesInit1()
        }
    }

    override fun initUI() {
        getGameScene().setBackgroundColor(Color.BLACK)

        propertiesInit1()

        FXGL.addVarText("String property", 10.0, 20.0)
        FXGL.addVarText("Integer property", 10.0, 40.0)
        FXGL.addVarText("Double property", 10.0, 60.0)
        FXGL.addVarText("Boolean property", 10.0, 80.0)
        FXGL.addVarText("Object property", 10.0, 100.0)

    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(DevVarSample::class.java, args)
        }
    }
}