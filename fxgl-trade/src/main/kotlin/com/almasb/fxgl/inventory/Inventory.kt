/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.inventory

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Node

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Inventory<T>(var capacity: Int) {

    private val items = FXCollections.observableArrayList<InventoryItem<T>>()
    private val itemsProperty = FXCollections.unmodifiableObservableList(items)

    val isFull: Boolean
        get() = items.size == capacity

    /**
     * @return read-only list of items
     */
    fun itemsProperty(): ObservableList<InventoryItem<T>> = itemsProperty

    fun add(item: InventoryItem<T>): Boolean {
        if (isFull)
            return false

        items += item

        return true
    }

    fun remove(item: InventoryItem<T>) {
        items -= item
    }
}

class InventoryItem<T>
@JvmOverloads constructor(
        var userItem: T,
        var view: Node,
        var name: String,
        var description: String = "",
        var qty: Int = 1) {

}