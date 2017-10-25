/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.notification

import com.almasb.fxgl.ui.Position
import javafx.scene.paint.Color

/**
 * Represents a notification message.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Notification internal constructor(val message: String,
                                        val textColor: Color,
                                        val bgColor: Color,
                                        val position: Position) {
}