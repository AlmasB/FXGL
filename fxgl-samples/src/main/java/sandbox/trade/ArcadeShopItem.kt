/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.trade

import javafx.scene.Node

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class ArcadeShopItem(
        val name: String,
        val description: String,
        val costPerLevel: Int,
        val maxLevel: Int,
        var level: Int,
        val view: Node)