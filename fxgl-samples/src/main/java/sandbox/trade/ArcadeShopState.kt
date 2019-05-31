/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.trade

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.scene.SubScene
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
class ArcadeShopState : SubScene() {

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

        contentRoot.children.add(listView)

        //children.add(listView)
        //view.translateX = 150.0
        //view.translateY = 150.0
    }

    fun addItem(item: ArcadeShopItem) {
        items.add(item)
    }
}