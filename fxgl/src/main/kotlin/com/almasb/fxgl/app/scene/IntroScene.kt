/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.animation.AnimatedColor
import com.almasb.fxgl.animation.AnimatedCubicBezierPoint2D
import com.almasb.fxgl.animation.AnimatedPoint2D
import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.FXGL.Companion.animationBuilder
import com.almasb.fxgl.dsl.FXGL.Companion.random
import com.almasb.fxgl.dsl.image
import com.almasb.fxgl.texture.toPixels
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.shape.CubicCurve
import javafx.util.Duration
import java.util.function.Consumer

/**
 * Intro animation / video played before game starts
 * if intro is enabled in settings.
 *
 * Call [finishIntro] when your intro completed
 * so that the game can proceed to the next state.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
abstract class IntroScene : FXGLScene() {

    private var introFinished = false

    override fun onCreate() {
        startIntro()
    }

    override fun onUpdate(tpf: Double) {
        if (introFinished) {
            if (FXGL.getSettings().isMainMenuEnabled) {
                controller.gotoMainMenu()
            } else {
                controller.startNewGame()
            }
        }
    }

    /**
     * Closes intro and initializes the next game state, whether it's a menu or game.
     *
     * Note: call this when your intro completes, otherwise
     * the game won't proceed to next state.
     */
    protected fun finishIntro() {
        introFinished = true
    }

    /**
     * Starts the intro animation / video.
     */
    abstract fun startIntro()
}

/**
 * This is the default FXGL Intro animation.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLIntroScene : IntroScene() {

    private val pixels1: MutableList<Pixel>
    private val pixels2: MutableList<Pixel>

    private val fxglLogo = image("intro/fxgl_logo.png")
    private val javafxLogo = image("intro/javafx_logo.png")

    private val g: GraphicsContext
    
    private var delayIndex = 0.0

    init {
        setBackgroundColor(Color.BLACK)

        val fxglLogoPixels = toPixels(fxglLogo)

        pixels1 = fxglLogoPixels
                .filter { (_, _, color) -> color != Color.TRANSPARENT }
                .mapIndexed { index, (x, y, color) ->
                    Pixel(
                            layoutX = x + (appWidth / 2.0 - fxglLogo.width / 2.0),
                            layoutY = y + (appHeight / 2.0 - fxglLogo.height / 2.0),
                            scaleX = 0.0,
                            scaleY = 0.0,
                            fill = color,
                            index = index
                    )
                }.toMutableList()

        pixels2 = toPixels(javafxLogo)
                .filter { (_, _, color) -> color != Color.TRANSPARENT }
                .map { (x, y, color) ->
                    Pixel(layoutX = x + 0.0, layoutY = y + 0.0, fill = color)
                }.toMutableList()

        // add the difference in pixels as TRANSPARENT
        val numPixels = pixels1.size - pixels2.size
        
        pixels2 += fxglLogoPixels.filter { (_, _, color) -> color == Color.TRANSPARENT }
                .take(numPixels)
                .map { (x, y, _) ->
                    Pixel(layoutX = x + 0.0, layoutY = y + 0.0)
                }

        pixels2.sortBy { it.layoutX }
        
        val canvas = Canvas(appWidth.toDouble(), appHeight.toDouble())
        g = canvas.graphicsContext2D
        
        contentRoot.children += canvas
    }

    private fun playAnim1() {
        delayIndex = 0.0

        pixels1.forEach { p ->
            animationBuilder(this)
                    .delay(Duration.seconds(delayIndex + 0.01))
                    .duration(Duration.seconds(1.0))
                    .interpolator(Interpolators.CIRCULAR.EASE_IN_OUT())
                    .animate(AnimatedPoint2D(Point2D(0.0, 0.0), Point2D(1.0, 1.0)))
                    .onProgress(Consumer {
                        p.scaleX = it.x
                        p.scaleY = it.y
                    })
                    .buildAndPlay()

            delayIndex += 0.0001
        }

        timer.runOnceAfter({
            playAnim2()
        }, Duration.seconds(2.0))

        timer.runOnceAfter({
            playAnim3()
        }, Duration.seconds(5.0))

        timer.runOnceAfter({
            finishIntro()
        }, Duration.seconds(7.6))
    }

    private fun playAnim2() {
        delayIndex = 0.0

        val centerX = appWidth / 2.0 - javafxLogo.width / 2.0
        val centerY = appHeight / 2.0 - javafxLogo.height / 2.0

        pixels1.forEach { p ->
            val p2 = pixels2[p.index]

            //val animColor = AnimatedColor(p.fill, p2.fill)

            animationBuilder(this)
                    .delay(Duration.seconds(delayIndex + 0.11))
                    .duration(Duration.seconds(1.05))
                    .interpolator(Interpolators.BACK.EASE_IN_OUT())
                    .animate(AnimatedPoint2D(
                            Point2D(p.translateX, p.translateY),
                            Point2D(centerX - p.layoutX + p2.layoutX, centerY - p.layoutY + p2.layoutY))
                    )

//                    .animate(AnimatedCubicBezierPoint2D(CubicCurve(
//                            p.translateX,
//                            p.translateY,
//                            random(200.0, 400.0),
//                            random(-150.0, 200.0),
//                            random(-300.0, 100.0),
//                            random(400.0, 500.0),
//                            centerX - p.layoutX + p2.layoutX,
//                            centerY - p.layoutY + p2.layoutY))
//                    )
                    .onProgress(Consumer {
                        p.translateX = it.x
                        p.translateY = it.y

//                        p.colorProgress += 0.022
//
//                        val progress = minOf(p.colorProgress, 1.0)
//
//                        p.fill = animColor.getValue(progress, Interpolators.EXPONENTIAL.EASE_IN())
                    })
                    .buildAndPlay()

            delayIndex += 0.0001
        }
    }

    private fun playAnim3() {
        delayIndex = 0.0

        pixels1.forEach { p ->
            animationBuilder(this)
                    .delay(Duration.seconds(delayIndex + 0.11))
                    .duration(Duration.seconds(1.05))
                    .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                    .animate(AnimatedPoint2D(
                            Point2D(p.translateX, p.translateY),
                            Point2D(appWidth * 2.0, appHeight * 2.0))
                    )
//                    .animate(AnimatedCubicBezierPoint2D(CubicCurve(
//                            p.translateX,
//                            p.translateY,
//                            random(200.0, 400.0),
//                            random(-150.0, 200.0),
//                            random(-300.0, 100.0),
//                            random(400.0, 500.0),
//                            appWidth * 2.0,
//                            appHeight * 2.0))
//                    )
                    .onProgress(Consumer {
                        p.translateX = it.x
                        p.translateY = it.y
                    })
                    .buildAndPlay()

            delayIndex += 0.0001
        }
    }

    override fun onUpdate(tpf: Double) {
        super.onUpdate(tpf)
        
        g.clearRect(0.0, 0.0, appWidth.toDouble(), appHeight.toDouble())

        pixels1.forEach {
            g.fill = it.fill
            g.fillRect(it.layoutX + it.translateX, it.layoutY + it.translateY, it.scaleX, it.scaleY)
        }
    }

    override fun startIntro() = playAnim1()
}

private data class Pixel(
        var layoutX: Double = 0.0,
        var layoutY: Double = 0.0,
        var translateX: Double = 0.0,
        var translateY: Double = 0.0,
        var scaleX: Double = 1.0,
        var scaleY: Double = 1.0,
        var fill: Color = Color.TRANSPARENT,
        var index: Int = 0,
        var colorProgress: Double = 0.0
)