/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.cutscene

import com.almasb.fxgl.util.EmptyRunnable
import java.util.function.Supplier

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class DialogLine(
        val id: Int,
        val data: String) {

    var used = false
    var reusable: Boolean = false
    //var closing: Boolean = false

    var precondition: Supplier<Boolean> = Supplier { true }
    var postAction: Runnable = EmptyRunnable
}