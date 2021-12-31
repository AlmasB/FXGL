/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

import com.almasb.fxgl.animation.Animatable;
import com.almasb.fxgl.core.Copyable;
import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.reflect.ReflectionUtils;
import com.almasb.fxgl.core.util.EmptyRunnable;
import com.almasb.fxgl.entity.component.*;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.entity.components.TransformComponent;
import com.almasb.fxgl.entity.components.TypeComponent;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.logging.Logger;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

import static com.almasb.fxgl.core.reflect.ReflectionUtils.*;

/**
 * A generic game object.
 * Behavior and data are added via components.
 *
 * During update (or component update) it is not allowed to:
 * <ul>
 *     <li>Add component</li>
 *     <li>Remove component</li>
 * </ul>
 *
 * Entity is guaranteed to have Type, Transform, BBox, View components.
 * The best practice is to add all components to an entity before attaching
 * the entity to the world and pause the components.
 * You can then resume components when you need them, rather than adding them later.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Entity implements Animatable, Copyable<Entity> {

    private static class ComponentMap {
        private Map<Class<? extends Component>, Component> components = new HashMap<>();
        private List<Component> componentList = new ArrayList<>();

        void update(double tpf) {
            for (int i = 0; i < componentList.size(); i++) {
                Component c = componentList.get(i);
                if (!c.isPaused()) {
                    c.onUpdate(tpf);
                }
            }
        }

        <T extends Component> boolean has(Class<T> type) {
            return components.containsKey(type);
        }

        void add(Component c) {
            components.put(c.getClass(), c);
            componentList.add(c);
        }

        void remove(Class<? extends Component> type) {
            var c = components.remove(type);
            componentList.remove(c);
        }

        <T extends Component> Component get(Class<T> type) {
            return components.get(type);
        }

        Set<Class<? extends Component>> types() {
            return components.keySet();
        }

        List<Component> getAll() {
            return new ArrayList<>(componentList);
        }

        void clear() {
            components.clear();
            componentList.clear();
        }
    }

    private static class ComponentMethod {
        private Method method;
        private Component component;

        ComponentMethod(Component component, Method method) {
            this.component = component;
            this.method = method;
        }

        <T> T call(Object... args) throws Exception {
            return (T) method.invoke(component, args);
        }
    }

    private static final Logger log = Logger.get(Entity.class);

    private PropertyMap properties = new PropertyMap();
    private ComponentMap components = new ComponentMap();

    private Map<String, ComponentMethod> componentMethods = new HashMap<>();

    private List<ComponentListener> componentListeners = new ArrayList<>();

    private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper(false);

    private Runnable onActive = EmptyRunnable.INSTANCE;
    private Runnable onNotActive = EmptyRunnable.INSTANCE;

    private boolean isEverUpdated = true;
    private boolean isUpdateEnabled = true;
    private boolean isUpdating = false;
    private boolean isReusable = false;

    private TypeComponent type = new TypeComponent();
    private TransformComponent transform = new TransformComponent();
    private BoundingBoxComponent bbox = new BoundingBoxComponent();
    private ViewComponent view = new ViewComponent();

    private GameWorld world = null;

    public Entity() {
        addComponentNoChecks(type);
        addComponentNoChecks(transform);
        addComponentNoChecks(bbox);
        addComponentNoChecks(view);
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
    void init(GameWorld world) {
        this.world = world;
        onActive.run();
        active.set(true);
    }

    /**
     * Removes all components.
     * Resets entity to its "new" state.
     *
     * https://github.com/AlmasB/FXGL/issues/528
     */
    void clean() {
        if (isReusable) {
            world = null;
            return;
        }

        removeAllComponents();

        properties.clear();

        componentListeners.clear();
        componentMethods.clear();

        world = null;
        onActive = EmptyRunnable.INSTANCE;
        onNotActive = EmptyRunnable.INSTANCE;

        isUpdateEnabled = true;
        isUpdating = false;
    }

    /**
     * Equivalent to world?.removeEntity(this);
     */
    public final void removeFromWorld() {
        if (world != null)
            world.removeEntity(this);
    }

    /**
     * @return false if this entity never needs an update
     */
    public final boolean isEverUpdated() {
        return isEverUpdated;
    }

    /**
     * Set this to false for performance improvements if this entity never needs an update.
     * This setting is only honored if it was set before adding the entity to game world.
     */
    public final void setEverUpdated(boolean everUpdated) {
        isEverUpdated = everUpdated;
    }

    /**
     * If set to false, the components attached to this entity will not update.
     */
    public final void setUpdateEnabled(boolean b) {
        isUpdateEnabled = b;
    }

    /**
     * @return can this entity be removed from world and re-added
     */
    public boolean isReusable() {
        return isReusable;
    }

    /**
     * Set the entity to be reusable or not.
     * A reusable entity is not cleaned when removed from world.
     * Instead, the entity is put into a pool using its spawnName.
     * Next time an entity with spawnName needs to be spawned, the pooled entity will be added to world.
     * By default, entities are not reusable.
     */
    public void setReusable(boolean isReusable) {
        this.isReusable = isReusable;
    }

    /**
     * Update tick for this entity.
     *
     * @param tpf time per frame
     */
    void update(double tpf) {
        if (!isUpdateEnabled)
            return;

        isUpdating = true;

        components.update(tpf);

        isUpdating = false;
    }

    /**
     * Sets entity to be not active.
     */
    void markForRemoval() {
        onNotActive.run();
        active.set(false);
    }

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

    public final PropertyMap getProperties() {
        return properties;
    }

    /**
     * @param key property key
     * @param value property value
     */
    public final void setProperty(String key, Object value) {
        properties.setValue(key, value);
    }

    public final <T> Optional<T> getPropertyOptional(String key) {
        return properties.getValueOptional(key);
    }

    public final int getInt(String key) {
        return properties.getInt(key);
    }

    public final double getDouble(String key) {
        return properties.getDouble(key);
    }

    public final boolean getBoolean(String key) {
        return properties.getBoolean(key);
    }

    public final String getString(String key) {
        return properties.getString(key);
    }

    public final <T> T getObject(String key) {
        return properties.getObject(key);
    }

    public final void addComponentListener(ComponentListener listener) {
        componentListeners.add(listener);
    }

    public final void removeComponentListener(ComponentListener listener) {
        componentListeners.remove(listener);
    }

    /**
     * @param type component type
     * @return true iff entity has a component of given type
     */
    public final boolean hasComponent(Class<? extends Component> type) {
        return components.has(type);
    }

    /**
     * Returns component of given type, or {@link Optional#empty()}
     * if entity has no such component.
     *
     * @param type component type
     * @return component
     */
    public final <T extends Component> Optional<T> getComponentOptional(Class<T> type) {
        return Optional.ofNullable(type.cast(components.get(type)));
    }

    /**
     * @param type component type
     * @return component of given type or throws exception if entity has no such component
     */
    public final <T extends Component> T getComponent(Class<T> type) {
        Component component = components.get(type);

        if (component == null) {
            throw new IllegalArgumentException("Component " + type.getSimpleName() + " not found!");
        }

        return type.cast(component);
    }

    /**
     * @return a new list containing all components of this entity
     */
    public final List<Component> getComponents() {
        return components.getAll();
    }

    /**
     * Adds given component to this entity.
     *
     * @param component the component
     * @throws IllegalArgumentException if a component with same type already registered
     * @throws IllegalStateException if components required by the given component are missing
     */
    public final void addComponent(Component component) {

        if (isUpdating) {
            log.warning("Cannot add / remove components during updating");
            return;
        }
        if (checkRequirementsMet(component.getClass())) {
            addComponentNoChecks(component);
        }

    }

    private void addComponentNoChecks(Component component) {
        injectFields(component);

        component.onAdded();
        notifyComponentAdded(component);

        components.add(component);
    }

    /**
     * Remove a component with given type from this entity.
     * Core components (type, position, rotation, bbox, view) cannot be removed.
     *
     * @param type type of the component to remove
     * @throws IllegalArgumentException if the component is required by other components
     * @return true if removed, false if not found
     */
    public final boolean removeComponent(Class<? extends Component> type) {
        if (!hasComponent(type))
            return false;

        if (isCoreComponent(type)) {
            log.warning("Removing a core component: " + type + " is not allowed. Ignoring");
            return false;
        }

        if (isUpdating) {
            log.warning("Cannot add / remove components during updating");
            return false;
        }

        checkNotRequiredByAny(type);

        removeComponent(getComponent(type));

        components.remove(type);

        return true;
    }

    /**
     * Call a method of a component attached to this entity.
     * For example: given AttackComponent with method attack(), you can call
     * entity.call("attack"), which is equivalent to
     * entity.getComponent(AttackComponent.class).attack().
     *
     * Note: avoid using more than one component with same method names.
     */
    public <T> T call(String componentMethodName, Object... args) {
        ComponentMethod method;

        if (componentMethods.containsKey(componentMethodName)) {
            method = componentMethods.get(componentMethodName);
        } else {
            var types = Arrays.stream(args)
                    .map(Object::getClass)
                    .map(ReflectionUtils::convertToPrimitive)
                    .toArray(Class[]::new);

            method = findMethod(componentMethodName, types)
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find method: " + format(componentMethodName, types)));

            componentMethods.put(componentMethodName, method);
        }

        try {
            return method.call(args);
        } catch (Exception e) {
            String message = "Failed to call: " + format(componentMethodName, args) + " Cause: " + getRootCause(e);

            throw new IllegalArgumentException(message, e);
        }
    }

    /**
     * @return a method name formatted as method signature
     */
    private String format(String methodName, Object[] args) {
        String argsString = Arrays.toString(args);

        return methodName + "(" + argsString.substring(1, argsString.length() - 1) + ")";
    }

    private Optional<ComponentMethod> findMethod(String name, Class<?>... types) {
        for (Component c : components.getAll()) {
            try {
                var method = c.getClass().getDeclaredMethod(name, types);

                return Optional.of(new ComponentMethod(c, method));
            } catch (NoSuchMethodException e) {
                continue;
            }
        }

        return Optional.empty();
    }

    private void removeAllComponents() {
        getComponents().forEach(this::removeComponent);

        components.clear();
    }

    @SuppressWarnings("unchecked")
    private void injectFields(Component component) {
        ComponentHelper.setEntity(component, this);

        if (!component.isComponentInjectionRequired())
            return;

        findFieldsByTypeRecursive(component, Component.class).forEach(field -> {
            getComponentOptional((Class<? extends Component>) field.getType()).ifPresent(comp -> {
                inject(field, component, comp);
            });
        });
    }

    private void removeComponent(Component component) {
        notifyComponentRemoved(component);

        component.onRemoved();

        ComponentHelper.setEntity(component, null);
    }

    private <T extends Component> void notifyComponentAdded(T c) {
        componentListeners.forEach(l -> l.onAdded(c));
    }

    private <T extends Component> void notifyComponentRemoved(T c) {
        componentListeners.forEach(l -> l.onRemoved(c));
    }

    private boolean isCoreComponent(Class<? extends Component> type) {
        return type.getAnnotation(CoreComponent.class) != null;
    }

    private boolean checkRequirementsMet(Class<? extends Component> type) {
        // check if not duplicate;
        if (hasComponent(type)) {
            log.warning("Entity already has component: " + type.getCanonicalName());
            return false;
        }
        for (Required r : type.getAnnotationsByType(Required.class)) {
            if (!hasComponent(r.value())) {
                throw new IllegalStateException("Required component: [" + r.value().getSimpleName() + "] for: " + type.getSimpleName() + " is missing");
            }
        }
        return true;
    }

    private void checkNotRequiredByAny(Class<? extends Component> type) {
        for (Class<? extends Component> t : components.types()) {
            checkNotRequiredBy(t, type);
        }
    }

    /**
     * Fails with IAE if [requiringType] has a dependency on [type].
     */
    private void checkNotRequiredBy(Class<? extends Component> requiringType, Class<? extends Component> type) {
        for (Required required : requiringType.getAnnotationsByType(Required.class)) {
            if (required.value().equals(type)) {
                throw new IllegalArgumentException("Required component: [" + required.value().getSimpleName() + "] by: " + requiringType.getSimpleName());
            }
        }
    }

    // CONVENIENCE COMPONENT ACCESS

    public final TypeComponent getTypeComponent() {
        return type;
    }

    public final TransformComponent getTransformComponent() {
        return transform;
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

    // TRANSFORM BEGIN

    /**
     * @return top left point of this entity in world coordinates
     */
    public final Point2D getPosition() {
        return transform.getPosition();
    }

    public final Point3D getPosition3D() {
        return transform.getPosition3D();
    }

    /**
     * Set top left position of this entity in world coordinates.
     */
    public final void setPosition(Point2D position) {
        transform.setPosition(position);
    }

    /**
     * Set top left position of this entity in world coordinates.
     */
    public final void setPosition(Vec2 position) {
        transform.setPosition(position.x, position.y);
    }

    public final void setPosition3D(Point3D position) {
        transform.setPosition3D(position);
    }

    /**
     * Set top left position of this entity in world coordinates.
     */
    public final void setPosition(double x, double y) {
        transform.setPosition(x, y);
    }

    public final void setPosition3D(double x, double y, double z) {
        transform.setPosition3D(x, y, z);
    }

    /**
     * @return top left x
     */
    public final double getX() {
        return transform.getX();
    }

    /**
     * @return top left y
     */
    public final double getY() {
        return transform.getY();
    }

    public final double getZ() {
        return transform.getZ();
    }

    /**
     * Set position top left x of this entity.
     */
    public final void setX(double x) {
        transform.setX(x);
    }

    /**
     * Set position top left y of this entity.
     */
    public final void setY(double y) {
        transform.setY(y);
    }

    public final void setZ(double z) {
        transform.setZ(z);
    }

    @Override
    public final DoubleProperty xProperty() {
        return transform.xProperty();
    }

    @Override
    public final DoubleProperty yProperty() {
        return transform.yProperty();
    }

    @Override
    public final DoubleProperty zProperty() {
        return transform.zProperty();
    }

    public final Point2D getLocalAnchor() {
        return transform.getLocalAnchor();
    }

    public final void setLocalAnchorFromCenter() {
        setLocalAnchor(bbox.getCenterLocal());
    }

    public final void setLocalAnchor(Point2D localAnchor) {
        transform.setLocalAnchor(localAnchor);
    }

    public final void setAnchoredPosition(double x, double y) {
        transform.setAnchoredPosition(new Point2D(x, y));
    }

    public final void setAnchoredPosition(Point2D p) {
        transform.setAnchoredPosition(p);
    }

    public final void setAnchoredPosition(double x, double y, Point2D localAnchor) {
        setPosition(x - localAnchor.getX(), y - localAnchor.getY());
    }

    /**
     * @return world coordinates of the point that corresponds to local anchor
     */
    public final Point2D getAnchoredPosition() {
        return transform.getAnchoredPosition();
    }

    /**
     * @param localAnchor in local coordinates
     * @return world coordinates of the point that corresponds to local anchor
     */
    public final Point2D getAnchoredPosition(Point2D localAnchor) {
        return new Point2D(getX() + localAnchor.getX(), getY() + localAnchor.getY());
    }

    /**
     * Translate x and y by given vector.
     */
    public final void translate(Point2D vector) {
        transform.translate(vector);
    }

    /**
     * Translate x and y by given vector.
     */
    public final void translate(Vec2 vector) {
        transform.translate(vector.x, vector.y);
    }

    /**
     * Translate x and y by given vector.
     *
     * @param dx vector x
     * @param dy vector y
     */
    public final void translate(double dx, double dy) {
        transform.translate(dx, dy);
    }

    /**
     * Translate x, y and z by given vector.
     */
    public final void translate3D(Point3D vector) {
        transform.translate3D(vector);
    }

    /**
     * Translate x, y and z by given dx, dy and dz respectively.
     */
    public final void translate3D(double dx, double dy, double dz) {
        transform.translate3D(dx, dy, dz);
    }

    /**
     * Translate x by given value.
     */
    public final void translateX(double dx) {
        transform.translateX(dx);
    }

    /**
     * Translate y by given value.
     */
    public final void translateY(double dy) {
        transform.translateY(dy);
    }

    /**
     * Translate z by given value.
     */
    public final void translateZ(double dz) {
        transform.translateZ(dz);
    }

    /**
     * Instantly moves this entity distance units towards given point.
     *
     * @param point the point to move towards
     * @param distance the distance to move
     */
    public final void translateTowards(Point2D point, double distance) {
        transform.translateTowards(point, distance);
    }

    /**
     * @return distance in pixels from this entity's transform (top-left point) to the other's transform (top-left point)
     */
    public final double distance(Entity other) {
        return transform.distance(other.transform);
    }

    /**
     * @return distance in pixels from this entity to the other using their bounding boxes
     */
    public final double distanceBBox(Entity other) {
        var rect1 = bbox.range(0, 0);
        var rect2 = other.bbox.range(0, 0);

        return FXGLMath.distance(rect1, rect2);
    }

    /**
     * @return rotation angle
     */
    public final double getRotation() {
        return transform.getAngle();
    }

    /**
     * Set absolute rotation angle.
     */
    public final void setRotation(double angle) {
        transform.setAngle(angle);
    }

    public final DoubleProperty angleProperty() {
        return transform.angleProperty();
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
        transform.rotateBy(angle);
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
        transform.rotateToVector(vector);
    }

    /**
     * Sets scale X, Y and Z to given scale value.
     */
    public final void setScaleUniform(double scale) {
        setScaleX(scale);
        setScaleY(scale);
        setScaleZ(scale);
    }

    public final void setScaleX(double scaleX) {
        transform.setScaleX(scaleX);
    }

    public final void setScaleY(double scaleY) {
        transform.setScaleY(scaleY);
    }

    public final void setScaleZ(double scaleZ) {
        transform.setScaleZ(scaleZ);
    }

    public final double getScaleX() {
        return transform.getScaleX();
    }

    public final double getScaleY() {
        return transform.getScaleY();
    }

    public final double getScaleZ() {
        return transform.getScaleZ();
    }

    // TRANSFORM END

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

    /**
     * @param opacity a value in range [0..1], where 0 is fully transparent and 1 is fully opaque
     */
    public final void setOpacity(double opacity) {
        view.setOpacity(opacity);
    }

    public final double getOpacity() {
        return view.getOpacity();
    }

    /**
     * Setting invisible allows to prevent the entity from receiving mouse events.
     */
    public final void setVisible(boolean isVisible) {
        view.setVisible(isVisible);
    }

    public final boolean isVisible() {
        return view.isVisible();
    }

    public final void setZIndex(int z) {
        view.setZIndex(z);
    }

    public final int getZIndex() {
        return view.getZIndex();
    }

    // VIEW END

    // Animatable overrides BEGIN

    @Override
    public DoubleProperty scaleXProperty() {
        return transform.scaleXProperty();
    }

    @Override
    public DoubleProperty scaleYProperty() {
        return transform.scaleYProperty();
    }

    @Override
    public DoubleProperty scaleZProperty() {
        return transform.scaleZProperty();
    }

    @Override
    public DoubleProperty rotationXProperty() {
        return transform.rotationXProperty();
    }

    @Override
    public DoubleProperty rotationYProperty() {
        return transform.rotationYProperty();
    }

    @Override
    public DoubleProperty rotationZProperty() {
        return transform.rotationZProperty();
    }

    @Override
    public DoubleProperty opacityProperty() {
        return view.opacityProperty();
    }

    public void setScaleOrigin(Point2D pivotPoint) {
        transform.setScaleOrigin(pivotPoint);
    }

    public void setRotationOrigin(Point2D pivotPoint) {
        transform.setRotationOrigin(pivotPoint);
    }

    @Override
    public void setScaleOrigin(Point3D pivotPoint) {
        transform.setScaleOrigin3D(pivotPoint);
    }

    @Override
    public void setRotationOrigin(Point3D pivotPoint) {
        transform.setRotationOrigin3D(pivotPoint);
    }

    // Animatable overrides END


    @Override
    public Entity copy() {
        return EntityHelper.INSTANCE.copy(this);
    }

    @Override
    public String toString() {
        List<Component> comps = getComponents();

        // we want core components to be shown first for readability
        List<String> coreComponentsAsString = new ArrayList<>(comps.size());
        List<String> otherComponentsAsString = new ArrayList<>(comps.size());

        comps.forEach(c -> {
            if (isCoreComponent(c.getClass())) {
                coreComponentsAsString.add(c.toString());
            } else {
                otherComponentsAsString.add(c.toString());
            }
        });

        Collections.sort(coreComponentsAsString);
        Collections.sort(otherComponentsAsString);

        return "Entity(" + coreComponentsAsString + otherComponentsAsString + ")";
    }
}
