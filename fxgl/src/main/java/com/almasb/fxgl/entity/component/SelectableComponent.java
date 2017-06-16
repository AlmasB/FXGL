/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.fxgl.ecs.CopyableComponent;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.component.BooleanComponent;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.ecs.GameWorld;

/**
 * Marks an entity as selectable.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(ViewComponent.class)
public class SelectableComponent extends BooleanComponent implements CopyableComponent<SelectableComponent> {

//    private ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper(false);
//
//    public ReadOnlyBooleanProperty selectedProperty() {
//        return selected.getReadOnlyProperty();
//    }
//
//    public boolean isSelected() {
//        return selected.get();
//    }

    public SelectableComponent(boolean value) {
        super(value);
    }

    @Override
    public void onAdded(Entity entity) {
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
    public void onRemoved(Entity entity) {
        ViewComponent view = getEntity().getComponent(ViewComponent.class);

        removeSelectListener(view.getView());
    }

    private void attachSelectListener(EntityView view) {
        view.setOnMousePressed(e -> {
            //selected.set(true);
            ((GameWorld) getEntity().getWorld()).selectedEntityProperty().set(getEntity());
        });
    }

    private void removeSelectListener(EntityView view) {
        view.setOnMousePressed(null);

        ((GameWorld) getEntity().getWorld()).getSelectedEntity().ifPresent(e -> {
            if (e == getEntity()) {
                ((GameWorld) getEntity().getWorld()).selectedEntityProperty().set(null);
            }
        });
    }

    @Override
    public SelectableComponent copy() {
        return new SelectableComponent(getValue());
    }
}
