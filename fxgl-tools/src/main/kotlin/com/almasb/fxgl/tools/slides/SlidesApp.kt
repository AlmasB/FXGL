/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.slides

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.onKeyDown
import javafx.scene.input.KeyCode

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SlidesApp : GameApplication() {

    private val slides = arrayListOf<Slide>()
    private var slideIndex = 0

    override fun initSettings(settings: GameSettings) {
        settings.title = "Slides App"
    }

    override fun initInput() {
        onKeyDown(KeyCode.LEFT) {
            prevSlide()
        }

        onKeyDown(KeyCode.RIGHT) {
            nextSlide()
        }
    }

    private fun prevSlide() {

    }

    private fun nextSlide() {

    }

    override fun initGame() {

    }
}

class Slide {



}

fun main(args: Array<String>) {
    GameApplication.launch(SlidesApp::class.java, args)
}