package com.almasb.fxgl.logging

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface LoggerOutput {

    fun append(message: String)

    fun close()
}