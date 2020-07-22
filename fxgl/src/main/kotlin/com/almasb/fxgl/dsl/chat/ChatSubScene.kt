/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.chat

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.getUIFactoryService
import com.almasb.fxgl.scene.SubScene
import javafx.collections.FXCollections
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import java.util.function.Consumer

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ChatSubScene(val width: Int, val height: Int) : SubScene() {

    override val isAllowConcurrency: Boolean = true

    // TODO: shouldn't this belong to scene, i.e. a scene should know when it's in the state machine ...
    var isActive = false

    private val users = FXCollections.observableArrayList<String>()
    private val textArea = TextArea()
    private val textInput = TextField()

    internal lateinit var inputHandler: Consumer<String>

    init {
        val bg = Rectangle(width.toDouble(), height.toDouble(), Color.color(0.08, 0.08, 0.1))

        val userViews = FXGL.getUIFactoryService().newListView<String>(users)
        userViews.prefWidth = 200.0
        userViews.prefHeight = bg.height


        textArea.translateX = userViews.prefWidth
        textArea.prefWidth = bg.width - userViews.prefWidth
        textArea.prefHeight = bg.height - 50
        textArea.isEditable = false
        textArea.isWrapText = true
        textArea.styleClass += "fxgl-chat-text-area"
        textArea.font = getUIFactoryService().newFont(14.0)


        textInput.translateX = userViews.prefWidth
        textInput.translateY = textArea.prefHeight + 10.0
        textInput.prefWidth = textArea.prefWidth
        textInput.prefHeight = 15.0
        textInput.styleClass += "fxgl-chat-text-input"
        textInput.font = getUIFactoryService().newFont(14.0)

        textInput.setOnAction {
            inputHandler.accept(textInput.text)

            textInput.clear()
        }

        contentRoot.children.addAll(bg, userViews, textArea, textInput)
    }

    override fun onCreate() {
        isActive = true
    }

    override fun onDestroy() {
        isActive = false
    }

    fun addUser(userName: String) {
        users += userName

        appendMessage("", "$userName joined the chat.")
    }

    fun removeUser(userName: String) {
        users -= userName

        appendMessage("", "$userName left the chat.")
    }

    // TODO: owner type
    fun appendMessage(ownerName: String, message: String) {
        textArea.appendText("$ownerName: $message\n")
    }
}