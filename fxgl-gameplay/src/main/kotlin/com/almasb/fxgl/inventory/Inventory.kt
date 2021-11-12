/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.inventory

import com.almasb.fxgl.logging.Logger
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import java.util.*

/**
 * Represents an inventory (bag) of user-defined items.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Adam Bocco (adam.bocco@gmail.com)
 */
class Inventory<T>(

        /**
         * How many stacks of items can be stored.
         */
        var capacity: Int
) {

    private val log = Logger.get(javaClass)

    /**
     * List of stacks.
     */
    private val items = FXCollections.observableArrayList< ItemStack<T> >()
    private val itemsProperty = FXCollections.unmodifiableObservableList(items)

    /**
     * @return read-only list of stacks
     */
    fun itemsProperty(): ObservableList< ItemStack<T> > = itemsProperty

    /**
     * @return number of stacks
     */
    val size: Int
        get() = items.size

    private val itemsData = hashMapOf<T, ItemData<T>>()

    /**
     * @return a copy of all item data, where K - unique user-defined item, V - its data
     */
    val allData: Map<T, ItemData<T>>
        get() = itemsData.toMap()

    fun getData(item: T): Optional<ItemData<T>> {
        return Optional.ofNullable(itemsData[item])
    }

    /**
     * How many stacks are available before the inventory is full
     */
    val numFreeStacks: Int
        get() = capacity - size

    /**
     * @return true [size] is equal to [capacity]
     */
    val isFull: Boolean
        get() = size == capacity

    /**
     * @return property with the total sum of all [item] stack quantities
     */
    fun itemQuantityProperty(item: T): IntegerProperty {
        return itemsData[item]?.quantityProperty()
                ?: throw IllegalArgumentException("Item $item is not in $itemsData")
    }

    /**
     * @return the total sum of all [item] stack quantities
     */
    fun getItemQuantity(item: T): Int
        = itemQuantityProperty(item).value

    @Deprecated("Use add(ItemConfig)", ReplaceWith("add(com.almasb.fxgl.inventory.ItemConfig)"))
    fun add(item: T, name: String, description: String, view: Node, quantity: Int): Boolean {
        return add(item, ItemConfig(name, description, Int.MAX_VALUE, view), quantity)
    }

    /**
     * Add [item] to inventory, dynamically creating stacks based on [config].
     * If [item] (as checked by hashCode()) is already present, then its quantity is increased by [quantity].
     *
     * @return false if could not add item, otherwise true
     */
    @JvmOverloads fun add(
            item: T,
            config: ItemConfig = ItemConfig(),
            quantity: Int = 1
    ): Boolean {

        // already in inventory, just increment quantity
        if (item in itemsData)
            return incrementQuantity(item, quantity)

        // can't add because inventory is full
        if (isFull)
            return false

        // adding a new item, so check if quantity > 0
        if (quantity < 0) {
            log.warning("Attempted to add a new item with negative quantity. Ignoring")
            return false
        }

        val numStacksNeeded = Math.ceil(quantity.toDouble() / config.maxStackQuantity).toInt()

        // can't add because overflows inventory capacity
        if (numStacksNeeded > numFreeStacks)
            return false

        // capacity-wise we are good, add new item

        val data = ItemData(item).also {
            it.name = config.name
            it.description = config.description
            it.view = config.view
            it.maxStackQuantity = config.maxStackQuantity

            it.incrementQuantity(quantity)
        }

        itemsData[item] = data

        // add newly created stacks
        items += data.stacks

        return true
    }

    /**
     * Removes [item] from inventory, reducing the inventory size by 1.
     * If you only want to reduce quantity, use [incrementQuantity] with negative amount instead.
     */
    fun remove(item: T) {
        items.removeIf { it.userItem == item }
        itemsData -= item
    }

    /**
     * @return true if operation was successful
     */
    fun incrementQuantity(item: T, amount: Int): Boolean {
        if (item !in itemsData) {
            log.warning("Attempted to increment qty of item that is not in inventory. Ignoring")
            return false
        }

        val data = itemsData[item]!!

        // check if we can actually add
        if (amount > 0 && data.maxStackQuantity < Int.MAX_VALUE) {
            val maxPossibleQuantityForItem = numFreeStacks * data.maxStackQuantity + items.filter { it.userItem == item }.size

            if (amount + data.quantity > maxPossibleQuantityForItem) {
                return false
            }
        }

        val isOK = data.incrementQuantity(amount)

        // if all good, update stacks
        if (isOK) {
            // remove non-existent stacks
            items.removeIf { it.userItem == item && it !in data.stacks }

            // add newly created stacks
            data.stacks.forEach {
                if (it !in items) {
                    items += it
                }
            }
        }

        return isOK
    }
}

class ItemData<T> internal constructor(var userItem: T) {

    private val viewProperty = SimpleObjectProperty<Node>()
    private val nameProperty = SimpleStringProperty()
    private val descriptionProperty = SimpleStringProperty()
    private val quantityProperty = SimpleIntegerProperty()

    val stacks = FXCollections.observableArrayList< ItemStack<T> >()

    var maxStackQuantity = Int.MAX_VALUE

    fun viewProperty() = viewProperty
    fun nameProperty() = nameProperty
    fun descriptionProperty() = descriptionProperty

    // TODO: make it read-only
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

    val quantity: Int
        get() = quantityProperty.value

    fun incrementQuantity(amount: Int): Boolean {
        if (amount > 0)
            return fill(amount)

        if (amount < 0)
            return deplete(-amount)

        return true
    }

    /**
     * @param amount - a positive value to increment
     */
    private fun fill(amount: Int): Boolean {
        var left = amount

        // fill existing stacks
        for (stack in stacks) {
            if (stack.quantity < maxStackQuantity) {
                val availableSpace = maxStackQuantity - stack.quantity

                if (left <= availableSpace) {
                    stack.quantity += left
                    quantityProperty.value += left
                    left = 0

                    break
                } else {
                    stack.quantity = maxStackQuantity
                    quantityProperty.value += availableSpace
                    left -= availableSpace
                }
            }
        }

        // if still need to add more

        if (left > 0) {
            val numFullStacks = left / maxStackQuantity
            val remainder = left % maxStackQuantity

            repeat(numFullStacks) {
                stacks += ItemStack(userItem).also { it.quantity = maxStackQuantity }
                quantityProperty.value += maxStackQuantity
            }

            if (remainder > 0) {
                stacks += ItemStack(userItem).also { it.quantity = remainder }
                quantityProperty.value += remainder
            }
        }

        return true
    }

    /**
     * @param amount - a positive value to decrement
     */
    private fun deplete(amount: Int): Boolean {
        // tried to decrease by more than we have
        if (amount > quantityProperty.value)
            return false

        var left = amount

        while (left > 0) {
            val lastStack = stacks.last()

            if (left >= lastStack.quantity) {
                quantityProperty.value -= lastStack.quantity
                left -= lastStack.quantity

                lastStack.quantity = 0
                stacks.removeLast()
            } else {
                quantityProperty.value -= left
                lastStack.quantity -= left

                left = 0
            }
        }

        return true
    }
}

class ItemStack<T>(val userItem: T) {
    private val quantityProperty = SimpleIntegerProperty()

    fun quantityProperty() = quantityProperty

    var quantity: Int
        get() = quantityProperty.value
        set(value) { quantityProperty.value = value }

    val isEmpty: Boolean
        get() = quantity == 0
}

class ItemConfig
@JvmOverloads constructor(
        var name: String = "",
        var description: String = "",
        var maxStackQuantity: Int = Int.MAX_VALUE,
        var view: Node = EmptyView
)

private object EmptyView : Group()