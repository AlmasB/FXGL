/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.trade

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TradeItemTest {

    @Test
    fun `Trade item hashcode is based on actual item`() {
        val ref1 = ItemRef()

        val item1 = TradeItem(ref1, "some item", "desc", 1, 1, 2)

        assertThat(item1.hashCode(), `is`(ref1.hashCode()))
    }

    @Test
    fun `Trade item equality is based on referencing the same item`() {
        val ref1 = ItemRef()
        val ref2 = ItemRef()

        val item1 = TradeItem(ref1, "some item", "desc", 1, 1, 2)
        val item2 = TradeItem(ref1, "some item", "desc", 1, 1, 2)
        val item3 = TradeItem(ref2, "some item", "desc", 1, 1, 2)

        assertThat(item1, `is`(item2))
        assertThat(item2, `is`(item1))
        assertThat(item1, `is`(not(item3)))
        assertThat(item2, `is`(not(item3)))

        assertFalse(item1.equals(ref1))
    }

    private class ItemRef
}