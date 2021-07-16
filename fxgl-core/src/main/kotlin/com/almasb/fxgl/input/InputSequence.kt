/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.scene.input.KeyCode
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InputSequence(firstKey: KeyCode, secondKey: KeyCode, vararg furtherKeys: KeyCode) {

    private val sequence = listOf(firstKey, secondKey) + furtherKeys

    val lastKey = sequence.last()

    fun matches(queue: Queue<KeyCode>): Boolean {
        if (queue.size < sequence.size)
            return false

        val window = queue.drop(queue.size - sequence.size).take(sequence.size)

        return window == sequence
    }
}