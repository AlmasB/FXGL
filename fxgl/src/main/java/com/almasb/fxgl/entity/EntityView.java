/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.entity;

import com.almasb.fxgl.core.Disposable;
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
public class EntityView extends Parent implements Disposable {

    protected static final Logger log = Logger.get("FXGL.EntityView");

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

    @Override
    public void dispose() {
        // we only call dispose to let children to do manual cleanup
        // but we do not remove them from the parent
        // which would have been done by now by JavaFX
        getChildren().stream()
                .filter(n -> n instanceof Disposable)
                .map(n -> (Disposable)n)
                .forEach(Disposable::dispose);
    }
}
