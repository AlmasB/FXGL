/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.animation.AnimatedPoint2D
import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.dsl.animationBuilder
import com.almasb.fxgl.dsl.image
import com.almasb.fxgl.dsl.random
import com.almasb.fxgl.texture.toPixels
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
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

    internal var onFinished: Runnable = EmptyRunnable

    override fun onCreate() {
        startIntro()
    }

    /**
     * Closes intro and initializes the next game state, whether it's a menu or game.
     *
     * Note: call this when your intro completes, otherwise
     * the game won't proceed to next state.
     */
    protected fun finishIntro() {
        onFinished.run()
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
        
        val canvas = Canvas(appWidth.toDouble(), appHeight.toDouble())
        g = canvas.graphicsContext2D
        
        contentRoot.children += canvas
    }

    private fun playAnim1() {
        delayIndex = 0.0

        pixels1.forEach { p ->
            animationBuilder(this)
                    .autoReverse(true)
                    .repeat(2)
                    .delay(Duration.seconds(delayIndex + 0.001))
                    .duration(Duration.seconds(0.8))
                    .interpolator(Interpolators.SMOOTH.EASE_OUT())
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
        }, Duration.seconds(3.0))

        timer.runOnceAfter({
            playAnim3()
        }, Duration.seconds(5.5))

        timer.runOnceAfter({
            finishIntro()
        }, Duration.seconds(7.9))
    }

    private fun playAnim2() {
        delayIndex = 0.0

        val centerX = appWidth / 2.0 - javafxLogo.width / 2.0
        val centerY = appHeight / 2.0 - javafxLogo.height / 2.0

        pixels1.forEach { p ->
            val p2 = pixels2[p.index]

            val offsetX = random(-250, 250)
            val offsetY = random(-250, 250)

            val baseX = centerX - p.layoutX + p2.layoutX
            val baseY = centerY - p.layoutY + p2.layoutY

            p.translateX = baseX + offsetX
            p.translateY = baseY + offsetY
            p.fill = p2.fill

            animationBuilder(this)
                    .delay(Duration.seconds(delayIndex + 0.001))
                    .duration(Duration.seconds(0.75))
                    .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                    .animate(AnimatedPoint2D(Point2D(0.0, 0.0), Point2D(1.0, 1.0)))
                    .onProgress(Consumer {
                        p.scaleX = it.x
                        p.scaleY = it.y

                        p.translateX = baseX + offsetX - offsetX * it.x * it.x
                        p.translateY = baseY + offsetY - offsetY * it.y * it.x
                    })
                    .buildAndPlay()

            delayIndex += 0.0001
        }
    }

    private fun playAnim3() {
        delayIndex = 0.0

        pixels1.sortedBy { it.layoutX }.forEach { p ->
            animationBuilder(this)
                    .delay(Duration.seconds(random(delayIndex, delayIndex + 0.21)))
                    .duration(Duration.seconds(1.05))
                    .interpolator(Interpolators.EXPONENTIAL.EASE_IN())
                    .animate(AnimatedPoint2D(
                            Point2D(p.translateX, p.translateY),
                            Point2D(appWidth * 2.0, appHeight / 2.0))
                    )
                    .onProgress(Consumer {
                        p.translateX = it.x
                        p.translateY = it.y
                    })
                    .buildAndPlay()

            delayIndex += 0.0001
        }
    }

    override fun onUpdate(tpf: Double) {
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