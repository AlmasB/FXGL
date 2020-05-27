/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.trade

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ShopTest {

    @Test
    fun `Basic shop functionality`() {
        val items = arrayListOf("hello", "world").map { TradeItem(it, "", "", 5, 5, 2) }

        val shop = Shop(10, items)

        assertThat(shop.moneyProperty().value, `is`(10))
        assertThat(shop.money, `is`(10))
        assertThat(shop.items, `is`(items))
    }

    @Test
    fun `Buy and sell`() {
        val items1 = arrayListOf("hello", "world").map { TradeItem(it, "", "", 5, 3, 2) }
        val items2 = listOf<TradeItem<String>>()

        val shop1 = Shop(items1)
        val shop2 = Shop(items2)

        var wasBought = shop2.buyFrom(shop1, items1[0], 1)

        // no money
        assertFalse(wasBought)

        shop2.money = 100

        wasBought = shop2.buyFrom(shop1, items1[0], 1)

        assertTrue(wasBought)
        assertThat(shop1.money, `is`(3))
        assertThat(shop2.money, `is`(97))
        assertThat(shop2.items.size, `is`(1))
        assertThat(shop2.items[0].item, `is`("hello"))
        assertThat(shop2.items[0].description, `is`(""))
        assertThat(shop2.items[0].name, `is`(""))
        assertThat(shop2.items[0].sellPrice, `is`(5))
        assertThat(shop2.items[0].buyPrice, `is`(3))
        assertThat(shop2.items[0].quantity, `is`(1))
    }
}