/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.trade

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Shop<T>(initialMoney: Int = 0, items: List<TradeItem<T>>) {

    private val propMoney = SimpleIntegerProperty(initialMoney)

    val items: ObservableList<TradeItem<T>> = FXCollections.observableArrayList(items)

    fun moneyProperty(): IntegerProperty = propMoney

    var money: Int
        get() = propMoney.value
        set(value) { propMoney.value = value }

    fun buyFrom(other: Shop<T>, item: TradeItem<T>, qty: Int): Boolean {
        if (item.quantity < qty)
            return false

        if (item !in other.items)
            return false

        val cost = item.buyPrice * qty

        if (money < cost)
            return false

        money -= cost
        other.money += cost

        item.quantity -= qty

        if (item in items) {
            items.find { it.item === item.item }!!.quantity += qty
        } else {
            items += item.copy().also { it.quantity = qty }
        }

        if (item.quantity == 0) {
            other.items -= item
        }

        return true
    }
}