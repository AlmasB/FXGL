/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.anim

import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameSettings
import com.almasb.fxgl.dsl.FXGL.Companion.addUINode
import com.almasb.fxgl.dsl.FXGL.Companion.animationBuilder
import com.almasb.fxgl.dsl.FXGL.Companion.getGameScene
import com.almasb.fxgl.dsl.FXGL.Companion.onKeyDown
import com.almasb.fxgl.dsl.getUIFactoryService
import javafx.beans.binding.Bindings
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import java.util.HashMap

class ScaleAndRotateSample : GameApplication() {

    private val animations = HashMap<Int, Runnable>()
    private var animationSelector = 0


    override fun initSettings(settings: GameSettings) {
        settings.width = 800
        settings.height = 600
        settings.title = "Rotation and scale sample"
    }

    override fun initInput() {
        onKeyDown(KeyCode.F, Runnable {
            animations[animationSelector++]?.run()
            animationSelector %= animations.size
        })
    }

    override fun initGame() {
        getGameScene().setBackgroundColor(Color.BLACK)

        val rect = Rectangle(400.0, 100.0, Color.WHITE)

        addUINode(rect, 200.0, 250.0)

        animations[0] = Runnable {
            animationBuilder()
                    .duration(Duration.seconds(2.0))
                    .interpolator(Interpolators.LINEAR.EASE_IN_OUT())
                    .repeat(2)
                    .autoReverse(true)
                    .rotate(rect)
                    .origin(Point2D(0.0, 0.0))
                    .from(0.0)
                    .to(360.0)
                    .buildAndPlay()
        }

        animations[1] = Runnable {
            animationBuilder()
                    .duration(Duration.seconds(2.0))
                    .interpolator(Interpolators.LINEAR.EASE_IN_OUT())
                    .repeat(2)
                    .autoReverse(true)
                    .scale(rect)
                    .origin(Point2D(0.0, 0.0))
                    .from(Point2D(1.0, 1.0))
                    .to(Point2D(2.0,2.0))
                    .buildAndPlay()
        }

        animations[2] = Runnable {
            animationBuilder()
                    .duration(Duration.seconds(2.0))
                    .interpolator(Interpolators.LINEAR.EASE_IN_OUT())
                    .repeat(2)
                    .autoReverse(true)
                    .rotate(rect)
                    .from(0.0)
                    .to(360.0)
                    .buildAndPlay()
        }

        animations[3] = Runnable {
            animationBuilder()
                    .duration(Duration.seconds(2.0))
                    .interpolator(Interpolators.LINEAR.EASE_IN_OUT())
                    .repeat(2)
                    .autoReverse(true)
                    .scale(rect)
                    .from(Point2D(1.0, 1.0))
                    .to(Point2D(2.0,2.0))
                    .buildAndPlay()
        }

        val text = getUIFactoryService().newText("")
        text.textProperty().bind(Bindings.size(rect.transforms).asString("Num transforms: %d"))

        addUINode(text, 20.0, 20.0)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(ScaleAndRotateSample::class.java, args)
        }
    }
}