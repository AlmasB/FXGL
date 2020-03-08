/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface PropertyViewChangeListener<T, V extends Node> {

    default V makeViewInternal(ObjectProperty<?> v) {
        return makeView((ObjectProperty<T>) v);
    }

    V makeView(ObjectProperty<T> value);
    
    void onPropertyChanged(ObjectProperty<T> value, V view);
    
    void onViewChanged(ObjectProperty<T> value, V view);
}
