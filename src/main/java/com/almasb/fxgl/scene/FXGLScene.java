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
package com.almasb.fxgl.scene;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.layout.Pane;

/**
 * Base class for all FXGL scenes.
 */
public abstract class FXGLScene {
    private Pane root;
    private Group eventHandlers = new Group();

    public FXGLScene() {
        root = new Pane();
        root.setBackground(null);
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
     * @return width
     */
    public final double getWidth() {
        return root.getPrefWidth();
    }

    /**
     *
     * @return height
     */
    public final double getHeight() {
        return root.getPrefHeight();
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

    private BooleanProperty active = new SimpleBooleanProperty(false);

    protected BooleanProperty activeProperty() {
        return active;
    }
}
