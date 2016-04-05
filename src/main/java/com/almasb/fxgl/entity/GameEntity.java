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

package com.almasb.fxgl.entity;

import com.almasb.ents.Entity;
import com.almasb.fxgl.entity.component.*;

/**
 * Entity that guarantees to have Type, Position, Rotation, BoundingBox and View
 * components.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GameEntity extends Entity {
    private TypeComponent type;
    private PositionComponent position;
    private RotationComponent rotation;
    private BoundingBoxComponent bbox;
    private MainViewComponent view;

    public GameEntity() {
        type = new TypeComponent();
        position = new PositionComponent();
        rotation = new RotationComponent();
        bbox = new BoundingBoxComponent();
        view = new MainViewComponent();

        addComponent(type);
        addComponent(position);
        addComponent(rotation);
        addComponent(bbox);
        addComponent(view);
    }

    /**
     * @return type
     */
    public TypeComponent getTypeComponent() {
        return type;
    }

    /**
     * @return position
     */
    public PositionComponent getPositionComponent() {
        return position;
    }

    /**
     * @return rotation
     */
    public RotationComponent getRotationComponent() {
        return rotation;
    }

    /**
     * @return bounding box
     */
    public BoundingBoxComponent getBoundingBoxComponent() {
        return bbox;
    }

    /**
     * @return view
     */
    public MainViewComponent getMainViewComponent() {
        return view;
    }

    @Override
    public String toString() {
        return "GameEntity(" + type + "," + position + "," + rotation  + ")";
    }
}
