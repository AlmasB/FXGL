/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.trade

import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TradeView : Pane() {
    private val box: VBox
    val listView: TradeListView

    val MENU_TEXT = Color.rgb(25, 250, 25)
    val MENU_BG = Color.rgb(0, 0, 0, 0.6)
    val MENU_BORDER = Color.rgb(25, 250, 25)
    val MENU_ITEM_SELECTION = Color.rgb(10, 140, 10)
    val LOADING_SYMBOL = Color.rgb(25, 250, 25)

    init {

        val width = 400
        val height = 450

        setPrefSize(width.toDouble(), height.toDouble())

        val bg = Rectangle(width.toDouble(), height.toDouble())
        bg.fill = MENU_BG

        val lineTop = Rectangle(width.toDouble(), 4.0)
        lineTop.fill = MENU_BORDER
        lineTop.stroke = Color.BLACK
        lineTop.strokeWidth = 1.3

        val lineBot = Rectangle(width.toDouble(), 4.0)
        lineBot.translateY = (height - 2).toDouble()
        lineBot.fill = MENU_BORDER
        lineBot.stroke = Color.BLACK
        lineBot.strokeWidth = 1.3

        box = VBox(5.0)
        box.translateX = 25.0
        box.translateY = 25.0

        val btn = Button("X")
        //btn.setOnAction { e -> FXGL.getGameController().popSubScene() }

//        val items = FXCollections.observableArrayList(
//                ".38 Round (66)",
//                "10mm Pistol",
//                "Bayoneted Missile Launcher",
//                "Longsword",
//                "Wooden Bow"
//        )

        val items = FXCollections.observableArrayList<TradeItem<*>>(
                TradeItem("", "10mm Pistol", "Item Description", 30, 65, 3),
                TradeItem("", "Bayoneted Missile Launcher", "Item Description", 30, 150, 2),
                TradeItem("", "Plasma Longsword", "Item Description", 30, 335, 1),
                TradeItem("", "Items with a long name that does not fit", "Item Description", 30, 18, 1),
                TradeItem("", "Steel Axe of Thirst", "Item Description", 30, 133, 1),
                TradeItem("", "10mm Pistol", "Item Description", 30, 65, 3),
                TradeItem("", "Bayoneted Missile Launcher", "Item Description", 30, 150, 2),
                TradeItem("", "Plasma Longsword", "Item Description", 30, 335, 1),
                TradeItem("", "Wooden Bow", "Item Description", 30, 28, 1),
                TradeItem("", "Steel Axe of Thirst", "Item Description", 30, 133, 1),
                TradeItem("", "10mm Pistol", "Item Description", 30, 65, 3),
                TradeItem("", "Bayoneted Missile Launcher", "Item Description", 30, 150, 2),
                TradeItem("", "Plasma Longsword", "Item Description", 30, 335, 1),
                TradeItem("", "Wooden Bow", "Item Description", 30, 28, 1),
                TradeItem("", "Steel Axe of Thirst", "Item Description", 30, 133, 1)
        )

        listView = TradeListView(items)
        listView.translateX = 10.0
        listView.translateY = 25.0
        listView.setPrefSize((width - 20).toDouble(), (height - 50).toDouble())

        listView.selectionModel.selectedItemProperty().addListener { _, _, newValue -> println("$newValue is selected") }

        children.addAll(bg, lineTop, lineBot, box, listView)
    }

    fun sellSelected() {
        val selected = listView.selectionModel.selectedItem
        if (selected != null) {
            listView.items.remove(selected)
            println("Sold $selected")
        }
    }
}