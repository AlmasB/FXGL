/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components;

import com.almasb.fxgl.entity.component.CopyableComponent;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.entity.view.EntityView;

/**
 * Marks an entity as selectable.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(ViewComponent.class)
public class SelectableComponent extends BooleanComponent implements CopyableComponent<SelectableComponent> {

    public SelectableComponent(boolean value) {
        super(value);
    }

    @Override
    public void onAdded() {
        ViewComponent view = getEntity().getComponent(ViewComponent.class);

        valueProperty().addListener((o, wasSelectable, isSelectable) -> {
            if (isSelectable) {
                attachSelectListener(view.getView());
            } else {
                removeSelectListener(view.getView());
            }
        });

        if (getValue()) {
            attachSelectListener(view.getView());
        }
    }

    @Override
    public void onRemoved() {
        ViewComponent view = getEntity().getComponent(ViewComponent.class);

        removeSelectListener(view.getView());
    }

    private void attachSelectListener(EntityView view) {
        view.setOnMousePressed(e -> {
            getEntity().getWorld().selectedEntityProperty().set(getEntity());
        });
    }

    private void removeSelectListener(EntityView view) {
        view.setOnMousePressed(null);

        getEntity().getWorld().getSelectedEntity().ifPresent(e -> {
            if (e == getEntity()) {
                getEntity().getWorld().selectedEntityProperty().set(null);
            }
        });
    }

    @Override
    public SelectableComponent copy() {
        return new SelectableComponent(getValue());
    }
}
