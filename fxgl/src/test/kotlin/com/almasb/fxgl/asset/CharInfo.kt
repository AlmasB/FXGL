/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.asset

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class CharInfo

@JvmOverloads constructor(var hp: Int = 0,
                    var mana: Double = 0.0,
                    var name: String = "",
                    var killable: Boolean = false) {
}
