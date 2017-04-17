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

package sandbox

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.asset.FXGLAssets
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.settings.GameSettings
import javafx.animation.ParallelTransition
import javafx.animation.RotateTransition
import javafx.animation.ScaleTransition
import javafx.animation.TranslateTransition
import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.input.KeyCode
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LectureSample : GameApplication() {

    override fun initSettings(settings: GameSettings) {
        with(settings) {
            width = 1920
            height = 1080
            isFullScreen = true
            isMenuEnabled = false
            isIntroEnabled = false
            isProfilingEnabled = false
            isCloseConfirmation = false
        }
    }

    override fun initInput() {
        input.addAction(object : UserAction("Next") {
            override fun onActionBegin() {
                nextAction()
            }
        }, KeyCode.RIGHT)

        input.addAction(object : UserAction("Prev") {
            override fun onActionBegin() {
                prevAction()
            }
        }, KeyCode.LEFT)

        input.addAction(object : UserAction("Exit") {
            override fun onActionBegin() {
                exit()
            }
        }, KeyCode.L)
    }

    private val slides = arrayListOf<SlideView>()
    private var currentSlide = 0

    override fun initGame() {
        addSlide("JavaFX Scene Graph", VBox())

        addSlide("Outline", VBox(
                text("Scene Graph"),
                text("Key Terminology")
        ))

        addSlide("Scene Graph", VBox(
                sceneGraph()
        ))

        addSlide("Terminology", VBox(
                text("Translate"),
                text("Rotate"),
                text("Scale")
        ))

        addSlide("Translate", VBox(
                text("Translate along X, Y or Z axis by amount"),
                translateSlide()
        ))

        addSlide("Rotate", VBox(
                text("Rotate along X, Y or Z axis by angle"),
                rotateSlide()
        ))

        addSlide("Scale", VBox(
                text("Scale along X, Y or Z axis by amount"),
                scaleSlide()
        ))

        addSlide("Conclusion", VBox(
                text("Scene Graph"),
                text("Key Terminology")
        ))

        slides.forEachIndexed { i, slide ->
            if (i > 0)
                slide.translateX = width.toDouble()
            else
                gameScene.addUINode(slide)
        }
    }

    private fun addSlide(title: String, content: Node) {
        slides.add(SlideView(title, content))
    }

    private var animating = false

    private fun nextAction() {
        if (animating)
            return

        if (currentSlide < slides.size - 1) {
            val node = slides[currentSlide]
            val tt = TranslateTransition(Duration.seconds(0.5), node)
            tt.fromX = 0.0
            tt.toX = -width.toDouble()
            tt.setOnFinished { gameScene.removeUINode(node) }

            currentSlide++

            val tt2 = TranslateTransition(Duration.seconds(0.5), slides[currentSlide])
            tt2.fromX = width.toDouble()
            tt2.toX = 0.0

            gameScene.addUINode(slides[currentSlide])

            animating = true
            with(ParallelTransition(tt, tt2)) {
                setOnFinished { animating = false }
                play()
            }
        }
    }

    private fun prevAction() {
        if (animating)
            return

        if (currentSlide > 0) {
            val node = slides[currentSlide]
            val tt = TranslateTransition(Duration.seconds(0.5), slides[currentSlide])
            tt.fromX = 0.0
            tt.toX = width.toDouble()
            tt.setOnFinished { gameScene.removeUINode(node) }

            currentSlide--

            val tt2 = TranslateTransition(Duration.seconds(0.5), slides[currentSlide])
            tt2.fromX = -width.toDouble()
            tt2.toX = 0.0

            gameScene.addUINode(slides[currentSlide])

            animating = true
            with(ParallelTransition(tt, tt2)) {
                setOnFinished { animating = false }
                play()
            }
        }
    }

    private fun translateSlide(): Node {
        val rect = Rectangle(120.0, 120.0, Color.BLUE)

        val tt = TranslateTransition(Duration.seconds(3.5), rect)
        tt.toX = width / 3.0
        tt.isAutoReverse = true
        tt.cycleCount = Int.MAX_VALUE
        tt.play()

        return rect
    }

    private fun rotateSlide(): Node {
        val rect = Rectangle(120.0, 120.0, Color.BLUE)
        //rect.translateX = width / 2.0

        val tt = RotateTransition(Duration.seconds(3.5), rect)
        tt.toAngle = 180.0
        tt.isAutoReverse = true
        tt.cycleCount = Int.MAX_VALUE
        tt.play()

        return rect
    }

    private fun scaleSlide(): Node {
        val rect = Rectangle(120.0, 120.0, Color.BLUE)
        //rect.translateX = width / 2.0

        val tt = ScaleTransition(Duration.seconds(3.5), rect)
        tt.toX = 0.1
        tt.toY = 0.1
        tt.isAutoReverse = true
        tt.cycleCount = Int.MAX_VALUE
        tt.play()

        return rect
    }

    private fun sceneGraph(): Node {
        return assetLoader.loadTexture("scenegraph.jpg", 800.0, 800.0)
//        val pane = Pane()
//        pane.translateX = width / 2.0 - 350 / 2
//
//        val node0 = GraphNode("Root (StackPane)")
//        val node1 = GraphNode("Branch (VBox)")
//        node1.translateY = 100.0
//
//        pane.children.addAll(node0, Line(node0.translateX + 170, node0.translateY, node1.translateX + 170, node1.translateY), node1)
//
//        return pane
    }
}

class GraphNode(val nodeName: String) : StackPane() {
    init {
        val node = Rectangle(350.0, 50.0, null)
        node.arcWidth = 25.0
        node.arcHeight = 25.0
        node.stroke = Color.BLUE

        val text = Text(nodeName)
        text.font = Font.font("Segoe UI", 36.0)

        children.addAll(node, text)
    }
}

// TODO: Slide data structure?
// TODO: each view should have its own animation timeline
// TODO: use scaling vs height hardcode?
class SlideView(val title: String, val content: Node) : StackPane() {

    init {
        setPrefSize(FXGL.getAppWidth().toDouble(), FXGL.getAppHeight().toDouble())

        (content as VBox).alignment = Pos.CENTER

        val root = VBox(prefHeight / 6, FXGL.getUIFactory().newText(title, Color.DARKBLUE, prefHeight / 12), content)
        root.alignment = Pos.CENTER
        root.padding = Insets(0.0, 10.0, 0.0, 10.0)

        children.addAll(root)
    }
}

fun text(content: String): Text {
    val text = FXGL.getUIFactory().newText(content, Color.BLACK, 42.0)
    text.font = Font.font("Segoe UI", FXGL.getAppHeight().toDouble() / 15.0)
    return text
}

fun main(args: Array<String>) {
    Application.launch(LectureSample::class.java, *args)
}