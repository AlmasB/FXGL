/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import com.almasb.fxgl.util.FXGLLogger;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Circle;

import java.util.logging.Logger;

/**
 * Represents the visual aspect of entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityView extends Parent {

    protected static final Logger log = FXGLLogger.getLogger("FXGL.EntityView");

    private Entity entity;

    /**
     * Scene view ctor
     *
     * @param entity   the entity creating the view
     * @param graphics the view for that entity
     */
    EntityView(Entity entity, Node graphics) {
        this.entity = entity;
        addNode(graphics);
        initAsSceneView();

        entity.activeProperty().addListener(((obs, old, isActive) -> {
            if (!isActive)
                removeFromScene();
        }));
    }

    /**
     * Constructs new view for given entity
     *
     * @param entity the entity
     */
    public EntityView(Entity entity) {
        this.entity = entity;

        entity.activeProperty().addListener(((obs, old, isActive) -> {
            if (!isActive)
                removeFromScene();
        }));
    }

    /**
     * @return source entity of this view
     */
    public final Entity getEntity() {
        return entity;
    }

    /**
     * Binds X Y and rotation of the view to entity's properties.
     */
    private void initAsSceneView() {
        this.translateXProperty().bind(entity.xProperty());
        this.translateYProperty().bind(entity.yProperty());
        this.rotateProperty().bind(entity.rotationProperty());

        NumberBinding scaleX = Bindings.when(entity.xFlippedProperty()).then(-1).otherwise(1);
        this.scaleXProperty().bind(scaleX);
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
     * Removes this view from scene and clears its children nodes.
     */
    public final void removeFromScene() {
        getChildren().clear();

        try {
            ((Group)getParent()).getChildren().remove(this);
        } catch (Exception e) {
            log.warning("View wasn't removed from scene because parent is not of type Group: "
                    + e.getMessage());
        }
    }

    private RenderLayer renderLayer = RenderLayer.TOP;

    /**
     * Set render layer for this entity. Render layer determines how an entity
     * is rendered relative to other entities. The layer with higher index()
     * will be rendered on top of the layer with lower index(). By default an
     * entity has the very top layer with highest index equal to
     * {@link Integer#MAX_VALUE}.
     * <p>
     * The render layer can only be set before adding entity to the scene. If
     * the entity is already registered in the scene graph, this method will
     * throw IllegalStateException.
     *
     * @param layer the render layer
     * @throws IllegalStateException
     */
    public final void setRenderLayer(RenderLayer layer) {
        if (entity.isActive())
            throw new IllegalStateException(
                    "Can't set render layer to active view.");

        this.renderLayer = layer;
    }

    /**
     * @return render layer for entity
     */
    public final RenderLayer getRenderLayer() {
        return renderLayer;
    }
}
