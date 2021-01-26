/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.inventory

import com.almasb.fxgl.logging.Logger
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import java.util.*

/**
 * Represents an inventory (bag) of user-defined items.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Inventory<T>(var capacity: Int) {

    private val log = Logger.get(javaClass)

    private val items = FXCollections.observableArrayList<T>()
    private val itemsProperty = FXCollections.unmodifiableObservableList(items)

    private val itemsData = hashMapOf<T, ItemData<T>>()

    /**
     * Number of items in inventory.
     */
    val size: Int
        get() = items.size

    val allData: Map<T, ItemData<T>>
        get() = itemsData.toMap()

    val isFull: Boolean
        get() = size == capacity

    var isRemoveItemsIfQty0 = false

    /**
     * @return read-only list of items
     */
    fun itemsProperty(): ObservableList<T> = itemsProperty

    /**
     * Add [item] to inventory.
     * If [item] (as checked by equals()) is already present, then its quantity is increased by [quantity]
     * (i.e. the inventory size does not change).
     * Otherwise the item is added to inventory (the inventory size is increased by 1).
     */
    @JvmOverloads fun add(
            item: T,
            name: String = "",
            description: String = "",
            view: Node = EmptyView,
            quantity: Int = 1
    ): Boolean {

        if (item in items) {
            getData(item).get().quantity += quantity
            return true
        }

        if (isFull)
            return false

        itemsData[item] = ItemData(item).also {
            it.name = name
            it.description = description
            it.view = view
            it.quantity = quantity
        }

        items += item

        return true
    }

    /**
     * Removes [item] from inventory, reducing the inventory size by 1.
     * If you only want to reduce quantity, use [incrementQuantity] with negative amount instead.
     */
    fun remove(item: T) {
        items -= item
        itemsData -= item
    }

    fun getData(item: T): Optional<ItemData<T>> {
        if (item !in items)
            return Optional.empty()

        return Optional.of(itemsData[item]!!)
    }

    // TODO: allow setting max item quantity

    /**
     * @return true if operation was successful
     */
    fun incrementQuantity(item: T, amount: Int): Boolean {
        if (item !in items) {
            log.warning("Attempted to increment qty of item that is not in inventory. Ignoring")
            return false
        }

        val data = getData(item).get()

        // can't go less than 0
        if (amount < 0 && data.quantity + amount < 0)
            return false

        data.quantity += amount

        if (isRemoveItemsIfQty0 && data.quantity == 0)
            remove(item)

        return true
    }
}

class ItemData<T> internal constructor(var userItem: T) {

    private val viewProperty = SimpleObjectProperty<Node>()
    private val nameProperty = SimpleStringProperty()
    private val descriptionProperty = SimpleStringProperty()
    private val quantityProperty = SimpleIntegerProperty()

    fun viewProperty() = viewProperty
    fun nameProperty() = nameProperty
    fun descriptionProperty() = descriptionProperty
    fun quantityProperty() = quantityProperty

    var view: Node
        get() = viewProperty.value
        set(value) { viewProperty.value = value }

    var name: String
        get() = nameProperty.value
        set(value) { nameProperty.value = value }

    var description: String
        get() = descriptionProperty.value
        set(value) { descriptionProperty.value = value }

    var quantity: Int
        get() = quantityProperty.value
        set(value) { quantityProperty.value = value }
}

private object EmptyView : Group()