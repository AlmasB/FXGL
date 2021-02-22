/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.inventory

import javafx.scene.shape.Rectangle
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InventoryTest {

    private lateinit var inventory: Inventory<String>

    @BeforeEach
    fun setUp() {
        inventory = Inventory(25)
    }

    @Test
    fun `Inventory capacity`() {
        assertThat(inventory.capacity, `is`(25))
        assertFalse(inventory.isFull)

        repeat(24) { index ->
            inventory.add("$index")
        }

        assertFalse(inventory.isFull)

        inventory.add("100")

        assertTrue(inventory.isFull)

        inventory.removeItem("100")

        assertFalse(inventory.isFull)
    }

    @Test
    fun `Add new and remove items`() {
        inventory.add("Hello")

        assertThat(inventory.size, `is`(1))
        assertThat(inventory.getItemQuantity("Hello"), `is`(1))

        // we are adding the same thing (as defined by equals()), so size remains the same
        // but quantity should change
        inventory.add("Hello")

        assertThat(inventory.size, `is`(1))
        assertThat(inventory.getItemQuantity("Hello"), `is`(2))

        inventory.add("Hello", quantity = 30)

        assertThat(inventory.size, `is`(1))
        assertThat(inventory.getItemQuantity("Hello"), `is`(32))

        inventory.incrementQuantity("Hello", -31)

        assertThat(inventory.size, `is`(1))
        assertThat(inventory.getItemQuantity("Hello"), `is`(1))

        inventory.removeItem("Hello")

        assertThat(inventory.size, `is`(0))
    }

    @Test
    fun `ItemData is generated for each new item`() {
        assertTrue(inventory.allData.isEmpty())

        inventory.add("Hello")

        assertThat(inventory.allData.size, `is`(1))

        val itemData = inventory.allData["Hello"]!!
        assertThat(itemData[0].userItem, `is`("Hello"))
        assertThat(itemData[0].quantity, `is`(1))

        val view = Rectangle()

        inventory.add("Hi", "name", "description", view, 3)

        assertThat(inventory.allData.size, `is`(2))

        val itemData2 = inventory.allData["Hi"]!!
        assertThat(itemData2[0].userItem, `is`("Hi"))
        assertThat(itemData2[0].name, `is`("name"))
        assertThat(itemData2[0].description, `is`("description"))
        assertThat(itemData2[0].view, `is`(view))
        assertThat(itemData2[0].quantity, `is`(3))

        assertThat(itemData2[0].nameProperty().value, `is`("name"))
        assertThat(itemData2[0].descriptionProperty().value, `is`("description"))
        assertThat(itemData2[0].viewProperty().value, `is`(view))
        assertThat(itemData2[0].quantityProperty().value, `is`(3))
    }

    @Test
    fun `Auto-remove items if quantity is 0`() {
        assertFalse(inventory.isRemoveItemsIfQty0)

        inventory.add("Hello", quantity = 2)

        inventory.incrementQuantity("Hello", -2)

        assertThat(inventory.itemsProperty().size, `is`(1))

        inventory.isRemoveItemsIfQty0 = true

        assertTrue(inventory.isRemoveItemsIfQty0)

        inventory.incrementQuantity("Hello", 2)

        // should remove item "Hello" since the flag is on
        inventory.incrementQuantity("Hello", -2)


        assertThat(inventory.itemsProperty().size, `is`(0))
    }

    @Test
    fun `getData returns empty optional if no item`() {
        assertFalse(inventory.getData("bla-bla").isPresent)
    }

    @Test
    fun `Cannot add items beyond capacity`() {
        inventory = Inventory(2)

        assertTrue(inventory.add("Hi1"))
        assertTrue(inventory.add("Hi2"))
        assertFalse(inventory.add("Hi3"))
    }

    @Test
    fun `Cannot increment quantity if item not present`() {
        assertFalse(inventory.incrementQuantity("bla-bla", 3))
    }

    @Test
    fun `Cannot increment by negative quantity if item does not have enough quantity`() {
        inventory.add("Hello", quantity = 3)

        assertFalse(inventory.incrementQuantity("Hello", -5))
    }

    @Test
    fun `New items are created when adding quantity of items past stackMax`() {
        inventory.setStackMax(3)

        inventory.add("Hello", quantity = 7)

        // Should create 3 stacks of quantities: [3, 3, 1]
        assertThat(inventory.size, `is`(3))

        // Should create 5 stacks of quantities: [3, 3, 3, 3, 2]
        inventory.add("Hello", quantity = 7)

        assertThat(inventory.size, `is`(5))
    }

    @Test
    fun `New items are created when incrementing quantity past stackMax`() {
        inventory.setStackMax(3)

        inventory.add("Hello", quantity = 3)

        // Should create stacks: [3, 3, 3, 1]
        inventory.incrementQuantity("Hello", 7)

        assertThat(inventory.size, `is`(4))

        // Should increment stacks: [3, 3, 3, 3, 3]
        inventory.incrementQuantity("Hello", 5)

        assertThat(inventory.size, `is`(5))
    }

    @Test
    fun `getItemQuantity returns total quantity of all item's stacks`() {
        inventory.setStackMax(3)

        // Stacks should be: [3, 3, 3, 3, 3]
        inventory.add("Hello", quantity = 15)

        assertThat(inventory.size, `is`(5))

        assertTrue(inventory.incrementQuantity("Hello", -10))

        // Stacks should be: [3, 2, 0, 0, 0]
        assertThat(inventory.size, `is`(5))
        assertThat(inventory.getItemQuantity("Hello"), `is`(5))

        //Stacks should be: [3, 3, 3, 3, 3, 1]
        inventory.incrementQuantity("Hello", 11)

        assertThat(inventory.size, `is`(6))
        assertThat(inventory.getItemQuantity("Hello"), `is` (16))
    }

    @Test
    fun `Stacks are removed when incrementing quantity when isRemoveItemsIfQty0 is true`() {
        inventory.setStackMax(3)

        // Stacks should be: [3, 3, 3, 3, 3]
        inventory.add("Hello", quantity = 15)

        inventory.isRemoveItemsIfQty0 = true

        // Stacks should be: [3, 2]
        assertTrue(inventory.incrementQuantity("Hello", -10))

        assertThat(inventory.size, `is`(2))
        assertThat(inventory.getItemQuantity("Hello"), `is` (5))

        // Item should be removed entirely
        assertTrue(inventory.incrementQuantity("Hello", -5))

        assertThat(inventory.size, `is`(0))
        assertThat(inventory.getItemQuantity("Hello"), `is` (0))
    }

    @Test
    fun `Cannot change stackMax to higher than greatest item quantity`() {
        inventory.setStackMax(5)

        inventory.add("Hello", quantity = 5)

        assertThat(inventory.size, `is`(1))

        assertFalse(inventory.setStackMax(4))

        assertThat(inventory.getStackMax(), `is`(5))
    }

    @Test
    fun `Cannot increment quantity past inventory size with specified stackMax`() {
        inventory = Inventory(5)
        inventory.setStackMax(3)

        assertFalse(inventory.add("Hello", quantity = 16))
        assertTrue(inventory.add("Hello", quantity = 15))

        assertTrue(inventory.incrementQuantity("Hello", -5))

        assertFalse(inventory.incrementQuantity("Hello", 6))

        assertThat(inventory.getItemQuantity("Hello"), `is`(10))

        assertFalse(inventory.incrementQuantity("Hello", -11))

        assertTrue(inventory.incrementQuantity("Hello", -10))
    }
}