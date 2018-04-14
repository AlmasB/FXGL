/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.scene;

import com.almasb.fxgl.app.FXGL;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.ImageCursor;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Scale;

import static com.almasb.fxgl.app.DSLKt.texture;

/**
 * Base class for all FXGL scenes.
 */
public abstract class FXGLScene {

    /**
     * Top-level root node.
     */
    private Pane root;

    /**
     * Root node for content.
     */
    private Pane contentRoot;


    private Viewport viewport;

    public FXGLScene() {
        root = new Pane();
        root.setBackground(null);

        contentRoot = new Pane();
        contentRoot.setBackground(null);

        root.getChildren().addAll(contentRoot);

        // should pass thru ctor params?
        viewport = new Viewport(FXGL.getAppWidth(), FXGL.getAppHeight());

        if (FXGL.isDesktop()) {
            setCursor("fxgl_default.png", new Point2D(7, 6));
        }
    }

    /**
     * @return top-level root node of the scene
     */
    public final Pane getRoot() {
        return root;
    }

    /**
     * @return root node of the content
     */
    public final Pane getContentRoot() {
        return contentRoot;
    }

    /**
     * @return viewport
     */
    public Viewport getViewport() {
        return viewport;
    }

    /**
     * @return width
     */
    public final double getWidth() {
        return root.getPrefWidth();
    }

    /**
     * @return height
     */
    public final double getHeight() {
        return root.getPrefHeight();
    }

    /**
     * Applies given effect to the scene.
     *
     * @param effect the effect to apply
     */
    public final void setEffect(Effect effect) {
        contentRoot.setEffect(effect);
    }

    /**
     * @return currently applied effect or null if no effect is applied
     */
    public final Effect getEffect() {
        return contentRoot.getEffect();
    }

    /**
     * Removes any effects applied to the scene.
     */
    public final void clearEffect() {
        setEffect(null);
    }

    /**
     * Sets global game cursor using given name to find
     * the image cursor within assets/ui/cursors/.
     * Hotspot is location of the pointer end on the image.
     *
     * @param imageName name of image file
     * @param hotspot hotspot location
     */
    public final void setCursor(String imageName, Point2D hotspot) {
        root.setCursor(new ImageCursor(FXGL.getAssetLoader().loadCursorImage(imageName),
                hotspot.getX(), hotspot.getY()));
    }

    private BooleanProperty active = new SimpleBooleanProperty(false);

    /**
     * If a scene is active it is being shown by the display.
     *
     * @return active property
     */
    public BooleanProperty activeProperty() {
        return active;
    }

    public void appendCSS(CSS css) {
        getRoot().getStylesheets().add(css.getExternalForm());
    }

    public void clearCSS() {
        getRoot().getStylesheets().clear();
    }

    public void bindSize(DoubleProperty scaledWidth, DoubleProperty scaledHeight, DoubleProperty scaleRatioX, DoubleProperty scaleRatioY) {
        root.prefWidthProperty().bind(scaledWidth);
        root.prefHeightProperty().bind(scaledHeight);

        Scale scale = new Scale();
        scale.xProperty().bind(scaleRatioX);
        scale.yProperty().bind(scaleRatioY);
        root.getTransforms().setAll(scale);
    }

    public void setBackgroundColor(Paint color) {
        root.setBackground(new Background(new BackgroundFill(color, null, null)));
    }

    /**
     * Convenience method to load the texture and repeat as often as needed to cover the background.
     */
    public void setBackgroundRepeat(String textureName) {
        setBackgroundRepeat(texture(textureName).getImage());
    }

    /**
     * The image is repeated as often as needed to cover the background.
     */
    public void setBackgroundRepeat(Image image) {
        root.setBackground(new Background(new BackgroundImage(image,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null)));
    }
}
