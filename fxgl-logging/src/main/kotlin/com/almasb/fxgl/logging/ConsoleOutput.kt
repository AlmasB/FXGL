package com.almasb.fxgl.logging

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ConsoleOutput : LoggerOutput {

    override fun append(message: String) {
        println(message)
    }

    override fun close() {}
}