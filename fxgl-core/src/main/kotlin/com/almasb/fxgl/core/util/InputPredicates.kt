/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util

import java.util.function.Predicate

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InputPredicates {

    companion object {

        @JvmField val ALPHANUM = Predicate<String> { input -> input.matches("^[\\p{L}\\p{N}]+$".toRegex()) }
    }
}