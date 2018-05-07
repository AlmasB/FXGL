/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.util

/**
 * Contains API introduced in Java 8 but compatible with Java 7.
 * Designed to be called from Java.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */


fun <K, V> forEach(map: Map<K, V>, function: BiConsumer<K, V>) {
    map.forEach { function.accept(it.key, it.value) }
}

fun <T> forEach(iterable: Iterable<T>, function: Consumer<T>) {
    iterable.forEach { function.accept(it) }
}

fun max(iterable: Iterable<Double>) {

}