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

import com.almasb.ents.Entity;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.util.FXGLLogger;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

import java.util.logging.Logger;

/**
 * Represents the visual aspect of entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityView extends Parent {

    protected static final Logger log = FXGLLogger.getLogger("FXGL.EntityView");

    private static boolean showBBox = false;
    private static Color showBBoxColor = Color.BLACK;

    public static final void turnOnDebugBBox(Color color) {
        showBBox = true;
        showBBoxColor = color;
    }

    private Entity entity;

    /**
     * Scene view ctor
     *
     * @param entity   the entity creating the view
     * @param graphics the view for that entity
     */
    public EntityView(Entity entity, Node graphics) {
        this.entity = entity;
        addNode(graphics);

//        if (showBBox) {
//            BoundingBoxComponent bbox = Entities.getBBox(entity);
//
//            Rectangle debugBBox = new Rectangle();
//            debugBBox.setStroke(showBBoxColor);
//            debugBBox.setFill(null);
//            debugBBox.widthProperty().bind(bbox.widthProperty());
//            debugBBox.heightProperty().bind(bbox.heightProperty());
//
//            addNode(debugBBox);
//        }

        entity.activeProperty().addListener(((obs, old, isActive) -> {
            if (!isActive)
                removeFromScene();
        }));
    }

    //        entity.xFlippedProperty().addListener(((obs, oldValue, isFlipped) -> {
//            if (isFlipped) {
//                getTransforms().setAll(new Scale(-1, 1, entity.getXFlipLine(), 0));
//            } else {
//                getTransforms().clear();
//            }
//        }));

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

    public EntityView() {

    }

    /**
     * @return source entity of this view
     */
    public final Entity getEntity() {
        return entity;
    }

    /**
     * Returns nodes attached to this view.
     * Modifying the list directly is discouraged as certain events
     * may not be properly registered.
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

    private boolean removedFromScene = false;

    /**
     * Removes this view from scene and clears its children nodes.
     */
    public final void removeFromScene() {
        if (removedFromScene)
            return;

        getChildren().clear();

        try {
            // we were created by user and he set scene view manually
            if (getParent() instanceof EntityView) {
                ((EntityView) getParent()).removeFromScene();
            }
            // we were created automatically by Entity
            else if (getParent() instanceof Group) {
                ((Group)getParent()).getChildren().remove(this);
            } else {
                throw new IllegalStateException("View parent is of unknown type: " + getParent().getClass());
            }

            removedFromScene = true;
        } catch (Exception e) {
            log.warning("View wasn't removed from scene: " + e.getMessage());
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
            throw new IllegalStateException("Can't set render layer to active view.");

        this.renderLayer = layer;
    }

    /**
     * @return render layer for entity
     */
    public final RenderLayer getRenderLayer() {
        return renderLayer;
    }
}
