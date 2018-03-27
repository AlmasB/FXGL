/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.devtools

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.asset.FXGLAssets
import com.almasb.fxgl.core.collection.ObjectMap
import com.almasb.fxgl.util.Consumer
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.paint.Color

/**
 * Basic developer console that can be accessed via developer menu bar.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Console : Pane() {

    private val commands = ObjectMap<String, Command>()

    private val typedCommands = com.almasb.fxgl.core.collection.Array<String>()
    private var queueIndex = 0

    private val input: TextField
    private val output: TextArea

    init {
        setPrefSize(FXGL.getApp().width.toDouble(), FXGL.getApp().height.toDouble())
        background = Background(BackgroundFill(Color.color(0.5, 0.5, 0.5, 0.5), null, null))

        output = initOutput()
        input = initInput()

        children.addAll(output, input)

        initCommands()

        //TextFields.bindAutoCompletion(input, commands.keys().toList())
    }

    fun isOpen() = scene != null

    fun open() {
        FXGL.getApp().input.registerInput = false
        FXGL.getApp().gameScene.addUINode(this)
    }

    fun close() {
        FXGL.getApp().gameScene.removeUINode(this)
        FXGL.getApp().input.registerInput = true
    }

    private fun initOutput(): TextArea {
        with(TextArea()) {
            translateX = 50.0
            translateY = 50.0
            prefWidth = FXGL.getApp().width - 50.0 - 50.0
            prefHeight = FXGL.getApp().height - 100.0 - 100.0
            font = FXGLAssets.UI_MONO_FONT.newFont(14.0)
            isEditable = false
            isFocusTraversable = false

            return this
        }
    }

    private fun initInput(): TextField {
        with(TextField()) {
            translateX = 50.0
            translateY = FXGL.getApp().height - 100.0
            prefWidth = FXGL.getApp().width - 50.0 - 50.0
            font = output.font

            setOnAction {

                if (text.isNotEmpty()) {
                    typedCommands.add(text)
                    queueIndex = typedCommands.size()

                    pushMessage("$ $text")
                    parse(text)
                    text = ""
                }
            }

            // add any special behaving keys
            setOnKeyPressed {
                @Suppress("NON_EXHAUSTIVE_WHEN")
                when (it.code) {
                    KeyCode.UP -> prev()
                    KeyCode.DOWN -> next()
                }
            }

            return this
        }
    }

    private fun initCommands() {
        cmd("help", "list available commands", { help() })
        cmd("clear", "clears output", { clear() })
        cmd("exit", "closes console", { exit() })
        cmd("set", "set a property value", { set(it) }, "varName varValue")
        cmd("vars", "list available property variables", { vars() })
    }

    private fun cmd(name: String, description: String, function: (Array<String>) -> Unit, syntax: String = "") {
        commands.put(name, Command(name, description, Consumer { function.invoke(it) }, syntax))
    }

    private fun prev() {
        if (queueIndex > 0) {
            queueIndex--
            setInput(typedCommands.get(queueIndex))
        }
    }

    private fun next() {
        if (queueIndex < typedCommands.size() - 1) {
            queueIndex++
            setInput(typedCommands.get(queueIndex))
        } else if (queueIndex == typedCommands.size() - 1) {
            setInput("")
        }
    }

    private fun setInput(message: String) {
        input.text = message
    }

    private fun pushMessage(message: String) {
        output.appendText(message + "\n")
    }

    private fun parse(input: String) {
        val tokens = input.split(" +".toRegex())
        val cmdName = tokens[0]

        val cmd = commands.get(cmdName)

        if (cmd == null) {
            pushMessage("$cmdName command not recognized")
            return
        }

        val params = tokens.drop(1).toTypedArray()

        try {
            cmd.function.accept(params)
        } catch (e: IndexOutOfBoundsException) {
            pushMessage("Incorrect number of parameters")
            pushMessage("$cmd")
        } catch (e: Exception) {
            pushMessage("Parse failed: $e")
            pushMessage("$cmd")
        }
    }

    private fun help() {
        commands.forEach { pushMessage("${it.value}") }
    }

    private fun clear() {
        output.text = ""
    }

    private fun exit() {
        close()
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
            pushMessage("Property with name $varName does not exist!")
        }
    }

    private fun vars() {
        FXGL.getApp().gameState.getProperties().forEach {
            pushMessage("${it.key} = ${it.value}")
        }
    }

    data class Command(
            val name: String,
            val description: String,
            val function: Consumer<Array<String>>,
            val syntax: String = "") {

        override fun toString(): String {
            val s = if (syntax.isNotEmpty()) "$syntax " else ""

            return "$name $s// $description"
        }
    }
}