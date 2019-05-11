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
class TradeItem<T>(
        var item: T,
        var name: String,
        var description: String,
        var sellPrice: Int,
        var buyPrice: Int,
        var quantity: Int
) {

    override fun hashCode(): Int {
        return item.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TradeItem<*>)
            return false

        return item === other.item
    }

    fun copy(): TradeItem<T> = TradeItem(item, name, description, sellPrice, buyPrice, quantity)
}