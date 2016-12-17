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

package com.almasb.fxgl.app

import com.almasb.fxgl.event.*
import com.google.inject.Inject
import javafx.animation.AnimationTimer
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.text.Font

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class InitEventHandlersTask
@Inject constructor(private val app: GameApplication) : Runnable {

    override fun run() {
        val bus = app.eventBus

        val isMac = System.getProperty("os.name").contains("Mac")
        val fpsFont = Font.font(if (isMac) "Monaco" else "Lucida Console", if (isMac) 18.0 else 20.0)

        // Main tick

        app.masterTimer.addUpdateListener(app.input)
        app.masterTimer.addUpdateListener(app.audioPlayer)
        app.masterTimer.addUpdateListener(app.gameWorld)
        app.masterTimer.addUpdateListener({ event ->
            app.onUpdate(event.tpf())

            if (app.settings.isProfilingEnabled) {
                val g = app.gameScene.getGraphicsContext()

                g.setFont(fpsFont)
                g.setFill(Color.RED)
                g.fillText(app.profiler.getInfo(), 0.0, app.getHeight() - 120)
            }
        })

        val postUpdateTimer = object : AnimationTimer() {
            override fun handle(now: Long) {
                app.onPostUpdate(app.masterTimer.tpf())
            }
        }

        // Save/Load events

        bus.addEventHandler(SaveEvent.ANY, { event ->
            app.getInput().save(event.getProfile())
            app.getDisplay().save(event.getProfile())
            app.getAudioPlayer().save(event.getProfile())
            app.getAchievementManager().save(event.getProfile())
            app.getMasterTimer().save(event.getProfile())
        })

        bus.addEventHandler(LoadEvent.ANY, { event ->
            app.getInput().load(event.getProfile())
            app.getDisplay().load(event.getProfile())
            app.getAudioPlayer().load(event.getProfile())
            app.getAchievementManager().load(event.getProfile())

            if (event.getEventType() != LoadEvent.RESTORE_SETTINGS) {
                app.getMasterTimer().load(event.getProfile())
            }
        })

        // Core listeners

        app.addFXGLListener(app.getInput())
        app.addFXGLListener(app.getMasterTimer())
        app.addFXGLListener(object : FXGLListener {
            override fun onPause() {
                postUpdateTimer.stop()
                app.setState(ApplicationState.PAUSED)
            }

            override fun onResume() {
                postUpdateTimer.start()
                app.setState(ApplicationState.PLAYING)
            }

            override fun onReset() {
                app.getGameWorld().reset()
            }

            override fun onExit() { }
        })

        app.getGameWorld().addWorldListener(app.getPhysicsWorld())
        app.getGameWorld().addWorldListener(app.getGameScene())

        // Scene

        app.getGameScene().addEventHandler(MouseEvent.ANY, { app.getInput().onMouseEvent(it, app.getGameScene().getViewport(), app.display.scaleRatio) })
        app.getGameScene().addEventHandler(KeyEvent.ANY, { app.getInput().onKeyEvent(it) })

        bus.addEventHandler(NotificationEvent.ANY, EventHandler<NotificationEvent> { app.getAudioPlayer().onNotificationEvent(it) })

        bus.addEventHandler(AchievementEvent.ANY, EventHandler<AchievementEvent> { app.getNotificationService().onAchievementEvent(it) })

        // FXGL App

        bus.addEventHandler(DisplayEvent.CLOSE_REQUEST, { e -> app.exit() })
        bus.addEventHandler(DisplayEvent.DIALOG_OPENED, { e ->
            if (app.getState() === ApplicationState.INTRO || app.getState() === ApplicationState.LOADING)
                return@addEventHandler

            if (!app.isMenuOpen())
                app.pause()

            app.getInput().onReset()
        })
        bus.addEventHandler(DisplayEvent.DIALOG_CLOSED, { e ->
            if (app.getState() === ApplicationState.INTRO || app.getState() === ApplicationState.LOADING)
                return@addEventHandler

            if (!app.isMenuOpen())
                app.resume()
        })
    }
}