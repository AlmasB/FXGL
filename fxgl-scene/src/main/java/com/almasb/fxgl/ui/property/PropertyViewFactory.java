/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui.property;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface PropertyViewFactory<T, V extends Node> {

    default V makeViewInternal(ObjectProperty<?> v) {
        return makeView((ObjectProperty<T>) v);
    }

    /**
     * @return the visual representation of the property [value]
     */
    V makeView(ObjectProperty<T> value);

    /**
     * Called when the property has changed, so that the view can be updated.
     */
    void onPropertyChanged(ObjectProperty<T> value, V view);

    /**
     * Called when the view has changed, so that the property can be updated.
     */
    void onViewChanged(ObjectProperty<T> value, V view);
}
