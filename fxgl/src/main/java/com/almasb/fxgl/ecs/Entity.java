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

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.fxgl.core.logging.FXGLLogger;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.io.serialization.Bundle;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

import java.util.Optional;

/**
 * A generic entity in the Entity-Component-System (Control) model.
 * During update (or control update) it is not allowed to:
 * <ul>
 *     <li>Add control</li>
 *     <li>Remove control</li>
 * </ul>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Entity {

    private static final Logger log = FXGLLogger.get(Entity.class);

    private final ObjectMap<String, Object> properties = new ObjectMap<>();

    private Controls controls = new Controls(this);
    private Components components = new Components(this);

    private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper(false);

    /**
     * Set a property specified by a key-value pair.
     * Prefer {@link Component} instead.
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
     * Prefer {@link Component} instead.
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

    /**
     * @param type control type
     * @return true iff entity has control of given type
     */
    public final boolean hasControl(Class<? extends Control> type) {
        checkValid();

        return controls.hasControl(type);
    }

    /**
     * Returns control of given type or {@link Optional#empty()} if
     * no such type is registered on this entity.
     *
     * @param type control type
     * @return control
     */
    public final <T extends Control> Optional<T> getControl(Class<T> type) {
        checkValid();

        return Optional.ofNullable(getControlUnsafe(type));
    }

    /**
     * Returns control of given type or null if no such type is registered.
     *
     * @param type control type
     * @return control
     */
    public final <T extends Control> T getControlUnsafe(Class<T> type) {
        checkValid();

        return controls.getControlUnsafe(type);
    }

    /**
     * Warning: object allocation.
     * Cannot be called during update.
     *
     * @return array of controls
     */
    public final Array<Control> getControls() {
        checkValid();

        return controls.get();
    }

    /**
     * Adds behavior to entity.
     * Only 1 control per type is allowed.
     * Anonymous controls are not allowed.
     * Cannot add controls within update() of another control.
     *
     * @param control the behavior
     * @throws IllegalArgumentException if control with same type already registered or anonymous
     * @throws IllegalStateException if components required by the given control are missing
     */
    public final void addControl(Control control) {
        checkValid();

        controls.addControl(control);
    }

    /**
     * @param type the control type to remove
     */
    public final void removeControl(Class<? extends Control> type) {
        checkValid();

        controls.removeControl(type);
    }

    /**
     * Remove all controls from entity.
     */
    public final void removeAllControls() {
        checkValid();

        controls.removeAllControls();
    }

    /**
     * @param listener the listener to add
     */
    public void addControlListener(ControlListener listener) {
        checkValid();

        controls.addControlListener(listener);
    }

    /**
     * @param listener the listener to remove
     */
    public void removeControlListener(ControlListener listener) {
        checkValid();

        controls.removeControlListener(listener);
    }

    public ObjectMap.Keys<Class<? extends Component> > getComponentTypes() {
        return components.types();
    }

    /**
     * @param type component type
     * @return true iff entity has a component of given type
     */
    public final boolean hasComponent(Class<? extends Component> type) {
        checkValid();

        return components.hasComponent(type);
    }

    /**
     * Returns component of given type, or {@link Optional#empty()}
     * if type not registered.
     *
     * @param type component type
     * @return component
     */
    public final <T extends Component> Optional<T> getComponent(Class<T> type) {
        checkValid();

        return Optional.ofNullable(getComponentUnsafe(type));
    }

    /**
     * Returns component of given type, or null if type not registered.
     *
     * @param type component type
     * @return component
     */
    public final <T extends Component> T getComponentUnsafe(Class<T> type) {
        checkValid();

        return components.getComponentUnsafe(type);
    }

    /**
     * Warning: object allocation.
     * Cannot be called during update.
     *
     * @return array of components
     */
    public final Array<Component> getComponents() {
        checkValid();

        return components.get();
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

        components.addComponent(component);
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
            log.warning("Attempted to remove component but entity doesn't have a component with type: "+ type.getSimpleName());
        } else {

            // if not cleaning, then entity is alive, whether active or not
            // hence we cannot allow removal if component is required by other components / controls
            if (!cleaning) {
                checkNotRequiredByAny(type);
            }

            components.removeComponent(type);
        }
    }

    private void checkNotRequiredByAny(Class<? extends Component> type) {
        // check components
        for (Class<?> t : components.types()) {
            checkNotRequiredBy(t, type);
        }

        // check controls
        for (Class<?> t : controls.types()) {
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
     * Removes all components from this entity.
     */
    public final void removeAllComponents() {
        checkValid();

        components.removeAllComponents();
    }

    /**
     * Register a component listener on this entity.
     *
     * @param listener the listener
     */
    public void addComponentListener(ComponentListener listener) {
        components.addComponentListener(listener);
    }

    /**
     * Removed a component listener.
     *
     * @param listener the listener
     */
    public void removeComponentListener(ComponentListener listener) {
        components.removeComponentListener(listener);
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

    private boolean updating = false;
    private boolean delayedRemove = false;

    /**
     * Update tick for this entity.
     *
     * @param tpf time per frame
     */
    void update(double tpf) {
        updating = true;

        if (controlsEnabled) {
            for (Control c : controls.getRaw()) {
                if (!c.isPaused()) {
                    c.onUpdate(this, tpf);
                }
            }
        }

        updating = false;

        if (delayedRemove)
            removeFromWorld();
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

        controls.clean();
        components.clean();

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

        if (updating) {
            delayedRemove = true;
        } else {
            world.removeEntity(this);
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
        checkValid();

        return EntityCopier.INSTANCE.copy(this);
    }

    /**
     * Save entity state into bundle.
     * Only serializable components and controls will be written.
     *
     * @param bundle the bundle to write to
     */
    public void save(Bundle bundle) {
        checkValid();

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
        checkValid();

        EntitySerializer.INSTANCE.load(this, bundle);
    }

    @Override
    public String toString() {
        return "Entity("
                + String.join("\n", "components=" + components, "controls=" + controls)
                + ")";
    }
}
