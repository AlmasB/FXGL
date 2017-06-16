/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.rpg.quest

import javafx.scene.paint.Color

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
enum class QuestState(val color: Color) {
    ACTIVE(Color.TRANSPARENT),
    COMPLETED(Color.color(0.25, 1.0, 0.4)),
    FAILED(Color.RED)
}