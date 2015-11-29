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
package com.almasb.fxgl.ui;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.ServiceType;
import com.almasb.fxgl.asset.AssetLoader;
import com.almasb.fxgl.settings.SceneSettings;

import javafx.event.*;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

/**
 * Base class for all FXGL scenes.
 */
public abstract class FXGLScene {
    private Pane root;
    private Group eventHandlers = new Group();
    private SceneSettings settings;

    public FXGLScene(SceneSettings settings) {
        this.settings = settings;
        root = new Pane();
        root.setBackground(null);

        root.prefWidthProperty().bind(settings.scaledWidthProperty());
        root.prefHeightProperty().bind(settings.scaledHeightProperty());

        Scale scale = new Scale();
        scale.xProperty().bind(settings.scaleRatioProperty());
        scale.yProperty().bind(settings.scaleRatioProperty());
        root.getTransforms().setAll(scale);

        String css = settings.getCSS();
        if (!css.isEmpty())
            root.getStylesheets().add(css);
    }

    /**
     *
     * @return root node of the scene
     */
    public final Pane getRoot() {
        return root;
    }

    /**
     *
     * @return target width
     */
    public final double getWidth() {
        return settings.getTargetWidth();
    }

    /**
     *
     * @return target height
     */
    public final double getHeight() {
        return settings.getTargetHeight();
    }

    public final double getScaleRatio() {
        return settings.getScaleRatio();
    }

    public final <T extends Event> void addEventHandler(EventType<T> eventType,
                                                  EventHandler<? super T> eventHandler) {
        eventHandlers.addEventHandler(eventType, eventHandler);
    }

    public final <T extends Event> void removeEventHandler(EventType<T> eventType,
                                                     EventHandler<? super T> eventHandler) {
        eventHandlers.removeEventHandler(eventType, eventHandler);
    }

    /**
     * Fire JavaFX event on this FXGL scene.
     *
     * @param event the JavaFX event
     */
    public final void fireEvent(Event event) {
        eventHandlers.fireEvent(event);
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
        root.setCursor(new ImageCursor(GameApplication.getService(ServiceType.ASSET_LOADER).loadCursorImage(imageName),
                hotspot.getX(), hotspot.getY()));
    }
}
