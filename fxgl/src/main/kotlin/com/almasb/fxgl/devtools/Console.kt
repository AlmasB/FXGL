/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.devtools

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.collection.ObjectMap
import com.almasb.fxgl.util.Splitter
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import org.controlsfx.control.textfield.TextFields
import java.util.function.Consumer


/**
 * Basic developer console that can be accessed via developer menu bar.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Console : Pane() {

    private val commands = ObjectMap<String, Command>()

    private val output: Text

    init {
        setPrefSize(FXGL.getApp().width, FXGL.getApp().height)
        background = Background(BackgroundFill(Color.color(0.5, 0.5, 0.5, 0.5), null, null))

        output = FXGL.getUIFactory().newText("", 18.0)
        with(output) {
            translateX = 50.0
            translateY = 50.0
        }

        val input = TextField()
        with(input) {
            translateX = 50.0
            translateY = FXGL.getApp().height - 100.0
            prefWidth = FXGL.getApp().width - 50.0 - 50.0

            setOnAction {

                if (text.isNotEmpty()) {
                    pushMessage(text)
                    parse(text)
                    text = ""
                }
            }

            // in case we want any special behaving keys
            setOnKeyPressed {
                when (it.code) {
                    //KeyCode.TAB ->
                }
            }
        }

        children.addAll(output, input)

        initCommands()

        TextFields.bindAutoCompletion(input, commands.keys().toList())
    }

    fun isOpen() = scene != null

    private fun initCommands() {
        cmd("help", "list available commands", 0, 0, { help() })
        cmd("set", "set a property value", 2, 2, { set(it) })
    }

    private fun cmd(name: String, description: String, minParams: Int, maxParams: Int, function: (Array<String>) -> Unit) {
        commands.put(name, Command(name, description, minParams, maxParams, Consumer { function.invoke(it) }))
    }

    private fun pushMessage(message: String) {
        output.text += message + "\n"
    }

    private fun parse(input: String) {
        val tokens = Splitter.split(input)
        val cmdName = tokens[0]

        val cmd = commands.get(cmdName)

        if (cmd == null) {
            pushMessage("$cmdName command not recognized")
            return
        }

        val params = tokens.drop(1).toTypedArray()

        if (params.size !in cmd.minParams..cmd.maxParams) {
            pushMessage("$cmdName requires min: ${cmd.minParams} and max: ${cmd.maxParams}. Supplied: ${params.size}")
            return
        }

        cmd.function.accept(params)
    }

    private fun help() {
        commands.forEach { pushMessage("${it.value}") }
    }

    private fun set(params: Array<String>) {
        val varName = params[0]
        val varValue = params[1]

        val gameState = FXGL.getApp().gameState

        if (gameState.exists(varName)) {
            val varType = gameState.getType(varName)

            when (varType) {
                SimpleBooleanProperty::class.java -> gameState.setValue(varName, java.lang.Boolean.parseBoolean(varValue))
                SimpleIntegerProperty::class.java -> gameState.setValue(varName, java.lang.Integer.parseInt(varValue))
                SimpleDoubleProperty::class.java -> gameState.setValue(varName, java.lang.Double.parseDouble(varValue))
                SimpleStringProperty::class.java -> gameState.setValue(varName, varValue)
                else -> pushMessage("Unknown property type: $varType")
            }

        } else {
            pushMessage("Property with $varName does not exist!")
        }
    }

    data class Command(
            val name: String,
            val description: String,
            val minParams: Int,
            val maxParams: Int,
            val function: Consumer<Array<String>>) {

        override fun toString(): String {
            return "$name ($minParams-$maxParams) : $description"
        }
    }
}