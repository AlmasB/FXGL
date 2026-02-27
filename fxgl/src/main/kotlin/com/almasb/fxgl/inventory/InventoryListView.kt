/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.inventory

import javafx.beans.binding.Bindings
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.util.Callback

/**
 *
 * @author Adam Bocco (adambocco@gmail.com)
 */
class InventoryListView<T>(inventory: Inventory<T>) : ListView<ItemStack<T>>() {

    init {
        cellFactory = Callback { InventoryListCell(inventory) }

        Bindings.bindContent(items, inventory.itemsProperty())
    }
}

class InventoryListCell<T>(private val inventory: Inventory<T>) : ListCell<ItemStack<T>>() {

    override fun updateItem(item: ItemStack<T>?, empty: Boolean) {
        super.updateItem(item, empty)
        if (empty || item == null) {
            text = null
            graphic = null

        } else {
            val itemData = inventory.getData(item.userItem).get()

            val nameBinding = Bindings.createStringBinding({
                val limit = 20

                if (itemData.name.length > limit)
                    itemData.name.substring(0, limit) + "..."
                else
                    itemData.name
            }, itemData.nameProperty())

            val label = Text()
            label.textProperty().bind(nameBinding.concat(" x").concat(itemData.quantityProperty()))
            label.fill = Color.WHITE

            text = null
            graphic = label
        }
    }
}