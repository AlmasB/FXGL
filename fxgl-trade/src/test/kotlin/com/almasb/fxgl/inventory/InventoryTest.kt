/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.inventory

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

        inventory.remove("100")

        assertFalse(inventory.isFull)
    }

    @Test
    fun `Add new and remove items`() {
        inventory.add("Hello")

        assertThat(inventory.size, `is`(1))
        assertThat(inventory.getData("Hello").get().quantity, `is`(1))

        // we are adding the same thing (as defined by equals()), so size remains the same
        // but quantity should change
        inventory.add("Hello")

        assertThat(inventory.size, `is`(1))
        assertThat(inventory.getData("Hello").get().quantity, `is`(2))

        inventory.add("Hello", quantity = 30)

        assertThat(inventory.size, `is`(1))
        assertThat(inventory.getData("Hello").get().quantity, `is`(32))

        inventory.incrementQuantity("Hello", -31)

        assertThat(inventory.size, `is`(1))
        assertThat(inventory.getData("Hello").get().quantity, `is`(1))

        inventory.remove("Hello")

        assertThat(inventory.size, `is`(0))
    }
}