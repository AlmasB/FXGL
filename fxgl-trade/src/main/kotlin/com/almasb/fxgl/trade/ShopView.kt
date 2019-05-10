/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.trade

import javafx.collections.ObservableList
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ShopView<T>(val shop: Shop<T>, width: Int, height: Int) : Pane() {

    private val box: VBox
    val listView: TradeListView

    val MENU_TEXT = Color.rgb(25, 250, 25)
    val MENU_BG = Color.rgb(0, 0, 0, 0.6)
    val MENU_BORDER = Color.rgb(25, 250, 25)
    val MENU_ITEM_SELECTION = Color.rgb(10, 140, 10)
    val LOADING_SYMBOL = Color.rgb(25, 250, 25)

    init {
        setPrefSize(width.toDouble(), height.toDouble())

        val bg = Rectangle(width.toDouble(), height.toDouble())
        bg.fill = MENU_BG

        val lineTop = Rectangle(width.toDouble(), 4.0)
        lineTop.fill = MENU_BORDER
        lineTop.stroke = Color.BLACK
        lineTop.strokeWidth = 1.3

        val lineBot = Rectangle(width.toDouble(), 4.0)
        lineBot.translateY = (height - 40 - 2).toDouble()
        lineBot.fill = MENU_BORDER
        lineBot.stroke = Color.BLACK
        lineBot.strokeWidth = 1.3

        box = VBox(5.0)
        box.translateX = 25.0
        box.translateY = 25.0

        val textMoney = Text()
        textMoney.fill = Color.YELLOW
        textMoney.font = Font.font(24.0)
        textMoney.textProperty().bind(shop.moneyProperty().asString("Money: %d"))
        textMoney.translateX = 25.0
        textMoney.translateY = height.toDouble() - 10

        listView = TradeListView(shop.items as ObservableList<TradeItem<*>>)
        listView.translateX = 10.0
        listView.translateY = 25.0
        listView.setPrefSize((width - 20).toDouble(), (height - 50 - 40).toDouble())

        listView.selectionModel.selectedItemProperty().addListener { _, _, newValue -> println("$newValue is selected") }

        children.addAll(bg, lineTop, lineBot, box, listView, textMoney)
    }
}