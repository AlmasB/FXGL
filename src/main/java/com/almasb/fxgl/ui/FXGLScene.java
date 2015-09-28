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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.almasb.fxgl.asset.AssetManager;
import com.almasb.fxgl.settings.SceneSettings;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

public abstract class FXGLScene {
    private Pane root;
    private Group eventHandlers = new Group();
    private SceneSettings settings;

    public FXGLScene(SceneSettings settings) {
        this.settings = settings;

        root = new Pane();
        root.setBackground(null);
        root.setPrefSize(settings.getScaledWidth(), settings.getScaledHeight());
        root.getTransforms().setAll(new Scale(settings.getScaleRatio(), settings.getScaleRatio()));
        root.getStylesheets().add(settings.getCSS());
    }

    public Scene getScene() {
        return getRoot().getScene();
    }

    public Pane getRoot() {
        return root;
    }

    public double getWidth() {
        return settings.getTargetWidth();
    }

    public final double getHeight() {
        return settings.getTargetHeight();
    }

    public final double getScaleRatio() {
        return settings.getScaleRatio();
    }

    public <T extends Event> void addEventHandler(EventType<T> eventType,
            EventHandler<? super T> eventHandler) {
        eventHandlers.addEventHandler(eventType, eventHandler);
    }

    public void fireEvent(Event event) {
        eventHandlers.fireEvent(event);
    }

    /**
     * Sets global game cursor using given name to find
     * the image cursor within assets/ui/cursors/.
     * Hotspot is location of the pointer end on the image.
     *
     * @param imageName
     * @param hotspot
     */
    public void setCursor(String imageName, Point2D hotspot) {
        root.setCursor(new ImageCursor(AssetManager.INSTANCE.loadCursorImage(imageName),
                hotspot.getX(), hotspot.getY()));
    }
}
