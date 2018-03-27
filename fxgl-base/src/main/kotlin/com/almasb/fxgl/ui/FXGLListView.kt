/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.ListView

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLListView<T>(items: ObservableList<T>) : ListView<T>(items) {

    constructor() : this(FXCollections.observableArrayList<T>())

    init {
        styleClass.add("fxgl-list-view")

        //cellFactory = Callback { FXGLListCell<T>() }
    }

//    private class FXGLListCell<T> : ListCell<T>() {
//        init {
//            styleClass.add("fxgl-list-cell")
//        }
//    }
}