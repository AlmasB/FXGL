/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.util

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
fun <K, V> forEach(map: Map<K, V>, function: BiConsumer<K, V>) {
    map.forEach { function.accept(it.key, it.value) }
}