/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.trade.view

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
        tabPane.styleClass.addAll("fxgl-trade-tab-pane")

        stylesheets.add(javaClass.getResource("fxgl_trade.css").toExternalForm())

        val tab1 = Tab("Sell")
        tab1.content = playerShopView
        tab1.isClosable = false

        val tab2 = Tab("Buy")
        tab2.content = npcShopView
        tab2.isClosable = false

        tabPane.tabs.addAll(
                tab1,
                tab2
        )

        children += tabPane
    }
}