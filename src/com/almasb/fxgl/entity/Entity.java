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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.text.Text;

/**
 * A generic FXGL game object
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.1
 *
 */
public class Entity extends Parent {

    public static final String PR_TYPE = "PR_TYPE";
    public static final String PR_USE_PHYSICS = "PR_USE_PHYSICS";

    private List<Control> controls = new ArrayList<>();

    /**
     * Constructs an entity with given type
     *
     * @param type
     */
    public Entity(EntityType type) {
        setProperty(PR_TYPE, type.getUniqueType());
        setGraphics(new Text("null"));
        setUsePhysics(false);
    }

    /**
     * Allow this entity to participate in collision detection
     *
     * @param b
     */
    public Entity setUsePhysics(boolean b) {
        setProperty(PR_USE_PHYSICS, b);
        return this;
    }

    /**
     *
     * @return entity position - translation from the parent's origin
     */
    public Point2D getPosition() {
        return new Point2D(getTranslateX(), getTranslateY());
    }

    /**
     * Equivalent to
     *
     * <pre>
     * setTranslateX()
     * setTranslateY()
     * </pre>
     *
     * @param x
     * @param y
     * @return this entity
     */
    public Entity setPosition(double x, double y) {
        setTranslateX(x);
        setTranslateY(y);
        return this;
    }

    /**
     * Translate (move) entity by vector (x, y)
     *
     * @param x
     * @param y
     */
    public void translate(double x, double y) {
        setTranslateX(getTranslateX() + x);
        setTranslateY(getTranslateY() + y);
    }

    /**
     * Translate (move) entity by vector
     *
     * @param vector
     */
    public void translate(Point2D vector) {
        translate(vector.getX(), vector.getY());
    }

    /**
     * @return entity type
     */
    public String getType() {
        return getProperty(PR_TYPE);
    }

    /**
     * Returns true if type of entity equals passed argument
     *
     * @param type
     * @return
     */
    public boolean isType(EntityType type) {
        return getType().equals(type.getUniqueType());
    }

    /**
     * Set graphics for this entity
     *
     * @param graphics
     * @return this entity
     */
    public Entity setGraphics(Node graphics) {
        getChildren().clear();
        getChildren().add(graphics);
        return this;
    }

    /**
     * Do NOT call prior to adding the entity to root
     *
     * @return width of the bounding box of this entity
     */
    public double getWidth() {
        return getBoundsInParent().getWidth();
    }

    /**
     * Do NOT call prior to adding the entity to root
     *
     * @return height of the bounding box of this entity
     */
    public double getHeight() {
        return getBoundsInParent().getHeight();
    }

    /**
     * Add behavior to entity
     *
     * @param control
     */
    public Entity addControl(Control control) {
        controls.add(control);
        if (control instanceof AbstractControl) {
            ((AbstractControl) control).setEntity(this);
        }
        return this;
    }

    /**
     * Remove behavior from entity
     *
     * @param control
     */
    public void removeControl(Control control) {
        controls.remove(control);
    }

    /**
     * Remove all behavior controls from entity
     */
    public void removeControls() {
        controls.clear();
    }

    /**
     * Returns the first control that is an instance of the given class,
     * or null if no such control exists.
     *
     * @param controlType The superclass of the control to look for
     * @return The first instance in the list of the controlType class, or null
     */
    @SuppressWarnings("unchecked")
    public <T extends Control> T getControl(Class<T> controlType) {
        for (Control c : controls) {
            if (controlType.isAssignableFrom(c.getClass())) {
                return (T) c;
            }
        }
        return null;
    }

    /**
     * Do NOT call manually. It is called automatically
     * by FXGL GameApplication
     *
     * @param now
     */
    public final void onUpdate(long now) {
        controls.forEach(control -> control.onUpdate(this, now));
    }

    /**
     * Do NOT call manually. It is called automatically
     * by FXGL GameApplication when entity has been removed
     *
     */
    public final void onClean() {
        getProperties().clear();
        eventHandlers.clear();
        getChildren().clear();
    }

    /**
     * Set a custom property
     *
     * <pre>
     * Example:
     *
     * player.setProperty("hp", 200);
     * player.setProperty("alive", true);
     * </pre>
     *
     * @param name
     * @param value
     */
    public Entity setProperty(String name, Object value) {
        getProperties().put(name, value);
        return this;
    }

    public Entity setProperty(PropertyKey key, Object value) {
        getProperties().put(key.getUniqueKey(), value);
        return this;
    }

    /**
     * Get value of a custom property that was previously set
     * by {@link #setProperty(String, Object)}
     *
     * <pre>
     * Example:
     *
     * if (player.<Boolean>getProperty("alive")) {
     *      // property "alive" is true
     * }
     *
     * int hp = player.getProperty("hp");
     * </pre>
     *
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name) {
        return (T)getProperties().get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(PropertyKey key) {
        return (T)getProperties().get(key.getUniqueKey());
    }

    private Map<String, Consumer<FXGLEvent> > eventHandlers = new HashMap<>();

    public void addFXGLEventHandler(FXGLEventType type, Consumer<FXGLEvent> eventHandler) {
        eventHandlers.put(type.getUniqueType(), eventHandler);
    }

    public void fireFXGLEvent(FXGLEvent event) {
        if (event.getSource() == null)
            event.setSource(this);

        event.setTarget(this);
        eventHandlers.getOrDefault(event.getType().getUniqueType(), e -> {}).accept(event);
    }

    /**
     * Returns a new entity without any type
     *
     * Use this method for background entity,
     * range selection entity, etc when you are not
     * going to use its type
     *
     * @return
     */
    public static Entity noType() {
        return new Entity(new EntityType() {
            @Override
            public String getUniqueType() {
                return "__anonymous__";
            }
        });
    }
}
