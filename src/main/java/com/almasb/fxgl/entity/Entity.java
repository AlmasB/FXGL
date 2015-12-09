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

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.control.AbstractControl;
import com.almasb.fxgl.entity.control.Control;
import com.almasb.fxgl.gameplay.GameWorld;
import com.almasb.fxgl.physics.CollisionResult;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.util.FXGLLogger;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

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
     * @param type entity type
     * @return true if entity has given type, false otherwise
     * @implNote equivalent to
     * <code>getTypeAsString().equals(type.getUniqueType())</code>
     */
    public final boolean isType(EntityType type) {
        return getTypeAsString().equals(type.getUniqueType());
    }

    /**
     * Constructs an entity with given type.
     *
     * @param type the type of entity
     */
    public Entity(EntityType type) {
        this.type = type;
    }

    /**
     * The world the entity is attached to.
     */
    private GameWorld world;

    /**
     * Sets the game world. Generates hit boxes from scene view
     * if enabled and scene view exists for this entity.
     * Sets active property to true.
     *
     * DO NOT CALL MANUALLY.
     *
     * @param world the game world the entity is attached to
     */
    public void init(GameWorld world) {
        if (isActive())
            throw new IllegalStateException("Attempted to init active entity");

        if (generateHitBoxesFromView && sceneView != null) {
            addHitBox(new HitBox("__BODY__", new BoundingBox(0, 0,
                    sceneView.getLayoutBounds().getWidth(),
                    sceneView.getLayoutBounds().getHeight())));
        }

        this.world = world;
        active.set(true);
    }

    /**
     * @return The world the entity is attached to.
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

    private EntityView sceneView;

    /**
     *
     * @return optional containing scene view or Optional.empty()
     * if entity doesn't have a scene view
     */
    public final Optional<EntityView> getSceneView() {
        return Optional.ofNullable(sceneView);
    }

    /**
     * Sets the primary view (graphics) for this entity. The view will be used to visualize this entity
     * in the scene.
     *
     * @param view graphics
     */
    public final void setSceneView(Node view) {
        setSceneView(view, RenderLayer.TOP);
    }

    /**
     * Sets the primary view (graphics) for this entity. The view will be used to visualize this entity
     * in the scene. The view will be rendered in the given layer group.
     *
     * @param view graphics
     * @param layer render layer
     */
    public final void setSceneView(Node view, RenderLayer layer) {
        if (this.sceneView != null)
            throw new IllegalStateException("Entity already has a scene view. Only 1 scene view is allowed");
        if (isActive())
            throw new IllegalStateException("Entity already part of the world");

        this.sceneView = new EntityView(this, view);
        this.sceneView.setRenderLayer(layer);
    }

    private DoubleProperty x = new SimpleDoubleProperty();

    /**
     * @return x property
     */
    public final DoubleProperty xProperty() {
        return x;
    }

    /**
     * Returns x coordinate of the entity's position.
     * Note: transformations like rotation may affect
     * the visual position but will not affect the value retrieved.
     *
     * @return x coordinate
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

    private DoubleProperty y = new SimpleDoubleProperty();

    /**
     * @return y property
     */
    public final DoubleProperty yProperty() {
        return y;
    }

    /**
     * Returns y coordinate of the entity's position.
     * Note: transformations like rotation may affect
     * the visual position but will not affect the value retrieved.
     *
     * @return y coordinate
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
     * @param position absolute position in game world
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
     * @param vector translate vector
     */
    public final void translate(Point2D vector) {
        translate(vector.getX(), vector.getY());
    }

    private DoubleProperty rotation = new SimpleDoubleProperty();

    /**
     *
     * @return rotation
     */
    public final DoubleProperty rotationProperty() {
        return rotation;
    }

    /**
     * Returns absolute angle of the entity rotation
     * in degrees.
     *
     * @return rotation angle
     */
    public final double getRotation() {
        return rotationProperty().get();
    }

    /**
     * Set absolute rotation of the entity view in
     * degrees.
     * Note: this doesn't affect hit boxes. For more accurate
     * collisions use {@link com.almasb.fxgl.physics.PhysicsEntity}.
     *
     * @param angle the new rotation angle
     */
    public final void setRotation(double angle) {
        rotationProperty().set(angle);
    }

    /**
     * Rotate entity view by given angle.
     * Note: this doesn't affect hit boxes. For more accurate
     * collisions use {@link com.almasb.fxgl.physics.PhysicsEntity}.
     *
     * @param byAngle rotation angle in degrees
     */
    public final void rotateBy(double byAngle) {
        setRotation(getRotation() + byAngle);
    }

    /**
     * Set absolute rotation of the entity view to angle
     * between vector and positive X axis.
     * This is useful for projectiles (bullets, arrows, etc)
     * which rotate depending on their current velocity.
     * Note, this assumes that at 0 angle rotation the scene view is
     * facing right.
     *
     * @param vector the rotation vector / velocity vector
     */
    public final void rotateToVector(Point2D vector) {
        double angle = Math.toDegrees(Math.atan2(vector.getY(), vector.getX()));
        setRotation(angle);
    }

    private BooleanProperty xFlipped = new SimpleBooleanProperty(false);
    private double xFlipLine = 0;

    /**
     * Line to flip around. E.g. an entity with texture 200x100 as scene view
     * with xFlipLine = 100 will be mirrored perfectly.
     *
     * @return vertical line at X point to use as pivot point for flip
     */
    public final double getXFlipLine() {
        return xFlipLine;
    }

    /**
     *
     * @return x flipped property
     */
    public final BooleanProperty xFlippedProperty() {
        return xFlipped;
    }

    /**
     *
     * @return true iff x axis is flipped
     */
    public final boolean isXFlipped() {
        return xFlippedProperty().get();
    }

    /**
     * Flip X axis of the entity. If set to true, the scene view
     * will be drawn from right to left. This also affects hit boxes
     *
     * @param b x flipped flag
     * @defaultValue false
     */
    public final void setXFlipped(boolean b) {
        xFlippedProperty().set(b);
    }

    /**
     * Flip X axis of the entity. If set to true, the scene view
     * will be drawn from right to left. This also affects hit boxes
     *
     * @param b x flipped flag
     * @param xFlipLine x flip line (pivot line)
     * @defaultValue false
     */
    public final void setXFlipped(boolean b, double xFlipLine) {
        this.xFlipLine = xFlipLine;
        xFlippedProperty().set(b);
    }

    /**
     * Contains all hit boxes (collision bounding boxes) for this entity.
     */
    private ObservableList<HitBox> hitBoxes = FXCollections.observableArrayList();

    /**
     * @return unmodifiable list of hit boxes
     */
    public final ObservableList<HitBox> hitBoxesProperty() {
        return FXCollections.unmodifiableObservableList(hitBoxes);
    }

    /**
     * Add a hit (collision) bounding box.
     *
     * @param hitBox the bounding box
     */
    public final void addHitBox(HitBox hitBox) {
        hitBoxes.add(hitBox);
    }

    /**
     * Removes a hit box with given name from the list of hit boxes for this entity.
     *
     * @param name hit box name
     */
    public final void removeHitBox(String name) {
        hitBoxes.removeIf(h -> h.getName().equals(name));
    }

    private boolean generateHitBoxesFromView = true;

    /**
     * Set to false if hit boxes have been added manually.
     * Otherwise, FXGL will attempt to generate another hit box
     * from the scene view.
     *
     * @param b flag
     * @defaultValue true
     */
    public final void setGenerateHitBoxesFromView(boolean b) {
        generateHitBoxesFromView = b;
    }

    /**
     * Checks for collision with another entity. Returns collision result
     * containing the first hit box that triggered collision.
     * If no collision - {@link CollisionResult#NO_COLLISION} will be returned.
     *
     * @param other entity to check collision against
     * @return collision result
     */
    public final CollisionResult checkCollision(Entity other) {
        for (HitBox box1 : hitBoxes) {
            Bounds b = isXFlipped() ? box1.translateXFlipped(getX(), getY(), getWidth()) : box1.translate(getX(), getY());
            for (HitBox box2 : other.hitBoxes) {
                Bounds b2 = other.isXFlipped()
                        ? box2.translateXFlipped(other.getX(), other.getY(), other.getWidth())
                        : box2.translate(other.getX(), other.getY());
                if (b.intersects(b2)) {
                    return new CollisionResult(box1, box2);
                }
            }
        }

        return CollisionResult.NO_COLLISION;
    }

    /**
     * @param other the other entity
     * @return true iff this entity is colliding with other based on
     * their hit boxes, in current frame
     */
    public final boolean isCollidingWith(Entity other) {
        return checkCollision(other) != CollisionResult.NO_COLLISION;
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
     * {@link GameWorld#getEntitiesInRange(Rectangle2D, EntityType...)}
     * .
     *
     * @param width radius width
     * @param height radius height
     * @return computed area
     */
    public final Rectangle2D computeRange(double width, double height) {
        double x = getX() - width;
        double y = getY() - height;
        double w = getX() + getWidth() + width - x;
        double h = getY() + getHeight() + height - y;

        return new Rectangle2D(x, y, w, h);
    }

    /**
     * Computes width of entity based on its hit boxes.
     *
     * @return width
     * @implNote it computes the rightmost point in X, so
     * it will return incorrect width if hit box doesn't start from 0
     */
    private double computeWidth() {
        // we take maxX because even if entity is translated
        // maxX is relative to entity's 0 origin
        return hitBoxes.stream()
                .mapToDouble(HitBox::getMaxX)
                .max()
                .orElse(0);
    }

    /**
     * Computes height of entity based on its hit boxes.
     *
     * @return height
     * @implNote it computes the highest point in Y, so
     * it will return incorrect height if hit box doesn't start from 0
     */
    private double computeHeight() {
        // we take maxY because even if entity is translated
        // maxY is relative to entity's 0 origin
        return hitBoxes.stream()
                .mapToDouble(HitBox::getMaxY)
                .max()
                .orElse(0);
    }

    /**
     * @return width of the bounding box of this entity
     */
    public final double getWidth() {
        return computeWidth();
    }

    /**
     * @return height of the bounding box of this entity
     */
    public final double getHeight() {
        return computeHeight();
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
     * @param type control type
     * @return control
     */
    public final <T extends Control> Optional<T> getControl(Class<T> type) {
        return Optional.ofNullable(type.cast(controls.get(type)));
    }

    /**
     * Adds behavior to entity.
     * Only 1 control per type is allowed.
     * Anonymous controls are not allowed.
     * <p>
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
     * @param control the behavior
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
     * @param type the control type to remove
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
     * @param type component type
     * @return component
     */
    public final <T extends Component> Optional<T> getComponent(Class<T> type) {
        return Optional.ofNullable(type.cast(components.get(type)));
    }

    /**
     * Adds given component to this entity.
     * Only 1 component with the same type can be registered.
     * Anonymous components are NOT allowed.
     *
     * @param component the component
     * @throws IllegalArgumentException if a component with same type already registered
     *                                  or anonymous
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
     * @param type - type of the component to remove
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
     * @return true if the object participates in collision detection, false
     * otherwise
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
     * @param collidable true enables collision detection, false - disables
     * @defaultValue false
     */
    public final void setCollidable(boolean collidable) {
        this.collidable = collidable;
    }

    private ReadOnlyBooleanWrapper alive = new ReadOnlyBooleanWrapper(true);

    /**
     * @return alive property of this entity
     */
    public final ReadOnlyBooleanProperty aliveProperty() {
        return alive.getReadOnlyProperty();
    }

    /**
     * Entity is considered alive from moment the object itself is created and
     * until removeEntity() is called
     *
     * @return true if entity is alive, false otherwise
     */
    public final boolean isAlive() {
        return alive.get();
    }

    private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper(false);

    /**
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
     * @return expireTime of entity, 0 if not set
     */
    public final Duration getExpireTime() {
        return expireTime;
    }

    /**
     * Set expire time for this entity. The timer starts when the entity is
     * added to game scene. Calling this method after entity was added to scene
     * has no effect.
     * <p>
     * Once the timer has expired, the entity will be removed with
     * removeEntity()
     *
     * @param duration time after which the entity will be removed from world
     */
    public final void setExpireTime(Duration duration) {
        expireTime = duration;
    }

    private boolean controlsEnabled = true;

    /**
     * Setting this to false will disable each control's update until this has
     * been set back to true.
     *
     * @param b controls enabled flag
     */
    public final void setControlsEnabled(boolean b) {
        controlsEnabled = b;
    }

    /**
     * Do NOT call manually. It is called automatically by the world.
     */
    public final void update() {
        if (controlsEnabled)
            controls.values().forEach(c -> c.onUpdate(this));
        onUpdate();
    }

    /**
     * Can be overridden to provide subclass implementation.
     */
    protected void onUpdate() {
    }

    /**
     * Do NOT call manually. It is called automatically by the world
     * when entity has been removed.
     */
    public final void clean() {
        alive.set(false);
        active.set(false);
        onClean();
        controls.clear();
        components.clear();
    }

    /**
     * Can be overridden to provide subclass implementation.
     */
    protected void onClean() {
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
        builder.append("]");
        return builder.toString();
    }

    /**
     * Returns a new entity without any type.
     * <p>
     * Use this method for background entity, range selection entity, temporary
     * entity, etc when you are not going to use its type.
     *
     * @return entity with no type
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
