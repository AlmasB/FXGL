/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.trade

import javafx.collections.ObservableList
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TradeListView(items: ObservableList<TradeItem<*>>) : ListView<TradeItem<*>>(items) {

    init {
        styleClass.setAll("fxgl-trade-list-view")

        stylesheets.add(javaClass.getResource("fxgl_trade.css").toExternalForm())

        cellFactory = Callback { TradeListCell() }
    }
}

class TradeListCell : ListCell<TradeItem<*>>() {

    override fun updateItem(item: TradeItem<*>?, empty: Boolean) {
        super.updateItem(item, empty)

        if (empty || item == null) {
            text = null
            graphic = null
        } else {
            val qty = if (item.quantity == 1) "" else " (${item.quantity})"

            val limit = 30

            val name = if (item.name.length > limit) item.name.substring(0, limit) + "..." else item.name

            text = "$name$qty - ${item.buyPrice}"
        }
    }
}