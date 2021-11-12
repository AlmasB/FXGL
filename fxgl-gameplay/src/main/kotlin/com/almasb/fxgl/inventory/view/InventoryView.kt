/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.inventory.view

import com.almasb.fxgl.inventory.Inventory
import javafx.collections.FXCollections
import javafx.scene.Parent
import javafx.scene.layout.VBox

/**
 *
 * @author Adam Bocco (adambocco@gmail.com)
 */
class InventoryView<T>(inventory: Inventory<T>) : Parent(){
    private val box: VBox = VBox(5.0)
    val listView: InventoryListView<T>

    init {

        box.translateX = 25.0
        box.translateY = 25.0

        listView = InventoryListView(FXCollections.observableArrayList(inventory.itemsProperty().map { it.userItem }), inventory)
        listView.translateX = 10.0
        listView.translateY = 25.0

        children.addAll(box, listView)
    }
}