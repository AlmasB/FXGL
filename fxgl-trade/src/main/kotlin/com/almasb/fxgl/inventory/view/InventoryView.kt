/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.inventory.view

import com.almasb.fxgl.inventory.Inventory
import com.almasb.fxgl.inventory.ItemData
import javafx.collections.ObservableList
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InventoryView<T>(val inventory: Inventory<T>, width: Int, height: Int) : Pane(){
    private val box: VBox
    val listView: InventoryListView

    init {
        setPrefSize(width.toDouble(), height.toDouble())

        val bg = Rectangle(width.toDouble(), height.toDouble())

        bg.fill = Color.rgb(0, 0, 0, 0.6)

        box = VBox(5.0)
        box.translateX = 25.0
        box.translateY = 25.0

        listView = InventoryListView(inventory.itemsProperty() as ObservableList<ItemData<*>>)
        listView.translateX = 10.0
        listView.translateY = 25.0
        listView.setPrefSize((width - 20).toDouble(), (height - 50 - 40).toDouble())

        children.addAll(bg, box, listView)
    }
}