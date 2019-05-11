/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.trade

import javafx.scene.Parent
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TradeView<T>(val playerShopView: ShopView<T>, val npcShopView: ShopView<T>) : Parent() {

    val tabPane = TabPane()

    init {
        val tab1 = Tab("Buy")
        tab1.content = playerShopView
        tab1.isClosable = false

        val tab2 = Tab("Sell")
        tab2.content = npcShopView
        tab2.isClosable = false

        tabPane.getTabs().addAll(
                tab1,
                tab2
        )

        children += tabPane
    }
}