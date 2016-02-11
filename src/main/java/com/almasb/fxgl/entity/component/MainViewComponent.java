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

import com.almasb.ents.AbstractComponent;
import com.almasb.ents.Entity;
import com.almasb.ents.component.Required;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.physics.HitBox;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
@Required(RotationComponent.class)
public class MainViewComponent extends AbstractComponent {

    private static boolean showBBox = false;
    private static Color showBBoxColor = Color.BLACK;

    /**
     * Turns on displaying of bounding boxes. Useful for debugging.
     * Note: this only shows bounding boxes, not each hit box.
     *
     * @param color the color to highlight bounding boxes
     */
    public static final void turnOnDebugBBox(Color color) {
        showBBox = true;
        showBBoxColor = color;
    }

    private ObjectProperty<RenderLayer> renderLayer;
    private ObjectProperty<EntityView> view;

    /**
     * Creates main view component with no graphics.
     */
    public MainViewComponent() {
        this(new EntityView());
    }

    /**
     * Creates main view with given graphics.
     *
     * @param graphics the graphics
     */
    public MainViewComponent(Node graphics) {
        this(new EntityView(graphics), RenderLayer.TOP);
    }

    /**
     * Creates main view with given graphics and given render layer.
     *
     * @param graphics the graphics
     * @param renderLayer render layer to use for view
     */
    public MainViewComponent(Node graphics, RenderLayer renderLayer) {
        this.view = new SimpleObjectProperty<>(new EntityView(graphics));
        this.renderLayer = new SimpleObjectProperty<>(renderLayer);
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

    /**
     * Set render layer.
     *
     * @param renderLayer render layer
     */
    public void setRenderLayer(RenderLayer renderLayer) {
        this.renderLayer.set(renderLayer);
        getView().setRenderLayer(renderLayer);
    }

    /**
     * @return view
     */
    public EntityView getView() {
        return view.get();
    }

    /**
     * @return view property
     */
    public ObjectProperty<EntityView> viewProperty() {
        return view;
    }

    /**
     * Convenience method to set JavaFX node directly.
     * This is equivalent to <pre>setView(new EntityView(graphics));</pre>
     *
     * @param graphics JavaFX node
     */
    public void setGraphics(Node graphics) {
        setView(new EntityView(graphics));
    }

    /**
     * Set view without generating bounding boxes from view.
     *
     * @param view the view
     */
    public void setView(EntityView view) {
        setView(view, false);
    }

    /**
     * Set view. The generate bbox flag tells the component
     * whether it should generate bbox from the given view.
     *
     * @param view the view
     * @param generateBoundingBox generate bbox flag
     */
    public void setView(EntityView view, boolean generateBoundingBox) {
        this.view.set(view);

        if (generateBoundingBox) {
            Entities.getBBox(getEntity()).addHitBox(new HitBox("__VIEW__", new BoundingBox(
                    0, 0, getView().getLayoutBounds().getWidth(), getView().getLayoutBounds().getHeight()
            )));
        }
    }

    /**
     * Convenience method to set texture as view.
     *
     * @param textureName name of texture
     */
    public void setTexture(String textureName) {
        setTexture(textureName, false);
    }

    /**
     * Convenience method to set texture as view.
     *
     * @param textureName name of texture
     * @param generateBoundingBox generate bbox from view flag
     */
    public void setTexture(String textureName, boolean generateBoundingBox) {
        EntityView view = new EntityView(GameApplication.getService(ServiceType.ASSET_LOADER)
                .loadTexture(textureName));

        setView(view, generateBoundingBox);
    }

    @Override
    public void onAdded(Entity entity) {
        if (!entity.getComponent(BoundingBoxComponent.class).isPresent()) {
            BoundingBoxComponent bbox = new BoundingBoxComponent(new HitBox("__VIEW__", new BoundingBox(
                0, 0, getView().getLayoutBounds().getWidth(), getView().getLayoutBounds().getHeight()
            )));

            entity.addComponent(bbox);
        }

        getView().translateXProperty().bind(getEntity().getComponentUnsafe(PositionComponent.class).xProperty());
        getView().translateYProperty().bind(getEntity().getComponentUnsafe(PositionComponent.class).yProperty());
        getView().rotateProperty().bind(getEntity().getComponentUnsafe(RotationComponent.class).valueProperty());

        if (showBBox) {
            initDebugBBox();
        }

        view.addListener((observable, oldGraphics, newGraphics) -> {
            oldGraphics.translateXProperty().unbind();
            oldGraphics.translateYProperty().unbind();
            oldGraphics.rotateProperty().unbind();

            newGraphics.translateXProperty().bind(getEntity().getComponentUnsafe(PositionComponent.class).xProperty());
            newGraphics.translateYProperty().bind(getEntity().getComponentUnsafe(PositionComponent.class).yProperty());
            newGraphics.rotateProperty().bind(getEntity().getComponentUnsafe(RotationComponent.class).valueProperty());

            if (showBBox) {
                // TODO: we can reuse the same rectangle
                initDebugBBox();
            }
        });
    }

    @Override
    public void onRemoved(Entity entity) {

    }

    private void initDebugBBox() {
        BoundingBoxComponent bbox = Entities.getBBox(getEntity());

        Rectangle debugBBox = new Rectangle();
        debugBBox.setStroke(showBBoxColor);
        debugBBox.setFill(null);
        debugBBox.translateXProperty().bind(bbox.minXLocalProperty());
        debugBBox.translateYProperty().bind(bbox.minYLocalProperty());
        debugBBox.widthProperty().bind(bbox.widthProperty());
        debugBBox.heightProperty().bind(bbox.heightProperty());

        getView().addNode(debugBBox);
    }
}
