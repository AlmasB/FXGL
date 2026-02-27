/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.intelligence.facedetect

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
data class Face(
    val id: Int,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)
