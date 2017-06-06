/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene.intro

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.scene.IntroScene
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView

/**
 * Intro that uses a video file instead of animation.
 * The video file must be placed under /assets/video/ .
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class VideoIntroScene(videoName: String) : IntroScene() {

    private val videoPlayer: MediaPlayer

    init {
        val media = Media(javaClass.getResource("/assets/video/$videoName").toExternalForm())
        videoPlayer = MediaPlayer(media)
        videoPlayer.onEndOfMedia = Runnable {
            finishIntro()
            videoPlayer.dispose()
        }

        val view = MediaView(videoPlayer)
        view.fitWidth = FXGL.getSettings().width.toDouble()
        view.fitHeight = FXGL.getSettings().height.toDouble()

        contentRoot.children.add(view)
    }

    override fun startIntro() {
        videoPlayer.play()
    }
}