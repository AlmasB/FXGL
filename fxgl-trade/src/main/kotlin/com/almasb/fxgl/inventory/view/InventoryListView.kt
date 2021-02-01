/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.inventory.view

import com.almasb.fxgl.inventory.Inventory
import com.almasb.fxgl.inventory.ItemData
import javafx.collections.ObservableList
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback

/**
 *
 * @author Adam Bocco (adambocco@gmail.com)
 */
class InventoryListView(items: ObservableList<ItemData<*>>) : ListView<ItemData<*>>(items) {

    init {
        cellFactory = Callback { InventoryListCell() }
    }
}

class InventoryListCell() : ListCell<ItemData<*>>() {

    override fun updateItem(item: ItemData<*>?, empty: Boolean) {

        super.updateItem(item, empty)

        if (empty || item == null) {
            text = null
            graphic = null
        } else {

            val limit = 30
            val name = if (item.name.length > limit) item.name.substring(0, limit) + "..." else item.name

            text = "$name - ${item.quantity} \n${item.description}"
        }
    }
}