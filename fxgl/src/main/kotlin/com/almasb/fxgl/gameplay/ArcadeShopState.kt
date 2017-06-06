/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.SubState
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.control.ListCell
import javafx.scene.layout.HBox
import javafx.util.Callback

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ArcadeShopState : SubState() {

    private val items = FXCollections.observableArrayList<ArcadeShopItem>()

    init {
        val listView = FXGL.getUIFactory().newListView<ArcadeShopItem>(items)
        listView.cellFactory = Callback {

            val cell = object : ListCell<ArcadeShopItem>() {
                override fun updateItem(item: ArcadeShopItem?, empty: Boolean) {
                    super.updateItem(item, empty)

                    if (empty || item == null) {
                        text = null
                        graphic = null
                    } else {

                        val hbox = HBox(10.0, item.view, Button("++"))
                        graphic = hbox
                    }
                }
            }

            return@Callback cell
        }

        children.add(listView)
        view.translateX = 150.0
        view.translateY = 150.0
    }

    fun addItem(item: ArcadeShopItem) {
        items.add(item)
    }
}