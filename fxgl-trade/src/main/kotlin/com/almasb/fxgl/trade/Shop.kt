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

interface ShopListener<T> {

    /**
     * Called when this shop sells [item] to another shop.
     */
    fun onSold(item: TradeItem<T>)

    /**
     * Called when this shop buys [item] from another shop.
     */
    fun onBought(item: TradeItem<T>)
}

class Shop<T>(initialMoney: Int, items: List<TradeItem<T>>) {

    constructor(items: List<TradeItem<T>>) : this(0, items)

    var listener: ShopListener<T>? = null

    val items: ObservableList<TradeItem<T>> = FXCollections.observableArrayList(items)

    private val propMoney = SimpleIntegerProperty(initialMoney)

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

        // transaction is successful
        money -= cost
        other.money += cost

        val copy = item.copy().also { it.quantity = qty }
        listener?.onBought(copy)
        other.listener?.onSold(copy)

        item.quantity -= qty

        if (item in items) {
            items.find { it == item }!!.quantity += qty
        } else {
            items += item.copy().also { it.quantity = qty }
        }

        if (item.quantity == 0) {
            other.items -= item
        }

        return true
    }
}

