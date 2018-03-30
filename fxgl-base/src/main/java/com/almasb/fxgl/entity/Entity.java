/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.reflect.ReflectionUtils;
import com.almasb.fxgl.entity.component.*;
import com.almasb.fxgl.entity.components.*;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.io.serialization.Bundle;
import com.almasb.fxgl.script.Script;
import com.almasb.fxgl.util.Optional;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.util.BackportKt.forEach;

/**
 * A generic entity in the Entity-Component-Control model.
 * During update (or control update) it is not allowed to:
 * <ul>
 *     <li>Add control</li>
 *     <li>Remove control</li>
 * </ul>
 *
 * Entity is guaranteed to have Type, Position, Rotation, BBox, View components.
 * The best practice is to add all component and controls to an entity before attaching
 * the entity to the world and pause the (immediately) unused controls.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Entity {

    private static final Logger log = Logger.get(Entity.class);

    private ObjectMap<String, Object> properties = new ObjectMap<>();

    private ObjectMap<String, Script> scripts = new ObjectMap<>();

    private ObjectMap<Class<? extends Component>, Component> components = new ObjectMap<>();

    private List<ComponentListener> componentListeners = new ArrayList<>();

    private GameWorld world = null;

    private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper(false);

    private boolean controlsEnabled = true;

    private Runnable onActive = null;
    private Runnable onNotActive = null;

    private boolean updating = false;

    private TypeComponent type = new TypeComponent();
    private PositionComponent position = new PositionComponent();
    private RotationComponent rotation = new RotationComponent();
    private BoundingBoxComponent bbox = new BoundingBoxComponent();
    private ViewComponent view = new ViewComponent();

    public Entity() {
        addComponent(type);
        addComponent(position);
        addComponent(rotation);
        addComponent(bbox);
        addComponent(view);
    }

    /**
     * @return the world this entity is attached to
     */
    public final GameWorld getWorld() {
        return world;
    }

    /**
     * Initializes this entity.
     *
     * @param world the world to which entity is being attached
     */
    final void init(GameWorld world) {
        this.world = world;
        if (onActive != null)
            onActive.run();
        active.set(true);
    }

    /**
     * Removes all controls and components.
     * Resets entity to its "new" state.
     */
    void clean() {
        removeAllComponents();

        properties.clear();

        componentListeners.clear();

        world = null;
        onActive = null;
        onNotActive = null;

        controlsEnabled = true;
        updating = false;

        active.set(false);
    }

    /**
     * Equivalent to world?.removeEntity(this);
     */
    public final void removeFromWorld() {
        if (world != null)
            world.removeEntity(this);
    }

    public final TypeComponent getTypeComponent() {
        return type;
    }

    public final PositionComponent getPositionComponent() {
        return position;
    }

    public final RotationComponent getRotationComponent() {
        return rotation;
    }

    public final BoundingBoxComponent getBoundingBoxComponent() {
        return bbox;
    }

    public final ViewComponent getViewComponent() {
        return view;
    }

    // TYPE BEGIN

    public final Serializable getType() {
        return type.getValue();
    }

    public final void setType(Serializable type) {
        this.type.setValue(type);
    }

    /**
     * <pre>
     *     Example:
     *     entity.isType(Type.PLAYER);
     * </pre>
     *
     * @param type entity type
     * @return true iff this type component is of given type
     */
    public final boolean isType(Object type) {
        return this.type.isType(type);
    }

    public final ObjectProperty<Serializable> typeProperty() {
        return type.valueProperty();
    }

    // TYPE END

    // POSITION BEGIN

    /**
     * @return top left point of this entity in world coordinates
     */
    public final Point2D getPosition() {
        return position.getValue();
    }

    /**
     * Set top left position of this entity in world coordinates.
     */
    public final void setPosition(Point2D position) {
        this.position.setValue(position);
    }

    /**
     * Set top left position of this entity in world coordinates.
     */
    public final void setPosition(double x, double y) {
        this.position.setValue(x, y);
    }

    /**
     * @return top left x
     */
    public final double getX() {
        return position.getX();
    }

    /**
     * @return top left y
     */
    public final double getY() {
        return position.getY();
    }

    /**
     * Set position top left x of this entity.
     */
    public final void setX(double x) {
        position.setX(x);
    }

    /**
     * Set position top left y of this entity.
     */
    public final void setY(double y) {
        position.setY(y);
    }

    public final DoubleProperty xProperty() {
        return position.xProperty();
    }

    public final DoubleProperty yProperty() {
        return position.yProperty();
    }

    /**
     * Translate x and y by given vector.
     */
    public final void translate(Point2D vector) {
        position.translate(vector);
    }

    /**
     * Translate x and y by given vector.
     */
    public final void translate(Vec2 vector) {
        position.translate(vector.x, vector.y);
    }

    /**
     * Translate x and y by given vector.
     *
     * @param dx vector x
     * @param dy vector y
     */
    public final void translate(double dx, double dy) {
        position.translate(dx, dy);
    }

    /**
     * Translate x by given value.
     */
    public final void translateX(double dx) {
        position.translateX(dx);
    }

    /**
     * Translate y by given value.
     */
    public final void translateY(double dy) {
        position.translateY(dy);
    }

    /**
     * Instantly moves this entity distance units towards given point.
     *
     * @param point the point to move towards
     * @param distance the distance to move
     */
    public final void translateTowards(Point2D point, double distance) {
        position.translateTowards(point, distance);
    }

    /**
     * @return distance in pixels from this entity to the other
     */
    public final double distance(Entity other) {
        return position.distance(other.position);
    }

    // POSITION END

    // ROTATION BEGIN

    /**
     * @return rotation angle
     */
    public final double getRotation() {
        return rotation.getValue();
    }

    /**
     * Set absolute rotation angle.
     */
    public final void setRotation(double angle) {
        rotation.setValue(angle);
    }

    public final DoubleProperty angleProperty() {
        return rotation.angleProperty();
    }

    /**
     * Rotate entity view by given angle clockwise.
     * To rotate counter clockwise use a negative angle value.
     *
     * Note: this doesn't affect hit boxes. For more accurate
     * collisions use {@link com.almasb.fxgl.physics.PhysicsComponent}.
     *
     * @param angle rotation angle in degrees
     */
    public final void rotateBy(double angle) {
        rotation.rotateBy(angle);
    }

    /**
     * Set absolute rotation of the entity view to angle
     * between vector and positive X axis.
     * This is useful for projectiles (bullets, arrows, etc)
     * which rotate depending on their current velocity.
     * Note, this assumes that at 0 angle rotation the view is facing right.
     *
     * @param vector the rotation vector / velocity vector
     */
    public final void rotateToVector(Point2D vector) {
        rotation.rotateToVector(vector);
    }

    // ROTATION END

    // BBOX BEGIN

    /**
     * @return width of this entity based on bounding box
     */
    public final double getWidth() {
        return bbox.getWidth();
    }

    /**
     * @return height of this entity based on bounding box
     */
    public final double getHeight() {
        return bbox.getHeight();
    }

    public final ReadOnlyDoubleProperty widthProperty() {
        return bbox.widthProperty();
    }

    public final ReadOnlyDoubleProperty heightProperty() {
        return bbox.heightProperty();
    }

    /**
     * @return the rightmost x of this entity in world coordinates based on bounding box
     */
    public final double getRightX() {
        return bbox.getMaxXWorld();
    }

    /**
     * @return the bottom y of this entity in world coordinates based on bounding box
     */
    public final double getBottomY() {
        return bbox.getMaxYWorld();
    }

    /**
     * @return center point of this entity in world coordinates based on bounding box
     */
    public final Point2D getCenter() {
        return bbox.getCenterWorld();
    }

    /**
     * @param other the other game entity
     * @return true iff bbox of this entity is colliding with bbox of other
     */
    public final boolean isColliding(Entity other) {
        return bbox.isCollidingWith(other.bbox);
    }

    /**
     * @param bounds a rectangular box that represents bounds
     * @return true iff entity is partially or entirely within given bounds
     */
    public final boolean isWithin(Rectangle2D bounds) {
        return bbox.isWithin(bounds);
    }

    // BBOX END

    // VIEW BEGIN

    public final EntityView getView() {
        return this.view.getView();
    }

    public final void setView(Node view) {
        this.view.setView(view);
    }

    /**
     * Set view from texture.
     *
     * @param textureName name of texture
     */
    public final void setViewFromTexture(String textureName) {
        this.view.setTexture(textureName);
    }

    /**
     * Set view from texture and generate bbox from it.
     *
     * @param textureName name of texture
     */
    public final void setViewFromTextureWithBBox(String textureName) {
        this.view.setTexture(textureName, true);
    }

    /**
     * Set view and generate bounding boxes from view.
     */
    public final void setViewWithBBox(Node view) {
        this.view.setView(view, true);
    }

    public final RenderLayer getRenderLayer() {
        return this.view.getRenderLayer();
    }

    public final void setRenderLayer(RenderLayer layer) {
        this.view.setRenderLayer(layer);
    }

    /**
     * Scale view x.
     */
    public final void setScaleX(double scaleX) {
        view.getView().setScaleX(scaleX);
    }

    /**
     * Scale view y.
     */
    public final void setScaleY(double scaleY) {
        view.getView().setScaleY(scaleY);
    }

    // VIEW END

    public final ReadOnlyBooleanProperty activeProperty() {
        return active.getReadOnlyProperty();
    }

    /**
     * Entity is "active" from the moment it is added to the world
     * and until it is removed from the world.
     *
     * @return true if entity is active, else false
     */
    public final boolean isActive() {
        return active.get();
    }

    /**
     * Set a callback for when entity is added to world.
     * The callback will NOT be executed if entity is already in the world.
     *
     * @param action callback
     */
    public final void setOnActive(Runnable action) {
        onActive = action;
    }

    /**
     * Set a callback for when entity is removed from world.
     * The callback will NOT be executed if entity is already removed from the world.
     *
     * @param action callback
     */
    public final void setOnNotActive(Runnable action) {
        onNotActive = action;
    }

    /**
     * Sets entity to be not active.
     */
    void markForRemoval() {
        if (onNotActive != null)
            onNotActive.run();
        active.set(false);
    }

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
     * Update tick for this entity.
     *
     * @param tpf time per frame
     */
    void update(double tpf) {
        updating = true;

        if (controlsEnabled) {
            for (Component c : components.values()) {
                if (!c.isPaused()) {
                    c.onUpdate(tpf);
                }
            }
        }

        updating = false;
    }

    private static final Object NULL = new Object();

    public ObjectMap<String, Object> getProperties() {
        return properties;
    }

    /**
     * @param key property key
     * @param value property value
     */
    public final void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * @param key property key
     * @return property value or null if key not present
     */
    @SuppressWarnings("unchecked")
    public final <T> T getProperty(String key) {
        Object value = properties.get(key, NULL);
        if (value == NULL) {
            log.warning("Access property with missing key: " + key);
            return null;
        }

        return (T) value;
    }

    /**
     * @param key property key
     * @return property value or Optional.empty() if value is null or key not present
     */
    @SuppressWarnings("unchecked")
    public final <T> Optional<T> getPropertyOptional(String key) {
        Object value = properties.get(key, null);
        return Optional.ofNullable((T) value);
    }

    /**
     * @param type component type
     * @return true iff entity has a component of given type
     */
    public final boolean hasComponent(Class<? extends Component> type) {
        return components.containsKey(type);
    }

    /**
     * Returns component of given type, or {@link Optional#empty()}
     * if type not registered.
     *
     * @param type component type
     * @return component
     */
    public final <T extends Component> Optional<T> getComponentOptional(Class<T> type) {
        return Optional.ofNullable(type.cast(components.get(type)));
    }

    /**
     * @param type component type
     * @return component of given type
     */
    public final <T extends Component> T getComponent(Class<T> type) {
        Component component = components.get(type);

        if (component == null) {
            throw new IllegalArgumentException("Component " + type.getSimpleName() + " not found!");
        }

        return type.cast(component);
    }

    /**
     * Warning: object allocation.
     * Cannot be called during update.
     *
     * @return array of components
     */
    public final Array<Component> getComponents() {
        return components.values().toArray();
    }

    /**
     * Adds given component to this entity.
     *
     * @param component the component
     * @throws IllegalArgumentException if a component with same type already registered or anonymous
     * @throws IllegalStateException if components required by the given component are missing
     */
    public final void addComponent(Component component) {
        checkNotUpdating("Add control");

        addModule(component);

        components.put(component.getClass(), component);
    }

    /**
     * Remove a component with given type from this entity.
     * Core components (type, position, rotation, bbox, view) cannot be removed.
     *
     * @param type type of the component to remove
     * @throws IllegalArgumentException if the component is required by other components / controls
     * @return true if removed, false if not found
     */
    public final boolean removeComponent(Class<? extends Component> type) {
        checkNotUpdating("Remove control");

        if (isCoreComponent(type)) {
            // this is not allowed by design, hence throw
            throw new IllegalArgumentException("Removing a core component: " + type + " is not allowed");
        }

        if (!hasComponent(type))
            return false;

        checkNotRequiredByAny(type);

        removeModule(getComponent(type));

        components.remove(type);

        return true;
    }

    private boolean isCoreComponent(Class<? extends Component> type) {
        return type.getAnnotation(CoreComponent.class) != null;
    }

    private void removeAllComponents() {
        for (Component comp : components.values()) {
            removeModule(comp);
        }

        components.clear();
    }

    public void addComponentListener(ComponentListener listener) {
        componentListeners.add(listener);
    }

    public void removeComponentListener(ComponentListener listener) {
        componentListeners.remove(listener);
    }

    private void addModule(Component comp) {
        checkRequirementsMet(comp.getClass());

        try {
            Method m = Component.class.getDeclaredMethod("setEntity", Entity.class);
            m.setAccessible(true);
            m.invoke(comp, this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //comp.setEntity(this);

        injectFields(comp);

        comp.onAdded();
        notifyModuleAdded(comp);
    }

    @SuppressWarnings("unchecked")
    private void injectFields(Component component) {
        forEach(
                ReflectionUtils.findFieldsByTypeRecursive(component, Component.class),
                field -> {
                    getComponentOptional((Class<? extends Component>) field.getType()).ifPresent(comp -> {
                        ReflectionUtils.inject(field, component, comp);
                    });
                }
        );

        // TODO: remove this convention
        injectEntityField(component);
    }

    @Deprecated
    private void injectEntityField(Component component) {
        // check if component has conventional name
        String controlName = component.getClass().getSimpleName();
        if (controlName.endsWith("Control") && controlName.length() > 8) {
            // SomeTestControl becomes someTest
            String fieldName = controlName.substring(1, controlName.length() - 7);
            fieldName = Character.toLowerCase(controlName.charAt(0)) + fieldName;

            ReflectionUtils.getDeclaredField(fieldName, component).ifPresent(field -> {
                // check that field type is Entity or subtype of Entity
                if (Entity.class.isAssignableFrom(field.getType()))
                    ReflectionUtils.inject(field, component, this);
            });
        }
    }

    private void removeModule(Component comp) {
        notifyModuleRemoved(comp);

        comp.onRemoved();

        try {
            Method m = Component.class.getDeclaredMethod("setEntity", Entity.class);
            m.setAccessible(true);
            m.invoke(comp, new Object[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //comp.setEntity(null);
    }

    private <T extends Component> void notifyModuleAdded(T c) {
        for (int i = 0; i < componentListeners.size(); i++) {
            componentListeners.get(i).onAdded(c);
        }
    }

    private <T extends Component> void notifyModuleRemoved(T c) {
        for (int i = 0; i < componentListeners.size(); i++) {
            componentListeners.get(i).onRemoved(c);
        }
    }

    private void checkNotUpdating(String action) {
        if (updating)
            throw new IllegalStateException("Cannot " + action + " during updating");
    }

    private void checkNotAnonymous(Class<?> type) {
        if (type.isAnonymousClass() || type.getCanonicalName() == null) {
            throw new IllegalArgumentException("Anonymous types are not allowed: " + type.getName());
        }
    }

    @SuppressWarnings("unchecked")
    private void checkNotDuplicate(Class<? extends Component> type) {
        if (hasComponent(type)) {
            throw new IllegalArgumentException("Entity already has type: " + type.getCanonicalName());
        }
    }

    private void checkRequirementsMet(Class<? extends Component> type) {
        checkNotAnonymous(type);

        checkNotDuplicate(type);

        Required[] required = type.getAnnotationsByType(Required.class);

        for (Required r : required) {
            if (!hasComponent(r.value())) {
                throw new IllegalStateException("Required component: [" + r.value().getSimpleName() + "] for: " + type.getSimpleName() + " is missing");
            }
        }
    }

    private void checkNotRequiredByAny(Class<? extends Component> type) {
        // check components
        for (Class<?> t : components.keys()) {
            checkNotRequiredBy(t, type);
        }
    }

    /**
     * Fails with IAE if [requiringType] has a dependency on [type].
     */
    private void checkNotRequiredBy(Class<?> requiringType, Class<? extends Component> type) {
        for (Required required : requiringType.getAnnotationsByType(Required.class)) {
            if (required.value().equals(type)) {
                throw new IllegalArgumentException("Required component: [" + required.value().getSimpleName() + "] by: " + requiringType.getSimpleName());
            }
        }
    }

    /**
     * Searches for a property with key scriptType and uses its value
     * to load a script, which is then cached.
     * The script typically takes an event object, but
     * custom scripts are allowed.
     *
     * @param scriptType e.g. onActivate, onHit, onLevelUp
     * @return a script that handles a specific event (scriptType)
     */
    public final Optional<Script> getScriptHandler(String scriptType) {
        if (scripts.containsKey(scriptType)) {
            return Optional.of(scripts.get(scriptType));
        }

        return getPropertyOptional(scriptType).map(scriptFile -> {
            Script script = FXGL.getAssetLoader().loadScript((String) scriptFile);

            scripts.put(scriptType, script);

            return script;
        });
    }

    /**
     * Creates a new instance, which is a copy of this entity.
     * For each copyable component, copy() will be invoked on the component and attached to new instance.
     * For each copyable control, copy() will be invoked on the control and attached to new instance.
     * Components and controls that cannot be copied, must be added manually if required.
     *
     * @return copy of this entity
     */
    public Entity copy() {
        return EntityCopier.INSTANCE.copy(this);
    }

    /**
     * Save entity state into bundle.
     * Only serializable components and controls will be written.
     *
     * @param bundle the bundle to write to
     */
    public void save(Bundle bundle) {
        EntitySerializer.INSTANCE.save(this, bundle);
    }

    /**
     * Load entity state from a bundle.
     * Only serializable components and controls will be read.
     * If an entity has a serializable type that is not present in the bundle,
     * a warning will be logged but no exception thrown.
     *
     * @param bundle bundle to read from
     */
    public void load(Bundle bundle) {
        EntitySerializer.INSTANCE.load(this, bundle);
    }

    @Override
    public String toString() {
        return "Entity(" + "components=" + components + ")";
    }
}
