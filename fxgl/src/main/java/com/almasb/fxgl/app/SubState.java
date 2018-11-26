/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class SubState extends State {

    private Pane view = new Pane();

    protected ObservableList<Node> getChildren() {
        return view.getChildren();
    }

    public Node getView() {
        return view;
    }
}
