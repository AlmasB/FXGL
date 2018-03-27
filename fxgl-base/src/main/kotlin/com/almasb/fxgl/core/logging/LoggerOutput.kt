package com.almasb.fxgl.core.logging

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface LoggerOutput {

    fun append(message: String)

    fun close()
}