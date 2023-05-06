/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection

/**
 * A "buffer" queue that keeps track of the moving average based on all elements.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MovingAverageQueue(private val maxSize: Int) {

    private var sum = 0.0

    var average = 0.0
        private set

    private val queue = ArrayDeque<Double>(maxSize)

    fun put(item: Double) {
        if (queue.size == maxSize) {
            val oldItem = queue.removeFirst()

            sum -= oldItem
        }

        queue.addLast(item)

        sum += item
        average = sum / queue.size
    }
}