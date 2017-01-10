/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.ecs;

import com.almasb.easyio.serialization.Bundle;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.ecs.serialization.SerializableComponent;
import com.almasb.fxgl.ecs.serialization.SerializableControl;
import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.ObjectMap;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A generic entity in the Entity-Component-System model.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Entity {

    private static final Logger log = LogManager.getLogger(Entity.class);

    private final ObjectMap<String, Object> properties = new ObjectMap<>();

    /**
     * Set a property specified by a key-value pair.
     * Note: only exists for convenience and for short lived entities.
     * Prefer {@link #addComponent(Component)} instead.
     *
     * @param key property key
     * @param value property value
     */
    public final void setProperty(String key, Object value) {
        checkValid();

        properties.put(key, value);
    }

    /**
     * Retrieve a property value by a given key.
     * Note: only exists for convenience and for short lived entities.
     * Prefer {@link #getComponent(Class)} instead.
     *
     * @param key property key
     * @param <T> value type
     * @return property value
     * @throws IllegalArgumentException if key doesn't exist
     */
    @SuppressWarnings("unchecked")
    public final <T> T getProperty(String key) {
        checkValid();

        Object value = properties.get(key, null);
        if (value == null)
            throw new IllegalArgumentException("No property with key: " + key);

        return (T) value;
    }

    ObjectMap<Class<? extends Control>, Control> controls = new ObjectMap<>();

    /**
     * @param type control type
     * @return true iff entity has control of given type
     */
    public final boolean hasControl(Class<? extends Control> type) {
        checkValid();

        return controls.containsKey(type);
    }

    /**
     * Returns control of given type or {@link Optional#empty()} if
     * no such type is registered on this entity.
     * Warning: object allocation.
     *
     * @param type control type
     * @return control
     */
    public final <T extends Control> Optional<T> getControl(Class<T> type) {
        checkValid();

        return Optional.ofNullable(getControlUnsafe(type));
    }

    /**
     * Returns control of given type.
     * Unlike {@link #getControl(Class)} there is no
     * check for control existence and return is not wrapped with Optional.
     * Use this only if you are certain the entity has this type of control
     * or to avoid object allocation.
     *
     * @param type control type
     * @return control
     */
    public final <T extends Control> T getControlUnsafe(Class<T> type) {
        checkValid();

        return type.cast(controls.get(type));
    }

    /**
     * Warning: object allocation.
     * Cannot be called during update.
     *
     * @return array of controls
     */
    public final Array<Control> getControls() {
        checkValid();

        return controls.values().toArray();
    }

    /**
     * Adds behavior to entity.
     * Only 1 control per type is allowed.
     * Anonymous controls are not allowed.
     * Avoid adding controls within update() of another control.
     *
     * @param control the behavior
     * @throws IllegalArgumentException if control with same type already registered or anonymous
     * @throws IllegalStateException if components required by the given control are missing
     */
    public final void addControl(Control control) {
        checkValid();

        Class<? extends Control> type = control.getClass();

        if (type.getCanonicalName() == null) {
            log.fatal("Adding anonymous control: " + type.getName());
            throw new IllegalArgumentException("Anonymous controls are not allowed! - " + type.getName());
        }

        if (hasControl(type)) {
            log.fatal("Entity already has a control with type: " + type.getCanonicalName());
            throw new IllegalArgumentException("Entity already has a control with type: " + type.getCanonicalName());
        }

        checkRequirementsMet(control.getClass());

        controls.put(control.getClass(), control);

        if (control instanceof AbstractControl) {
            ((AbstractControl) control).setEntity(this);
        }

        control.onAdded(this);
        notifyControlAdded(control);
    }

    /**
     * Remove behavior from entity of given type.
     *
     * @param type the control type to remove
     */
    public final void removeControl(Class<? extends Control> type) {
        checkValid();

        Control control = getControlUnsafe(type);

        if (control == null) {
            log.warn("Attempted to remove control but entity doesn't have a control with type: "+ type.getSimpleName());
        } else {
            controls.remove(control.getClass());
            removeControlImpl(control);
        }
    }

    /**
     * Remove all behavior controls from entity.
     */
    public final void removeAllControls() {
        checkValid();

        for (Control control : controls.values()) {
            removeControlImpl(control);
        }

        controls.clear();
    }

    private void removeControlImpl(Control control) {
        notifyControlRemoved(control);
        control.onRemoved(this);

        if (control instanceof AbstractControl) {
            ((AbstractControl) control).setEntity(null);
        }
    }

    private List<ControlListener> controlListeners = new ArrayList<>();

    /**
     * Add control listener.
     *
     * @param listener the listener
     */
    public void addControlListener(ControlListener listener) {
        checkValid();

        controlListeners.add(listener);
    }

    /**
     * Remove control listener
     *
     * @param listener the listener
     */
    public void removeControlListener(ControlListener listener) {
        checkValid();

        controlListeners.remove(listener);
    }

    private void notifyControlAdded(Control control) {
        for (int i = 0; i < controlListeners.size(); i++) {
            controlListeners.get(i).onControlAdded(control);
        }
    }

    private void notifyControlRemoved(Control control) {
        for (int i = 0; i < controlListeners.size(); i++) {
            controlListeners.get(i).onControlRemoved(control);
        }
    }

    ObjectMap<Class<? extends Component>, Component> components = new ObjectMap<>();

    /**
     * @param type component type
     * @return true iff entity has a component of given type
     */
    public final boolean hasComponent(Class<? extends Component> type) {
        checkValid();

        return components.containsKey(type);
    }

    /**
     * Returns component of given type if registered. The type must be exactly
     * the same as the type of the instance registered. If component not found, {@link Optional#empty()}
     * is returned.
     *
     * @param type component type
     * @return component
     */
    public final <T extends Component> Optional<T> getComponent(Class<T> type) {
        checkValid();

        return Optional.ofNullable(getComponentUnsafe(type));
    }

    /**
     * Returns component of given type. Unlike {@link #getComponent(Class)} there is no
     * checking if the component exists and so bare object is returned, i.e. can be null.
     * Use this only if you are certain that entity has this type of component.
     *
     * @param type component type
     * @return component
     */
    public final <T extends Component> T getComponentUnsafe(Class<T> type) {
        checkValid();

        return type.cast(components.get(type));
    }

    /**
     * Warning: object allocation.
     * Cannot be called during update.
     *
     * @return array of components
     */
    public final Array<Component> getComponents() {
        checkValid();

        return components.values().toArray();
    }

    /**
     * Adds given component to this entity.
     * Only 1 component with the same type can be registered.
     * Anonymous components are NOT allowed.
     *
     * @param component the component
     * @throws IllegalArgumentException if a component with same type already registered or anonymous
     * @throws IllegalStateException if components required by the given component are missing
     */
    public final void addComponent(Component component) {
        checkValid();

        Class<? extends Component> type = component.getClass();
        if (type.getCanonicalName() == null) {
            throw new IllegalArgumentException("Anonymous components are not allowed! - " + type.getName());
        }

        if (hasComponent(type)) {
            throw new IllegalArgumentException("Entity already has a component with type: " + type.getCanonicalName());
        }

        if (component instanceof AbstractComponent) {
            AbstractComponent c = (AbstractComponent) component;
            c.setEntity(this);
        }

        checkRequirementsMet(component.getClass());

        components.put(component.getClass(), component);
        component.onAdded(this);

        if (isActive())
            world.onComponentAdded(component, this);

        notifyComponentAdded(component);
    }

    /**
     * Remove a component with given type from this entity.
     *
     * @param type type of the component to remove
     * @throws IllegalArgumentException if the component is required by other components / controls
     */
    public final void removeComponent(Class<? extends Component> type) {
        checkValid();

        Component component = getComponentUnsafe(type);

        if (component == null) {
            log.warn("Attempted to remove component but entity doesn't have a component with type: "+ type.getSimpleName());
        } else {

            // if not cleaning, then entity is alive, whether active or not
            // hence we cannot allow removal if component is required by other components / controls
            if (!cleaning) {
                checkNotRequiredByAny(type);
            }

            components.remove(component.getClass());

            if (isActive())
                world.onComponentRemoved(component, this);

            removeComponentImpl(component);
        }
    }

    /**
     * Removes all components from this entity.
     */
    public final void removeAllComponents() {
        checkValid();

        for (Component component : components.values()) {
            removeComponentImpl(component);
        }

        components.clear();
    }

    private void removeComponentImpl(Component component) {
        notifyComponentRemoved(component);
        component.onRemoved(this);

        if (component instanceof AbstractComponent) {
            AbstractComponent c = (AbstractComponent) component;
            c.setEntity(null);
        }
    }

    private List<ComponentListener> componentListeners = new ArrayList<>();

    /**
     * Register a component listener on this entity.
     *
     * @param listener the listener
     */
    public void addComponentListener(ComponentListener listener) {
        componentListeners.add(listener);
    }

    /**
     * Removed a component listener.
     *
     * @param listener the listener
     */
    public void removeComponentListener(ComponentListener listener) {
        componentListeners.remove(listener);
    }

    private void notifyComponentAdded(Component component) {
        for (int i = 0; i < componentListeners.size(); i++) {
            componentListeners.get(i).onComponentAdded(component);
        }
    }

    private void notifyComponentRemoved(Component component) {
        for (int i = 0; i < componentListeners.size(); i++) {
            componentListeners.get(i).onComponentRemoved(component);
        }
    }

    /**
     * Checks if requirements for given type are met.
     *
     * @param type the type whose requirements to check
     * @throws IllegalStateException if the type requirements are not met
     */
    private void checkRequirementsMet(Class<?> type) {
        Required[] required = type.getAnnotationsByType(Required.class);

        for (Required r : required) {
            if (!hasComponent(r.value())) {
                throw new IllegalStateException("Required component: [" + r.value().getSimpleName() + "] for: " + type.getSimpleName() + " is missing");
            }
        }
    }

    /**
     * Checks if given type is not required by any other type.
     *
     * @param type the type to check
     * @throws IllegalArgumentException if the type is required by any other type
     */
    private void checkNotRequiredByAny(Class<? extends Component> type) {
        // check for components
        for (Class<?> t : components.keys()) {

            for (Required required : t.getAnnotationsByType(Required.class)) {
                if (required.value().equals(type)) {
                    throw new IllegalArgumentException("Required component: [" + required.value().getSimpleName() + "] by: " + t.getSimpleName());
                }
            }
        }

        // check for controls
        for (Class<?> t : controls.keys()) {

            for (Required required : t.getAnnotationsByType(Required.class)) {
                if (required.value().equals(type)) {
                    throw new IllegalArgumentException("Required component: [" + required.value().getSimpleName() + "] by: " + t.getSimpleName());
                }
            }
        }
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

    private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper(false);

    /**
     * @return active property of this entity
     */
    public final ReadOnlyBooleanProperty activeProperty() {
        return active.getReadOnlyProperty();
    }

    /**
     * Entity is "active" from the moment it is registered in the world
     * and until it is removed from the world.
     *
     * @return true if entity is active, else false
     */
    public final boolean isActive() {
        return active.get();
    }

    private Runnable onActive = null;

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

    private Runnable onNotActive = null;

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

    private EntityWorld world;

    /**
     * @return the world that entity is attached to
     */
    public EntityWorld getWorld() {
        return world;
    }

    /**
     * Initializes this entity.
     *
     * @param world the world to which entity is being attached
     */
    void init(EntityWorld world) {
        this.world = world;
        if (onActive != null)
            onActive.run();
        active.set(true);
    }

    /**
     * Update tick for this entity.
     *
     * @param tpf time per frame
     */
    void update(double tpf) {

        if (controlsEnabled) {
            for (Control c : controls.values()) {
                if (!c.isPaused()) {
                    c.onUpdate(this, tpf);
                }
            }
        }
    }

    private boolean cleaning = false;

    /**
     * Cleans entity.
     * Removes all controls and components.
     * After this the entity cannot be used.
     */
    void clean() {
        cleaning = true;
        if (onNotActive != null)
            onNotActive.run();
        active.set(false);

        removeAllControls();
        removeAllComponents();

        controlListeners.clear();
        componentListeners.clear();

        properties.clear();

        controlsEnabled = true;
        world = null;
        onActive = null;
        onNotActive = null;
    }

    private void checkValid() {
        if (cleaning && world == null)
            throw new IllegalStateException("Attempted access a cleaned entity!");
    }

    /**
     * Remove entity from world.
     */
    public final void removeFromWorld() {
        checkValid();

        world.removeEntity(this);
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
        checkValid();

        Entity copy = new Entity();

        for (Component component : components.values()) {
            if (component instanceof CopyableComponent) {
                copy.addComponent(((CopyableComponent) component).copy());
            }
        }

        for (Control control : controls.values()) {
            if (control instanceof CopyableControl) {
                copy.addControl(((CopyableControl) control).copy());
            }
        }

        return copy;
    }

    /**
     * Save entity state into bundle.
     * Only serializable components and controls will be written.
     *
     * @param bundle the bundle to write to
     */
    public void save(Bundle bundle) {
        checkValid();

        Bundle componentsBundle = new Bundle("components");

        for (Component component : components.values()) {
            if (component instanceof SerializableComponent) {
                Bundle b = new Bundle(component.getClass().getCanonicalName());
                ((SerializableComponent) component).write(b);

                componentsBundle.put(b.getName(), b);
            }
        }


        Bundle controlsBundle = new Bundle("controls");

        for (Control control : controls.values()) {
            if (control instanceof SerializableControl) {
                Bundle b = new Bundle(control.getClass().getCanonicalName());
                ((SerializableControl) control).write(b);

                controlsBundle.put(b.getName(), b);
            }
        }

        bundle.put("components", componentsBundle);
        bundle.put("controls", controlsBundle);
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
        checkValid();

        Bundle componentsBundle = bundle.get("components");

        for (Component component : components.values()) {
            if (component instanceof SerializableComponent) {

                Bundle b = componentsBundle.get(component.getClass().getCanonicalName());
                if (b != null)
                    ((SerializableComponent) component).read(b);
                else
                    log.warn("Bundle " + componentsBundle + " does not have SerializableComponent: " + component);
            }
        }

        Bundle controlsBundle = bundle.get("controls");

        for (Control control : controls.values()) {
            if (control instanceof SerializableControl) {

                Bundle b = controlsBundle.get(control.getClass().getCanonicalName());
                if (b != null)
                    ((SerializableControl) control).read(b);
                else
                    log.warn("Bundle " + componentsBundle + " does not have SerializableControl: " + control);
            }
        }
    }

    @Override
    public String toString() {
        return "Entity("
                + String.join("\n", "components=" + components.values(), "controls=" + controls.values())
                + ")";
    }
}
