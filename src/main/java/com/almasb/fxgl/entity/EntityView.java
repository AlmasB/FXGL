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

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Circle;

/**
 * Represents the visual aspect of entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public class EntityView extends Parent {

    private Entity entity;

    /**
     * Scene view ctor
     *
     * @param entity    the entity creating the view
     * @param graphics  the view for that entity
     */
    /*package-private*/ EntityView(Entity entity, Node graphics) {
        this.entity = entity;
        addNode(graphics);
        initAsSceneView();
    }

    /**
     * Constructs new view for given entity
     *
     * @param entity    the entity
     */
    public EntityView(Entity entity) {
        this.entity = entity;
    }

    /**
     *
     * @return  source entity of this view
     */
    public Entity getEntity() {
        return entity;
    }

    private final void initAsSceneView() {
        this.translateXProperty().bind(entity.xProperty());
        this.translateYProperty().bind(entity.yProperty());
        this.rotateProperty().bind(entity.rotationProperty());
    }

    public final void addNode(Node node) {
        if (node instanceof Circle) {
            Circle c = (Circle) node;
            c.setCenterX(c.getRadius());
            c.setCenterY(c.getRadius());
        }

        getChildren().add(node);
    }

    private RenderLayer renderLayer = RenderLayer.TOP;

    /**
     * Set render layer for this entity. Render layer determines how an entity
     * is rendered relative to other entities. The layer with higher index()
     * will be rendered on top of the layer with lower index(). By default an
     * entity has the very top layer with highest index equal to
     * {@link Integer#MAX_VALUE}.
     *
     * The render layer can only be set before adding entity to the scene. If
     * the entity is already registered in the scene graph, this method will
     * throw IllegalStateException.
     *
     * @param layer
     * @throws IllegalStateException
     */
    public final void setRenderLayer(RenderLayer layer) {
        if (entity.isActive())
            throw new IllegalStateException(
                    "Can't set render layer to active view.");

        this.renderLayer = layer;
    }

    /**
     *
     * @return render layer for entity
     */
    public final RenderLayer getRenderLayer() {
        return renderLayer;
    }
}
