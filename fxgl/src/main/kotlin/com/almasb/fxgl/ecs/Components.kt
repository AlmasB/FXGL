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

package com.almasb.fxgl.ecs

import com.almasb.fxgl.core.collection.Array
import com.almasb.fxgl.core.collection.ObjectMap
import com.almasb.fxgl.ecs.component.Required
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
        val type = component.javaClass
        if (type.canonicalName == null) {
            throw IllegalArgumentException("Anonymous components are not allowed! - " + type.name)
        }

        if (hasComponent(type)) {
            throw IllegalArgumentException("Entity already has a component with type: " + type.canonicalName)
        }

        if (component is AbstractComponent) {
            component.setEntity(parent)
        }

        checkRequirementsMet(component.javaClass)

        components.put(component.javaClass, component)
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
        val component = getComponentUnsafe<Component>(type)

        if (component == null) {
            //log.warning("Attempted to remove component but entity doesn't have a component with type: " + type.simpleName)
        } else {

            // if not cleaning, then entity is alive, whether active or not
            // hence we cannot allow removal if component is required by other components / controls
//            if (!cleaning) {
//                controls.checkNotRequiredByAny(type)
//            }

            components.remove(component.javaClass)

            if (parent.isActive())
                parent.world.onComponentRemoved(component, parent)

            removeComponentImpl(component)
        }
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

        if (component is AbstractComponent) {
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

    private fun checkRequirementsMet(type: Class<*>) {
        val required = type.getAnnotationsByType(Required::class.java)

        for (r in required) {
            if (!hasComponent(r.value.java)) {
                throw IllegalStateException("Required component: [" + r.value.java.getSimpleName() + "] for: " + type.simpleName + " is missing")
            }
        }
    }
}