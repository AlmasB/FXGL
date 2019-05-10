/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.trade

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class TradeItem<T>(
        var item: T,
        var name: String,
        var description: String,
        var sellPrice: Int,
        var buyPrice: Int,
        var quantity: Int
)