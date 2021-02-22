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

    private val itemsData = hashMapOf<T, ObservableList<ItemData<T>>>()

    private var stackMax = Int.MAX_VALUE

    /**
     * Number of items in inventory.
     */
    val size: Int
        get() {
            var totalSize = 0
            for (item in items) {
                totalSize += itemsData[item]?.size!!
            }
            return totalSize
        }


    val allData: Map<T, ObservableList<ItemData<T>>>
        get() = itemsData.toMap()

    val isFull: Boolean
        get() = size == capacity

    var isRemoveItemsIfQty0 = false

    fun getStackMax(): Int {
        return stackMax
    }

    /**
     * @return true if operation was successful
     * Cannot set stackMax less than item with highest quantity in inventory
     */
    fun setStackMax(newStackMax: Int): Boolean {
        if (newStackMax >= stackMax) {
            stackMax = newStackMax
        }
        for (item in allData.values) {
            for (stack in item) {
                if (stack.quantity > newStackMax) return false
            }
        }
        stackMax = newStackMax

        return true
    }

    /**
     * @return read-only list of items
     */
    fun itemsProperty(): ObservableList<T> = itemsProperty

    /**
     * @returns false if inventory is full, if adding quantity creates stacks greater than capacity, or reducing quantity past 0
     * Add [item] to inventory.
     * If [item] (as checked by equals()) is already present, then its quantity is increased by [quantity]
     * Inventory size may change if adding quantity greater than stackMax
     */
    @JvmOverloads fun add(
            item: T,
            name: String = "",
            description: String = "",
            view: Node = EmptyView,
            quantity: Int = 1
    ): Boolean {

        if (item in items) {
            return incrementQuantity(item, quantity)
        }

        if (isFull)
            return false

        itemsData[item] = FXCollections.observableArrayList()

        itemsData[item]?.add(ItemData(item).also {
            it.name = name
            it.description = description
            it.view = view
        })

        items += item

        if (quantity <= stackMax) {
            itemsData[item]?.get(0)?.quantity = quantity
            return true
        }
        else {
            itemsData[item]?.get(0)?.quantity = stackMax

            // If quantity can't fit in one stack, create more
            if (incrementQuantity(item, quantity-stackMax)) {
                return true
            }
            // If not enough room in inventory, remove initially added item
            else {
                removeItem(item)
                return false
            }

        }
    }

    /**
     * Removes [item] from inventory, reducing the inventory size by 1.
     * If you only want to reduce quantity, use [incrementQuantity] with negative amount instead.
     */
    fun removeItem(item: T) {
        items -= item
        itemsData -= item
    }

    private fun removeStack(itemData: ItemData<T>) {
        if (itemData.userItem in items)
            itemsData[itemData.userItem]?.remove(itemData)
    }

    fun getData(item: T): Optional<ObservableList<ItemData<T>>> {
        if (item !in items)
            return Optional.empty()

        return Optional.of(itemsData[item]!!)
    }

    /**
     * @return sum quantity of all type [item] in inventory
     */
    fun getItemQuantity(item:T): Int {
        if (item !in items) return 0
        var totalItemSize = 0
        for (stack in getData(item).get()) {
            totalItemSize += stack.quantity
        }
        return totalItemSize
    }

    /**
     * @return true if operation was successful
     */
    fun incrementQuantity(item: T, amount: Int): Boolean {
        if (item !in items) {
            log.warning("Attempted to increment qty of item that is not in inventory. Ignoring")
            return false
        }

        val stacks = getData(item).get()

        // Details used for creating new stacks
        val itemTemplate = stacks[0]

        // Check if there is room in inventory to add all quantity
        val freeSpace = capacity - size
        val itemQuantity = getItemQuantity(item)
        var quantityCount = amount

        for (stack in stacks) {
            quantityCount -= stackMax - stack.quantity
        }

        if (freeSpace < quantityCount.toDouble()/stackMax)
            return false

        // can't go less than 0
        if (amount < 0 && itemQuantity + amount < 0)
            return false


        quantityCount = amount

        /**
         * If incrementing with negative numbers, reduce or remove stacks of [item] until [amount] has been subtracted
         */
        if (amount < 0) {
            for (stack in stacks.reversed()) {

                if (quantityCount >= 0) break

                if (quantityCount >= -stack.quantity) {
                    stack.quantity += quantityCount

                    quantityCount = 0
                }
                else {
                    quantityCount += stack.quantity

                    stack.quantity = 0

                }

                if (isRemoveItemsIfQty0 && stack.quantity == 0) {
                    removeStack(stack)

                    if (getItemQuantity(item) == 0)
                        removeItem(item)
                }
            }
        }

        /**
         * If incrementing with positive numbers, fill non-full stacks first, then create new item stacks
         */
        else {
            for (stack in stacks) {

                if (quantityCount <= stackMax - stack.quantity) {
                    stack.quantity += quantityCount

                    quantityCount = 0

                    break
                }
                else if (stack.quantity < stackMax){
                    quantityCount -= stackMax - stack.quantity

                    stack.quantity = stackMax

                }
            }
            // Create new item stacks until amount has been added
            while (quantityCount > 0) {
                var newItemQty: Int

                if (quantityCount < stackMax ) {
                    newItemQty = quantityCount

                    quantityCount = 0
                }
                else {
                    newItemQty = stackMax

                    quantityCount -= stackMax
                }

                itemsData[item]?.add(ItemData(item).also {
                    it.name = itemTemplate.name
                    it.description = itemTemplate.description
                    it.view = itemTemplate.view
                    it.quantity = newItemQty
                })
            }
        }
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