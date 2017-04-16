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

import com.almasb.fxgl.asset.FXGLAssets
import com.almasb.fxgl.gameplay.AchievementEvent
import com.almasb.fxgl.gameplay.NotificationEvent
import com.almasb.fxgl.saving.LoadEvent
import com.almasb.fxgl.saving.SaveEvent
import com.almasb.fxgl.scene.DisplayEvent
import com.almasb.fxgl.service.MasterTimer
import com.almasb.fxgl.service.listener.AchievementListener
import com.almasb.fxgl.service.listener.FXGLListener
import com.almasb.fxgl.service.listener.NotificationListener
import com.almasb.fxgl.service.listener.UserProfileSavable
import com.google.inject.Inject
import javafx.animation.AnimationTimer
import javafx.scene.effect.BlendMode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color

/**
 * Initializes global event handlers.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class InitEventHandlersTask
@Inject constructor(private val app: GameApplication) : Runnable {

    private val log = FXGL.getLogger(javaClass)

    override fun run() {
        val bus = app.eventBus

        updateEvents()
        postUpdateEvents()

        scanForServiceListeners()

        displayEvents()

        bus.scanForHandlers(app)

        if (app.settings.isMenuEnabled) {
            // services are now ready and listening, we can generate default profile
            (app.menuListener as MenuEventHandler).generateDefaultProfile()
        }
    }

    private fun updateEvents() {
        val fpsFont = FXGLAssets.UI_MONO_FONT.newFont(20.0)

        app.masterTimer.addUpdateListener(app.audioPlayer)

        app.masterTimer.addUpdateListener({
            if (app.settings.isProfilingEnabled) {
                val g = app.gameScene.graphicsContext

                g.globalBlendMode = BlendMode.SRC_OVER
                g.globalAlpha = 1.0
                g.font = fpsFont
                g.fill = Color.RED
                // TODO:
                //g.fillText(app.profiler.getInfo(), 0.0, app.height - 120.0)
            }
        })
    }

    private fun postUpdateEvents() {
        val postUpdateTimer = object : AnimationTimer() {
            override fun handle(now: Long) {
                app.onPostUpdate(app.masterTimer.tpf())
            }
        }

        app.addFXGLListener(object : FXGLListener {
            override fun onPause() {
                postUpdateTimer.stop()
            }

            override fun onResume() {
                postUpdateTimer.start()
            }

            override fun onReset() {
                //app.getGameWorld().reset()
            }

            override fun onExit() {
                // no-op
            }
        })
    }

    private fun scanForServiceListeners() {
        log.debug("scanForServiceListeners")

        val bus = app.eventBus
        val services = FXGL.getServices()

//        services.map { it.service() }
//                .filter { it.interfaces.contains(FXGLListener::class.java) }
//                .forEach {
//                    log.debug("FXGLListener: $it")
//                    app.addFXGLListener(FXGL.getInstance(it) as FXGLListener)
//                }

        services.map { it.service() }
                .filter { it.interfaces.contains(UserProfileSavable::class.java) }
                .forEach {
                    log.debug("UserProfileSavable: $it")
                    val instance = FXGL.getInstance(it) as UserProfileSavable

                    bus.addEventHandler(SaveEvent.ANY, { instance.save(it.profile) })
                    bus.addEventHandler(LoadEvent.ANY, {
                        if (!(instance is MasterTimer && it.eventType == LoadEvent.RESTORE_SETTINGS)) {
                            instance.load(it.profile)
                        }
                    })
                }

        services.map { it.service() }
                .filter { it.interfaces.contains(NotificationListener::class.java) }
                .forEach {
                    log.debug("NotificationListener: $it")
                    val instance = FXGL.getInstance(it) as NotificationListener

                    bus.addEventHandler(NotificationEvent.ANY, { instance.onNotificationEvent(it) })
                }

        services.map { it.service() }
                .filter { it.interfaces.contains(AchievementListener::class.java) }
                .forEach {
                    log.debug("AchievementListener: $it")
                    val instance = FXGL.getInstance(it) as AchievementListener

                    bus.addEventHandler(AchievementEvent.ANY, { instance.onAchievementEvent(it) })
                }
    }

    private fun displayEvents() {
        app.eventBus.addEventHandler(DisplayEvent.CLOSE_REQUEST, { app.exit() })
    }
}