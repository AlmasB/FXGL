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
package com.almasb.fxgl.entity;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Circle;

/**
 * Represents the visual aspect of an entity.
 * Note that the view need not be associated with an entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityView extends Parent {

    protected static final Logger log = FXGL.getLogger("FXGL.EntityView");

    /**
     * Constructs a view with no content.
     */
    public EntityView() {}

    /**
     * Constructs a view with given graphics content.
     *
     * @param graphics the view content
     */
    public EntityView(Node graphics) {
        addNode(graphics);
    }

    /**
     * Constructs a view with given render layer.
     *
     * @param layer render layer
     */
    public EntityView(RenderLayer layer) {
        setRenderLayer(layer);
    }

    /**
     * Constructs a view with given graphics and render layer.
     *
     * @param graphics content
     * @param layer render layer
     */
    public EntityView(Node graphics, RenderLayer layer) {
        addNode(graphics);
        setRenderLayer(layer);
    }

    /**
     * Returns nodes attached to this view.
     * Do NOT modify the list.
     *
     * @return list of children
     */
    public final ObservableList<Node> getNodes() {
        return getChildren();
    }

    /**
     * Add a child node to this view.
     *
     * @param node graphics
     */
    public final void addNode(Node node) {
        if (node instanceof Circle) {
            Circle c = (Circle) node;
            c.setCenterX(c.getRadius());
            c.setCenterY(c.getRadius());
        }

        getChildren().add(node);
    }

    /**
     * Removes a child node attached to this view.
     *
     * @param node graphics
     */
    public final void removeNode(Node node) {
        getChildren().remove(node);
    }

    /**
     * Removes all attached nodes.
     */
    public final void clearChildren() {
        getChildren().clear();
    }

    private ObjectProperty<RenderLayer> renderLayer = new SimpleObjectProperty<>(RenderLayer.TOP);

    /**
     * Set render layer for this entity.
     * Render layer determines how an entity
     * is rendered relative to other entities.
     * The layer with higher index()
     * will be rendered on top of the layer with lower index().
     * By default an
     * entity has the very top layer with highest index equal to
     * {@link Integer#MAX_VALUE}.
     *
     * @param renderLayer the render layer
     */
    public void setRenderLayer(RenderLayer renderLayer) {
        this.renderLayer.set(renderLayer);
    }

    /**
     * @return render layer
     */
    public RenderLayer getRenderLayer() {
        return renderLayer.get();
    }

    /**
     * @return render layer property
     */
    public ObjectProperty<RenderLayer> renderLayerProperty() {
        return renderLayer;
    }
}
