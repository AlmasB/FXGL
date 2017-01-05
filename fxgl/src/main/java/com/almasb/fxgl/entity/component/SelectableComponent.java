/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.ents.CopyableComponent;
import com.almasb.ents.Entity;
import com.almasb.ents.component.BooleanComponent;
import com.almasb.ents.component.Required;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.gameplay.GameWorld;

/**
 * Marks an entity as selectable.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(MainViewComponent.class)
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
        MainViewComponent view = getEntity().getComponentUnsafe(MainViewComponent.class);

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
        MainViewComponent view = getEntity().getComponentUnsafe(MainViewComponent.class);

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
