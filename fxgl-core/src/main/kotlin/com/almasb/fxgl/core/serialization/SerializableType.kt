/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.serialization

/**
 * Marks a type as serializable.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface SerializableType {

    /**
     * Write state to [bundle].
     */
    fun write(bundle: Bundle)

    /**
     * Read state from [bundle].
     */
    fun read(bundle: Bundle)
}