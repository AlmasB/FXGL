/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import com.almasb.ents.Entity;
import com.almasb.ents.component.ObjectComponent;
import com.almasb.ents.component.Required;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.BoundingBox;
import javafx.scene.Node;

import java.util.Optional;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
//@Required(RotationComponent.class)
public class MainViewComponent extends ObjectComponent<EntityView> {

    private RenderLayer layer;
    private Node graphics;

    public MainViewComponent(Node view) {
        this(view, RenderLayer.TOP);
    }

    public MainViewComponent(Node view, RenderLayer layer) {
        super();
        this.graphics = view;
        this.layer = layer;
    }

    @Override
    public void onAdded(Entity entity) {
        EntityView view = new EntityView(entity, graphics);

        view.setRenderLayer(layer);
        view.translateXProperty().bind(entity.getComponentUnsafe(PositionComponent.class).xProperty());
        view.translateYProperty().bind(entity.getComponentUnsafe(PositionComponent.class).yProperty());

        Optional<RotationComponent> rotationComponent = entity.getComponent(RotationComponent.class);
        if (rotationComponent.isPresent()) {
            view.rotateProperty().bind(rotationComponent.get().valueProperty());
        } else {
            RotationComponent rotation = new RotationComponent(0);
            entity.addComponent(rotation);
            view.rotateProperty().bind(rotation.valueProperty());
        }

        setValue(view);

        if (!entity.getComponent(BoundingBoxComponent.class).isPresent()) {
            BoundingBoxComponent bbox = new BoundingBoxComponent(new HitBox("__VIEW__", new BoundingBox(
                0, 0, view.getLayoutBounds().getWidth(), view.getLayoutBounds().getHeight()
            )));

            System.out.println(bbox.getWidth() + " " + bbox.getHeight());

            entity.addComponent(bbox);
        }
    }
}
