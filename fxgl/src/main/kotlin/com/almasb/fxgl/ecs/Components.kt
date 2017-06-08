/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs

import com.almasb.fxgl.core.collection.Array
import com.almasb.fxgl.core.collection.ObjectMap
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class Components(private val parent: Entity) {

    private val components = ObjectMap<Class<out Component>, Component>()

    fun hasComponent(type: Class<out Component>): Boolean {
        return components.containsKey(type)
    }

    /**
     * Returns component of given type, or [Optional.empty]
     * if type not registered.

     * @param type component type
     * *
     * @return component
     */
    fun <T : Component> getComponent(type: Class<T>): Optional<T> {
        return Optional.ofNullable(getComponentUnsafe(type))
    }

    /**
     * Returns component of given type, or null if type not registered.

     * @param type component type
     * *
     * @return component
     */
    fun <T : Component> getComponentUnsafe(type: Class<out T>): T? {
        return type.cast(components.get(type))
    }

    /**
     * Warning: object allocation.
     * Cannot be called during update.

     * @return array of components
     */
    fun get(): Array<Component> {
        return components.values().toArray()
    }

    fun getRaw() = components.values()

    fun clean() {
        removeAllComponents()
        componentListeners.clear()
    }

    fun types() = components.keys()

    /**
     * Adds given component to this entity.
     * Only 1 component with the same type can be registered.
     * Anonymous components are NOT allowed.

     * @param component the component
     * *
     * @throws IllegalArgumentException if a component with same type already registered or anonymous
     * *
     * @throws IllegalStateException if components required by the given component are missing
     */
    fun addComponent(component: Component) {
        components.put(component.javaClass, component)

        if (component is Component) {
            component.entity = parent
        }

        component.onAdded(parent)

        if (parent.isActive())
            parent.world.onComponentAdded(component, parent)

        notifyComponentAdded(component)
    }

    /**
     * Remove a component with given type from this entity.

     * @param type type of the component to remove
     * *
     * @throws IllegalArgumentException if the component is required by other components / controls
     */
    fun removeComponent(type: Class<out Component>) {
        val component = getComponentUnsafe<Component>(type)!!

        components.remove(component.javaClass)

        if (parent.isActive())
            parent.world.onComponentRemoved(component, parent)

        removeComponentImpl(component)
    }

    fun removeAllComponents() {
        for (component in components.values()) {
            removeComponentImpl(component)
        }

        components.clear()
    }

    private fun removeComponentImpl(component: Component) {
        notifyComponentRemoved(component)
        component.onRemoved(parent)

        if (component is Component) {
            component.entity = null
        }
    }

    private val componentListeners = ArrayList<ComponentListener>()

    /**
     * Register a component listener on this entity.

     * @param listener the listener
     */
    fun addComponentListener(listener: ComponentListener) {
        componentListeners.add(listener)
    }

    /**
     * Removed a component listener.

     * @param listener the listener
     */
    fun removeComponentListener(listener: ComponentListener) {
        componentListeners.remove(listener)
    }

    private fun notifyComponentAdded(component: Component) {
        for (i in componentListeners.indices) {
            componentListeners[i].onComponentAdded(component)
        }
    }

    private fun notifyComponentRemoved(component: Component) {
        for (i in componentListeners.indices) {
            componentListeners[i].onComponentRemoved(component)
        }
    }

    override fun toString() = components.toString()
}