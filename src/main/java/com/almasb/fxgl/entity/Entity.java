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
package com.almasb.fxgl.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import com.almasb.fxgl.GameWorld;
import com.almasb.fxgl.util.FXGLLogger;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * A generic FXGL game object. Any game object "should" be of type Entity.
 * Although not recommended and is rarely necessary, it is possible for a game
 * object to extend this class to add extra functionality.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Entity {

    /**
     * The logger
     */
    protected static final Logger log = FXGLLogger.getLogger("FXGL.Entity");

    private EntityType type;

    /**
     *
     * @return entity type
     */
    public final EntityType getEntityType() {
        return type;
    }

    /**
     * @return entity type as String
     */
    public final String getTypeAsString() {
        return type.getUniqueType();
    }

    /**
     * Returns true if type of entity equals passed argument.
     *
     * @apiNote equivalent to
     *          <code>getTypeAsString().equals(type.getUniqueType())</code>
     * @param type
     * @return
     */
    public final boolean isType(EntityType type) {
        return getTypeAsString().equals(type.getUniqueType());
    }

    /**
     * Constructs an entity with given type.
     *
     * @param type  the type of entity
     */
    public Entity(EntityType type) {
        this.type = type;
    }

    /**
     * The world the entity is attached to.
     */
    private GameWorld world;

    /**
     * Set the game world.
     *
     * @param world the game world the entity is attached to
     */
    public void setWorld(GameWorld world) {
        this.world = world;
    }

    /**
     * @return  The world the entity is attached to.
     */
    public GameWorld getWorld() {
        return world;
    }

    /**
     * Removes entity from the game world.
     */
    public void removeFromWorld() {
        getWorld().removeEntity(this);
    }

    private EntityView view = new EntityView();

    public EntityView getView() {
        return view;
    }

    /**
     * Set graphics for this entity. The collision detection bounding box will
     * use graphics object's size properties.
     *
     * @param graphics
     * @return this entity
     */
    public final void setView(Node graphics) {
        view.removeChildren();

        if (graphics instanceof Circle) {
            Circle c = (Circle) graphics;
            c.setCenterX(c.getRadius());
            c.setCenterY(c.getRadius());
        }

        view.addChild(graphics);
    }

    /**
     *
     * @return  x property
     */
    public final DoubleProperty xProperty() {
        return view.translateXProperty();
    }

    /**
     * Returns x coordinate of the entity's position.
     * Note: transformations like rotation may affect
     * the visual position but will not affect the value retrieved.
     *
     * @return  x coordinate
     */
    public final double getX() {
        return xProperty().get();
    }

    /**
     * Set x position
     *
     * @param x coordinate of entity position
     */
    public final void setX(double x) {
        xProperty().set(x);
    }

    /**
     *
     * @return  y property
     */
    public final DoubleProperty yProperty() {
        return view.translateYProperty();
    }

    /**
     * Returns y coordinate of the entity's position.
     * Note: transformations like rotation may affect
     * the visual position but will not affect the value retrieved.
     *
     * @return  y coordinate
     */
    public final double getY() {
        return yProperty().get();
    }

    /**
     * Set y position.
     *
     * @param y coordinate of entity position
     */
    public final void setY(double y) {
        yProperty().set(y);
    }

    /**
     *
     * @return absolute position of entity
     */
    public final Point2D getPosition() {
        return new Point2D(getX(), getY());
    }

    /**
     * Set absolute position of entity to given point.
     *
     * @param x coordinate of entity position
     * @param y coordinate of entity position
     */
    public final void setPosition(double x, double y) {
        setX(x);
        setY(y);
    }

    /**
     * Set absolute position of entity to given point.
     *
     * @param position  absolute position in game world
     */
    public final void setPosition(Point2D position) {
        setPosition(position.getX(), position.getY());
    }

    /**
     * Translate (move) entity by vector (x, y).
     *
     * @param x units
     * @param y units
     */
    public final void translate(double x, double y) {
        setX(getX() + x);
        setY(getY() + y);
    }

    /**
     * Translate (move) entity by vector.
     *
     * @param vector    translate vector
     */
    public final void translate(Point2D vector) {
        translate(vector.getX(), vector.getY());
    }

    /**
     * Returns absolute angle of the entity rotation
     * in degrees.
     *
     * @return  rotation angle
     */
    public final double getRotation() {
        return view.getRotate();
    }

    /**
     * Set absolute rotation of the entity view in
     * degrees.
     *
     * @param angle the new rotation angle
     */
    public final void setRotation(double angle) {
        view.setRotate(angle);
    }

    /**
     * Rotate entity view by given angle.
     *
     * @param byAngle   rotation angle in degrees
     */
    public final void rotateBy(double byAngle) {
        setRotation(getRotation() + byAngle);
    }

    /**
     * Returns distance from center of this entity to center of the given
     * entity.
     *
     * @param other the other entity
     * @return distance between two entities
     */
    public final double distance(Entity other) {
        return getCenter().distance(other.getCenter());
    }

    /**
     *
     * @return center point of this entity
     */
    public final Point2D getCenter() {
        return getPosition().add(getWidth() / 2, getHeight() / 2);
    }

    /**
     * Returns an area around the entity with given width and height in each
     * direction of the entity + the area of entity itself. This can be used to
     * find the range of an exploding bomb, or area around the player with
     * interactive entities. This can be used together with
     * {@link com.almasb.fxgl.GameWorld#getEntitiesInRange(Rectangle2D, EntityType...)}
     * .
     *
     * @param width
     * @param height
     * @return
     */
    public final Rectangle2D computeRange(double width, double height) {
        double x = getX() - width;
        double y = getY() - height;
        double w = getX() + getWidth() + width - x;
        double h = getY() + getHeight() + height - y;

        return new Rectangle2D(x, y, w, h);
    }

    /**
     *
     * @return width of the bounding box of this entity
     */
    public final double getWidth() {
        return view.getLayoutBounds().getWidth();
    }

    /**
     *
     * @return height of the bounding box of this entity
     */
    public final double getHeight() {
        return view.getLayoutBounds().getHeight();
    }

    /**
     * Maps control type to control object.
     * Ensuring that only 1 object is registered per type.
     */
    private Map<Class<? extends Control>, Control> controls = new HashMap<>();

    /**
     * Returns control of given type or {@link Optional#empty()} if
     * no such type is registered on this entity.
     *
     * @param type  control type
     * @return  control
     */
    public final <T extends Control> Optional<T> getControl(Class<T> type) {
        return Optional.ofNullable(type.cast(controls.get(type)));
    }

    /**
     * Adds behavior to entity.
     * Only 1 control per type is allowed.
     * Anonymous controls are not allowed.
     *
     * <pre>
     * E.g.
     * entity.addControl(new GravityControl());
     *
     * // next line will throw IllegalArgumentException because of duplicate
     * entity.addControl(new GravityControl());
     *
     * // next line will throw IllegalArgumentException because of anonymous
     * entity.addControl(new Control() {
     * });
     * </pre>
     *
     * @param control   the behavior
     */
    public final void addControl(Control control) {
        Class<? extends Control> type = control.getClass();
        if (controls.containsKey(type)) {
            throw new IllegalArgumentException(
                    "Entity already has a control with type: "
                            + type.getCanonicalName());
        }

        controls.put(type, control);
        if (control instanceof AbstractControl) {
            ((AbstractControl) control).setEntity(this);
        }
    }

    /**
     * Remove behavior from entity of given type.
     *
     * @param type  the control type to remove
     */
    public final void removeControl(Class<? extends Control> type) {
        Control c = controls.remove(type);
        if (c == null) {
            log.warning(
                    "Attempted to remove control but entity doesn't have a control with type: "
                            + type.getSimpleName());
        }
    }

    /**
     * Remove all behavior controls from entity.
     */
    public final void removeAllControls() {
        controls.clear();
    }

    /**
     * Maps component types to components.
     */
    private Map<Class<? extends Component>, Component> components = new HashMap<>();

    /**
     * Returns component of given type if registered. The type must be exactly
     * the same as the type of the instance registered.
     *
     * @param type
     * @return
     */
    public final <T extends Component> Optional<T> getComponent(Class<T> type) {
        return Optional.ofNullable(type.cast(components.get(type)));
    }

    /**
     * Adds given component to this entity.
     * Only 1 component with the same type can be registered.
     * Anonymous components are NOT allowed.
     *
     * @param component
     * @throws IllegalArgumentException
     *             if a component with same type already registered
     *             or anonymous
     */
    public final void addComponent(Component component) {
        Class<? extends Component> type = component.getClass();
        if (type.getCanonicalName() == null) {
            throw new IllegalArgumentException(
                    "Anonymous components are not allowed! - "
                            + type.getName());
        }

        if (components.containsKey(type)) {
            throw new IllegalArgumentException(
                    "Entity already has a component with type: "
                            + type.getCanonicalName());
        }
        components.put(type, component);
    }

    /**
     * Remove a component with given type from this entity.
     *
     * @param type
     *            - type of the component to remove
     */
    public final void removeComponent(Class<? extends Component> type) {
        Component c = components.remove(type);
        if (c == null) {
            log.warning(
                    "Attempted to remove component but entity doesn't have a component with type: "
                            + type.getSimpleName());
        }
    }

    private boolean collidable = false;

    /**
     *
     * @return true if the object participates in collision detection, false
     *         otherwise
     */
    public final boolean isCollidable() {
        return collidable;
    }

    /**
     * Enable/disable ability for this entity to participate in collision
     * detection. If collision handler is registered (via PhysicsManager) and
     * both entities have collidable set to true, then the handler will be
     * notified of the collision event.
     *
     * @param collidable
     *            - true enables collision detection, false - disables
     */
    public final void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    public boolean isCollidingWith(Entity other) {
        return this.view.getBoundsInParent()
                .intersects(other.view.getBoundsInParent());
    }

    private ReadOnlyBooleanWrapper alive = new ReadOnlyBooleanWrapper(true);

    /**
     *
     * @return alive property of this entity
     */
    public final ReadOnlyBooleanProperty aliveProperty() {
        return alive.getReadOnlyProperty();
    }

    /**
     * Entity is considered alive from moment the object itself is created and
     * until removeEntity() is called
     *
     * @return
     */
    public final boolean isAlive() {
        return alive.get();
    }

    private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper(false);

    /**
     *
     * @return active property of this entity
     */
    public final ReadOnlyBooleanProperty activeProperty() {
        return active.getReadOnlyProperty();
    }

    /**
     * Entity is "active" from the moment it is registered in the game world
     * and until it is removed from the game world.
     *
     * @return true if entity is active, else false
     */
    public final boolean isActive() {
        return active.get();
    }

    /**
     * Used by temporary entities so that they are automatically removed from
     * the scene graph.
     */
    private Duration expireTime = Duration.ZERO;

    /**
     *
     * @return expireTime of entity, 0 if not set
     */
    public final Duration getExpireTime() {
        return expireTime;
    }

    /**
     * Set expire time for this entity. The timer starts when the entity is
     * added to game scene. Calling this method after entity was added to scene
     * has no effect.
     *
     * Once the timer has expired, the entity will be removed with
     * removeEntity()
     *
     * @param duration
     */
    public final void setExpireTime(Duration duration) {
        expireTime = duration;
    }

    private boolean controlsEnabled = true;

    /**
     * Setting this to false will disable each control's update until this has
     * been set back to true.
     *
     * @param b
     */
    public final void setControlsEnabled(boolean b) {
        controlsEnabled = b;
    }

    /**
     * Do NOT call manually. It is called automatically by FXGL GameApplication
     *
     * @param now
     */
    public final void update() {
        if (controlsEnabled)
            controls.values().forEach(c -> c.onUpdate(this));
        onUpdate();
    }

    /**
     * Can be overridden to provide subclass implementation.
     */
    protected void onUpdate() {}

    public final void init() {
        active.set(true);
    }

    /**
     * Do NOT call manually. It is called automatically by FXGL GameApplication
     * when entity has been removed
     *
     */
    public final void clean() {
        alive.set(false);
        active.set(false);
        onClean();
        eventHandlers.clear();
        controls.clear();
        components.clear();
        view.removeChildren();
    }

    /**
     * Can be overridden to provide subclass implementation.
     */
    protected void onClean() {}

    private Map<String, FXGLEventHandler> eventHandlers = new HashMap<>();

    /**
     * Register an event handler for FXGLEventType. The handler will be notified
     * when an event of the type occurs on this entity.
     *
     * @param type
     * @param eventHandler
     */
    public final void addFXGLEventHandler(FXGLEventType type,
            FXGLEventHandler eventHandler) {
        eventHandlers.put(type.getUniqueType(), eventHandler);
    }

    public final void removeFXGLEventHandler(FXGLEventType type,
            FXGLEventHandler eventHandler) {
        eventHandlers.remove(type.getUniqueType());
    }

    /**
     * Fire (trigger) an FXGL event on this entity This entity becomes the
     * target of the FXGL event.
     *
     * If the FXGL event doesn't have a source, this entity will also become the
     * source of the event.
     *
     * @param event
     */
    public final void fireFXGLEvent(FXGLEvent event) {
        if (event.getSource() == null)
            event.setSource(this);

        event.setTarget(this);
        eventHandlers.getOrDefault(event.getType().getUniqueType(), e -> {
        }).handle(event);
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
     * @return this entity
     */
    public final Entity setRenderLayer(RenderLayer layer) {
        if (isActive())
            throw new IllegalStateException(
                    "Can't set render layer to active entity.");

        this.renderLayer = layer;
        return this;
    }

    /**
     *
     * @return render layer for entity
     */
    public final RenderLayer getRenderLayer() {
        return renderLayer;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Entity [type=");
        builder.append(getTypeAsString());
        builder.append(", collidable=");
        builder.append(collidable);
        builder.append(", alive=");
        builder.append(isAlive());
        builder.append(", active=");
        builder.append(isActive());
        builder.append(", renderLayer=");
        builder.append(renderLayer.asString());
        builder.append("]");
        return builder.toString();
    }

    /**
     * Returns a new entity without any type.
     *
     * Use this method for background entity, range selection entity, temporary
     * entity, etc when you are not going to use its type.
     *
     * @return
     */
    public static final Entity noType() {
        return new Entity(new EntityType() {
            @Override
            public String getUniqueType() {
                return "__anonymous__";
            }
        });
    }
}
