///*
// * The MIT License (MIT)
// *
// * FXGL - JavaFX Game Library
// *
// * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package s21levelparsing
//
//import com.almasb.fxgl.app.ApplicationMode
//import com.almasb.fxgl.app.GameApplication
//import com.almasb.fxgl.entity.Entities
//import com.almasb.fxgl.parser.TextLevelParser
//import com.almasb.fxgl.settings.GameSettings
//import javafx.application.Application
//import javafx.scene.paint.Color
//import javafx.scene.shape.Rectangle
//
///**
// * This is an example of a basic FXGL game application in Kotlin.
// *
// * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
// *
// */
//class LevelParsingSampleKt : com.almasb.fxgl.app.GameApplication() {
//
//    private val BLOCK_SIZE = 200.0
//
//    override fun initSettings(settings: com.almasb.fxgl.settings.GameSettings) {
//
//        with(settings) {
//            width = 800
//            height = 600
//            title = "LevelParsingSampleKt"
//            version = "0.1"
//            isFullScreen = false
//            isIntroEnabled = false
//            isMenuEnabled = false
//            setProfilingEnabled(true)
//            applicationMode = ApplicationMode.DEVELOPER
//        }
//    }
//
//    override fun initInput() { }
//
//    override fun initAssets() { }
//
//    override fun initGame() {
//        val parser = TextLevelParser()
//        parser.emptyChar = '0'
//
//        parser.addEntityProducer('1', { x, y -> com.almasb.fxgl.entity.Entities.builder()
//                .at(x * BLOCK_SIZE, y * BLOCK_SIZE)
//                .viewFromNode(Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.RED))
//                .build() })
//
//        parser.addEntityProducer('2', { x, y -> com.almasb.fxgl.entity.Entities.builder()
//                .at(x * BLOCK_SIZE, y * BLOCK_SIZE)
//                .viewFromNode(Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.GREEN))
//                .build() })
//
//        val level = parser.parse("level0.txt")
//
//        gameWorld.setLevel(level)
//    }
//
//    override fun initPhysics() { }
//
//    override fun initUI() { }
//
//    override fun onUpdate(tpf: Double) { }
//}
//
//fun main(args: Array<String>) {
//    Application.launch(LevelParsingSampleKt::class.java, *args)
//}