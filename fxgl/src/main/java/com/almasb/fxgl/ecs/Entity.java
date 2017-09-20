/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.core.reflect.ReflectionUtils;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.io.serialization.Bundle;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A generic entity in the Entity-Component-System (Control) model.
 * During update (or control update) it is not allowed to:
 * <ul>
 *     <li>Add control</li>
 *     <li>Remove control</li>
 * </ul>
 *
 * The best practice is to add all controls an entity will use before attaching
 * the entity to the world and pause the (immediately) unused controls.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Entity {

    private static final Logger log = Logger.get(Entity.class);

    private ObjectMap<String, Object> properties = new ObjectMap<>();

    private ObjectMap<Class<? extends Control>, Control> controls = new ObjectMap<>();
    private ObjectMap<Class<? extends Component>, Component> components = new ObjectMap<>();

    private List<ModuleListener> moduleListeners = new ArrayList<>();

    private GameWorld world = null;

    private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper(false);

    private boolean cleaning = false;
    private boolean controlsEnabled = true;

    private Runnable onActive = null;
    private Runnable onNotActive = null;

    private boolean updating = false;

    /**
     * @return the world this entity is attached to
     */
    public GameWorld getWorld() {
        return world;
    }

    /**
     * Initializes this entity.
     *
     * @param world the world to which entity is being attached
     */
    void init(GameWorld world) {
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
        cleaning = true;

        removeAllControls();
        removeAllComponents();

        properties.clear();

        moduleListeners.clear();

        world = null;
        onActive = null;
        onNotActive = null;

        controlsEnabled = true;
        updating = false;

        active.set(false);

        cleaning = false;
    }

    /**
     * Equivalent to world?.removeEntity(this);
     */
    public final void removeFromWorld() {
        if (world != null)
            world.removeEntity(this);
    }

    /**
     * @return active property of this entity
     */
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
     * The callback will be executed immediately if entity is already in the world.
     *
     * @param action callback
     */
    public final void setOnActive(Runnable action) {
        if (isActive()) {
            action.run();
            return;
        }

        onActive = action;
    }

    /**
     * Set a callback for when entity is removed from world.
     * The callback will be executed immediately if entity is already removed from the world.
     *
     * @param action callback
     */
    public final void setOnNotActive(Runnable action) {
        if (!isActive()) {
            action.run();
            return;
        }

        onNotActive = action;
    }

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
            for (Control c : controls.values()) {
                if (!c.isPaused()) {
                    c.onUpdate(this, tpf);
                }
            }
        }

        updating = false;
    }

    private static final Object NULL = new Object();

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
    public final <T> Optional<T> getPropertyOptional(String key) {
        Object value = properties.get(key, null);
        return Optional.ofNullable((T) value);
    }

    /**
     * @param type control type
     * @return true iff entity has control of given type
     */
    public final boolean hasControl(Class<? extends Control> type) {
        return controls.containsKey(type);
    }

    /**
     * Returns control of given type or {@link Optional#empty()} if
     * no such type is registered on this entity.
     *
     * @param type control type
     * @return control
     */
    public final <T extends Control> Optional<T> getControlOptional(Class<T> type) {
        return Optional.ofNullable(getControl(type));
    }

    /**
     * Returns control of given type or null if no such type is registered.
     *
     * @param type control type
     * @return control
     */
    public final <T extends Control> T getControl(Class<T> type) {
        return type.cast(controls.get(type));
    }

    /**
     * Warning: object allocation.
     * Cannot be called during update.
     *
     * @return array of controls
     */
    public final Array<Control> getControls() {
        return controls.values().toArray();
    }

    /**
     * Adds behavior to entity.
     *
     * @param control the behavior
     * @throws IllegalArgumentException if control with same type already registered or anonymous
     * @throws IllegalStateException if components required by the given control are missing or if within update of another control
     */
    public final void addControl(Control control) {
        checkNotUpdating("Add control");

        addModule(control);

        controls.put(control.getClass(), control);
    }

    /**
     * @param type the control type to remove
     * @return true if removed, false if not found
     * @throws IllegalStateException if within update of another control
     */
    public final boolean removeControl(Class<? extends Control> type) {
        checkNotUpdating("Remove control");

        if (!hasControl(type))
            return false;

        removeModule(getControl(type));

        controls.remove(type);

        return true;
    }

    private void removeAllControls() {
        checkNotUpdating("Remove all controls");

        for (Control control : controls.values()) {
            removeModule(control);
        }

        controls.clear();
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
        return Optional.ofNullable(getComponent(type));
    }

    /**
     * Returns component of given type, or null if type not registered.
     *
     * @param type component type
     * @return component
     */
    public final <T extends Component> T getComponent(Class<T> type) {
        return type.cast(components.get(type));
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
        addModule(component);

        components.put(component.getClass(), component);
    }

    /**
     * Remove a component with given type from this entity.
     *
     * @param type type of the component to remove
     * @throws IllegalArgumentException if the component is required by other components / controls
     * @return true if removed, false if not found
     */
    public final boolean removeComponent(Class<? extends Component> type) {
        if (!hasComponent(type))
            return false;

        // if not cleaning, then entity is alive, whether active or not
        // hence we cannot allow removal if component is required by other components / controls
        if (!cleaning) {
            checkNotRequiredByAny(type);
        }

        removeModule(getComponent(type));

        components.remove(type);

        return true;
    }

    private void removeAllComponents() {
        for (Component comp : components.values()) {
            if (!cleaning) {
                checkNotRequiredByAny(comp.getClass());
            }

            removeModule(comp);
        }

        components.clear();
    }

    public void addModuleListener(ModuleListener listener) {
        moduleListeners.add(listener);
    }

    public void removeModuleListener(ModuleListener listener) {
        moduleListeners.remove(listener);
    }

    private void addModule(Module module) {
        checkRequirementsMet(module.getClass());

        module.setEntity(this);

        if (module instanceof Control)
            injectFields((Control) module);
        else if (module instanceof Component)
            injectFields((Component) module);

        module.onAdded(this);
        notifyModuleAdded(module);
    }

    @SuppressWarnings("unchecked")
    private void injectFields(Component component) {
        ReflectionUtils.findFieldsByTypeRecursive(component, Component.class).forEach(field -> {
            Component comp = getComponent((Class<? extends Component>) field.getType());
            if (comp != null) {
                ReflectionUtils.inject(field, component, comp);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void injectFields(Control control) {
        ReflectionUtils.findFieldsByTypeRecursive(control, Component.class).forEach(field -> {
            Component comp = getComponent((Class<? extends Component>) field.getType());
            if (comp != null) {
                ReflectionUtils.inject(field, control, comp);
            }
        });

        ReflectionUtils.findFieldsByTypeRecursive(control, Control.class).forEach(field -> {
            Control ctrl = getControl((Class<? extends Control>) field.getType());
            if (ctrl != null) {
                ReflectionUtils.inject(field, control, ctrl);
            }
        });
    }

    private void removeModule(Module module) {
        notifyModuleRemoved(module);

        module.onRemoved(this);
        module.setEntity(null);
    }

    private <T extends Module> void notifyModuleAdded(T module) {
        if (module instanceof Component) {
            Component c = (Component) module;
            for (int i = 0; i < moduleListeners.size(); i++) {
                moduleListeners.get(i).onAdded(c);
            }
        } else {
            Control c = (Control) module;
            for (int i = 0; i < moduleListeners.size(); i++) {
                moduleListeners.get(i).onAdded(c);
            }
        }
    }

    private <T extends Module> void notifyModuleRemoved(T module) {
        if (module instanceof Component) {
            Component c = (Component) module;
            for (int i = 0; i < moduleListeners.size(); i++) {
                moduleListeners.get(i).onRemoved(c);
            }
        } else {
            Control c = (Control) module;
            for (int i = 0; i < moduleListeners.size(); i++) {
                moduleListeners.get(i).onRemoved(c);
            }
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
    private void checkNotDuplicate(Class<?> type) {
        if ((Component.class.isAssignableFrom(type) && hasComponent((Class<? extends Component>) type))
                || (Control.class.isAssignableFrom(type) && hasControl((Class<? extends Control>) type))) {
            throw new IllegalArgumentException("Entity already has type: " + type.getCanonicalName());
        }
    }

    private void checkRequirementsMet(Class<?> type) {
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

        // check controls
        for (Class<?> t : controls.keys()) {
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
        return "Entity("
                + String.join("\n", "components=" + components, "controls=" + controls)
                + ")";
    }
}
