/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.inventory.view

import com.almasb.fxgl.inventory.Inventory
import javafx.collections.ObservableList
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback

/**
 *
 * @author Adam Bocco (adambocco@gmail.com)
 */
class InventoryListView<T> (items: ObservableList<T>, inventory: Inventory<T>) : ListView<T>(items) {

    init {
        cellFactory = Callback { InventoryListCell(inventory) }
    }
}

class InventoryListCell<T>(private val inventory: Inventory<T>) : ListCell<T>() {

    override fun updateItem(item: T?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty || item == null) {
            text = null
            graphic = null

        } else {
            val itemData = inventory.getData(item)
            val limit = 30
            val name = if (itemData[0].name.length > limit) itemData[0].name.substring(0, limit) + "..." else itemData[0].name

            text = "$name - ${itemData[0].quantity} \n${itemData[0].description}"
        }
    }
}