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

    fun hasItem(item: T): Boolean = item in itemsData

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
     * @return false if could not add item and no modifications were made, otherwise true
     */
    @JvmOverloads fun add(
            item: T,
            config: ItemConfig = ItemConfig(),
            quantity: Int = 1
    ): Boolean {

        // already in inventory, just increment quantity
        if (hasItem(item))
            return incrementQuantity(item, quantity)

        // can't add because inventory is full
        if (isFull)
            return false

        // TODO: if == 0?
        // adding a new item, so check if quantity > 0
        if (quantity < 0) {
            log.warning("Attempted to add a new item with negative quantity. Ignoring")
            return false
        }

        val numStacksNeeded = Math.ceil(quantity.toDouble() / config.maxStackQuantity).toInt()

        // can't add because overflows inventory capacity
        if (numStacksNeeded > numFreeStacks)
            return false

        // capacity-wise we are good, add new item will succeed

        val data = ItemData(item).also {
            it.name = config.name
            it.description = config.description
            it.view = config.view
            it.maxStackQuantity = config.maxStackQuantity

            // TODO: should we delegate to incrementQuantity(item, amount)
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
     * @return true if operation was successful, if returns false then no modifications were made
     */
    fun incrementQuantity(item: T, amount: Int): Boolean {
        // meaningless call
        if (amount == 0)
            return false

        if (!hasItem(item)) {
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

        // if decrementing, check that we have enough
        if (amount < 0 && -amount > data.quantity)
            return false

        // all checks passed, we will modify this inventory
        data.incrementQuantity(amount)

        // remove non-existent stacks
        items.removeIf { it.userItem == item && it !in data.stacks }

        // add newly created stacks
        data.stacks.forEach {
            if (it !in items) {
                items += it
            }
        }

        // if we reached 0, remove item from inventory
        if (data.quantity == 0) {
            itemsData -= item
        }

        return true
    }

    /**
     * Transfer [item] with [quantity] amount from [other] to this inventory.
     *
     * @return true if operation was successful, if returns false then no modifications were made to either inventory objects
     */
    @JvmOverloads fun transferFrom(other: Inventory<T>, item: T, quantity: Int = 1): Boolean {
        if (!other.hasItem(item))
            return false

        if (other.getItemQuantity(item) < quantity)
            return false

        val isOK = add(item, quantity = quantity)

        if (isOK) {
            return other.incrementQuantity(item, -quantity)
        }

        return false
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

    /**
     * This operation is meant to always succeed since checks should be done before the call.
     */
    internal fun incrementQuantity(amount: Int) {
        if (amount > 0)
            fill(amount)
        else if (amount < 0)
            deplete(-amount)
    }

    /**
     * @param amount - a positive value to increment
     */
    private fun fill(amount: Int) {
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
    }

    /**
     * @param amount - a positive value to decrement
     */
    private fun deplete(amount: Int) {
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