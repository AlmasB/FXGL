/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene

import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.input.view.KeyView
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.scene.SubScene
import javafx.scene.input.KeyCode
import javafx.scene.media.MediaView
import javafx.scene.paint.Color

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class VideoScene(private val sceneService: SceneService) : SubScene() {

    private lateinit var video: MediaView
    private lateinit var onFinished: Runnable

    init {
        input.addAction(object : UserAction("Skip Cutscene") {
            override fun onActionBegin() {
                endScene()
            }
        }, KeyCode.ENTER)

        val keyView = KeyView(KeyCode.ENTER, Color.GREENYELLOW, 18.0)
        keyView.translateX = sceneService.prefWidth - 80.0
        keyView.translateY = sceneService.prefHeight - 40.0

        contentRoot.children += keyView
    }

    private fun endScene() {
        contentRoot.children.removeAt(0)

        video.mediaPlayer.stop()
        sceneService.popSubScene()
        onFinished.run()
    }

    fun start(video: MediaView, onFinished: Runnable) {
        this.video = video
        this.onFinished = onFinished

        video.mediaPlayer.play()
        video.mediaPlayer.setOnEndOfMedia { endScene() }

        contentRoot.children.add(0, video)

        sceneService.pushSubScene(this)
    }
}