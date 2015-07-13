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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * A generic FXGL game object
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.2
 *
 */
public class Entity extends Parent {

    private EntityType type;

    private List<Control> controls = new ArrayList<>();

    private BooleanProperty active = new SimpleBooleanProperty(true);
    private boolean collidable = false;

    private double expireTime = 0;

    /**
     * Constructs an entity with given type
     *
     * @param type
     */
    public Entity(EntityType type) {
        this.type = type;
        setGraphics(new Text("null"));
    }

    /**
     *
     * @return expireTime of entity, 0 if not set
     */
    public double getExpireTime() {
        return expireTime;
    }

    /**
     * Set expire time for this entity in nanoseconds.
     * The timer starts when the entity is added to
     * game scene. Calling this method after entity
     * was added to scene has no effect.
     *
     * Once the timer has expired, the entity will
     * be removed with removeEntity()
     *
     * @param nanoseconds
     * @return this entity
     */
    public Entity setExpireTime(double nanoseconds) {
        expireTime = nanoseconds;
        return this;
    }

    /**
     * Allow this entity to participate in collision detection
     *
     * @param b
     */
    public Entity setCollidable(boolean b) {
        collidable = b;
        return this;
    }

    /**
     *
     * @return center point of this entity
     */
    public Point2D getCenter() {
        return getPosition().add(getWidth() / 2, getHeight() / 2);
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
     * Equivalent to
     *
     * <pre>
     * setTranslateX()
     * setTranslateY()
     * </pre>
     *
     * @param position
     * @return this entity
     */
    public Entity setPosition(Point2D position) {
        return setPosition(position.getX(), position.getY());
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
     *
     * @return entity type
     */
    public EntityType getEntityType() {
        return type;
    }

    /**
     * @return entity type as String
     */
    public String getTypeAsString() {
        return type.getUniqueType();
    }

    /**
     * Returns true if type of entity equals passed argument.
     *
     * @apiNote equivalent to <code>getTypeAsString().equals(type.getUniqueType())</code>
     * @param type
     * @return
     */
    public boolean isType(EntityType type) {
        return getTypeAsString().equals(type.getUniqueType());
    }

    /**
     * Set graphics for this entity. The collision detection
     * bounding box will use graphics object's size properties.
     *
     * @param graphics
     * @return this entity
     */
    public Entity setGraphics(Node graphics) {
        getChildren().clear();

        if (graphics instanceof Circle) {
            Circle c = (Circle) graphics;
            c.setCenterX(c.getRadius());
            c.setCenterY(c.getRadius());
        }

        getChildren().add(graphics);
        return this;
    }

    /**
     *
     * @return width of the bounding box of this entity
     */
    public double getWidth() {
        return getLayoutBounds().getWidth();
    }

    /**
     *
     * @return height of the bounding box of this entity
     */
    public double getHeight() {
        return getLayoutBounds().getHeight();
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
        active.set(false);
        getProperties().clear();
        eventHandlers.clear();
        controls.clear();
        getChildren().clear();
    }

    public final BooleanProperty activeProperty() {
        return active;
    }

    /**
     * Entity is considered active from moment the object itself
     * is created and until removeEntity() is called
     *
     * @return
     */
    public boolean isActive() {
        return active.get();
    }

    /**
     *
     * @return true if the object participates in collision detection,
     *      false otherwise
     */
    public boolean isCollidable() {
        return collidable;
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

    private Map<String, FXGLEventHandler> eventHandlers = new HashMap<>();

    public void addFXGLEventHandler(FXGLEventType type, FXGLEventHandler eventHandler) {
        eventHandlers.put(type.getUniqueType(), eventHandler);
    }

    /**
     * Fire (trigger) an FXGL event on this entity
     * This entity becomes the target of the FXGL event
     *
     * If the FXGL event doesn't have a source, this
     * entity will also become the source of the event
     *
     * @param event
     */
    public void fireFXGLEvent(FXGLEvent event) {
        if (event.getSource() == null)
            event.setSource(this);

        event.setTarget(this);
        eventHandlers.getOrDefault(event.getType().getUniqueType(), e -> {}).handle(event);
    }

    /**
     * Returns a new entity without any type
     *
     * Use this method for background entity,
     * range selection entity, temporary entity,
     * etc when you are not
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
