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

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.settings.GameSettings
import javafx.application.Application
import kotlinx.coroutines.experimental.*
import java.util.*

typealias F = Map<String, String>

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class KotlinSample : GameApplication() {

    private val map: F = HashMap<String, String>()

    override fun initSettings(settings: GameSettings) {
        with(settings) {
            isIntroEnabled = false
            isMenuEnabled = false
            isCloseConfirmation = false
            isProfilingEnabled = false
        }
    }

    override fun initGame() {
        println("initGame() ${Thread.currentThread().name}")

        // start doing something
        val deferredResult = async(CommonPool) {
            println("async() ${Thread.currentThread().name}")

            delay(3000)

            println("async Done with 999")

            999
        }

        kotlinx.coroutines.experimental.launch(CommonPool) {
            val workValue = doHeavyWork()

            println(workValue)
        }

        Thread.sleep(2000)

        println("initGame Done")



        //println("${deferredResult.getCompleted()}")
    }

    private suspend fun doHeavyWork(): Int {
        delay(1000)
        return 119
    }
}

fun main(args: Array<String>) {
    Application.launch(KotlinSample::class.java, *args)
}