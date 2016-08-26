/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.gameplay.qte

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.ui.UIFactory
import com.google.inject.Inject
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.util.Duration
import java.util.*
import java.util.concurrent.ScheduledFuture
import java.util.function.Consumer

/**
 * FXGL default QTE service provider.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QTEProvider
@Inject private constructor() : QTE {

    private val eventHandler: EventHandler<KeyEvent>
    private lateinit var scheduledAction: ScheduledFuture<*>

    private val fxScene: Scene

    private val closeButton = Button()
    private val keysBox = HBox(10.0)

    private val queue = ArrayDeque<KeyCode>()
    private val labels = ArrayDeque<Text>()

    private lateinit var callback: Consumer<Boolean>

    init {
        fxScene = FXGL.getDisplay().currentScene.root.scene

        closeButton.isVisible = false

        keysBox.alignment = Pos.CENTER

        eventHandler = EventHandler<KeyEvent> {

            val k = queue.poll()

            if (k == it.code) {

                val label = labels.poll()
                label.fill = Color.YELLOW

                if (queue.isEmpty()) {
                    close()
                    callback.accept(true)
                }

            } else {
                close()
                callback.accept(false)
            }
        }
    }

    private fun show() {
        FXGL.getDisplay().showBox("QTE!", keysBox, closeButton)

        fxScene.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler)
    }

    private fun close() {
        scheduledAction.cancel(true)

        queue.clear()
        labels.clear()

        fxScene.removeEventHandler(KeyEvent.KEY_PRESSED, eventHandler)

        closeButton.fire()
    }

    override fun start(callback: Consumer<Boolean>, duration: Duration, vararg keys: KeyCode) {
        if (keys.isEmpty())
            throw IllegalArgumentException("At least 1 key must be specified")

        if (queue.isNotEmpty())
            throw IllegalStateException("Cannot start more than 1 QTE at a time")

        this.callback = callback

        queue.addAll(keys)
        labels.addAll(
                keys.map { UIFactory.newText(it.getName(), Color.WHITE, 72.0) }
        )

        keysBox.children.setAll(labels)

        show()

        scheduledAction = FXGL.getExecutor().schedule( {

            if (queue.isNotEmpty()) {
                Platform.runLater {

                    close()
                    callback.accept(false)
                }
            }
        }, duration)
    }
}