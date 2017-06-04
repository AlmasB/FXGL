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

import com.almasb.fxgl.core.collection.ObjectMap
import com.almasb.fxgl.core.logging.FXGLLogger
import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.ecs.component.Required
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class Controls(private val parent: Entity) {

    companion object {
        private val log = FXGLLogger.get(Controls::class.java)
    }

    val controls = ObjectMap<Class<out Control>, Control>()

    fun hasControl(type: Class<out Control>): Boolean {
        return controls.containsKey(type)
    }

    fun <T : Control> getControl(type: Class<T>): Optional<T> {
        return Optional.ofNullable<T>(getControlUnsafe<T>(type))
    }

    fun <T : Control> getControlUnsafe(type: Class<out T>): T? {
        return type.cast(controls.get(type))
    }

    fun addControl(control: Control) {
        val type = control.javaClass

        if (type.canonicalName == null) {
            log.fatal("Adding anonymous control: " + type.name)
            throw IllegalArgumentException("Anonymous controls are not allowed! - " + type.name)
        }

        if (hasControl(type)) {
            log.fatal("Entity already has a control with type: " + type.canonicalName)
            throw IllegalArgumentException("Entity already has a control with type: " + type.canonicalName)
        }

        checkRequirementsMet(control.javaClass)

        controls.put(control.javaClass, control)

        if (control is AbstractControl) {
            control.setEntity(parent)
        }

        injectFields(control)

        control.onAdded(parent)
        notifyControlAdded(control)
    }

    private fun injectFields(control: Control) {
        ReflectionUtils.findFieldsByType(control, Component::class.java).forEach { field ->
            val comp = parent.getComponentUnsafe<Component>(field.type as Class<Component>)
            if (comp != null) {
                ReflectionUtils.inject(field, control, comp)
            } else {
                log.warning("Injection failed, entity has no component: " + field.type)
            }
        }

        ReflectionUtils.findFieldsByType(control, Control::class.java).forEach { field ->
            val ctrl = getControlUnsafe<Control>(field.type as Class<Control>)
            if (ctrl != null) {
                ReflectionUtils.inject(field, control, ctrl)
            } else {
                log.warning("Injection failed, entity has no control: " + field.type)
            }
        }
    }

    /**
     * @param type the control type to remove
     */
    fun removeControl(type: Class<out Control>) {
        val control = getControlUnsafe<Control>(type)

        if (control == null) {
            log.warning("Cannot remove control " + type.simpleName + ". Entity does not have one")
        } else {
            controls.remove(control.javaClass)
            removeControlImpl(control)
        }
    }

    /**
     * Remove all controls from entity.
     */
    fun removeAllControls() {
        for (control in controls.values()) {
            removeControlImpl(control)
        }

        controls.clear()
    }

    private fun removeControlImpl(control: Control) {
        notifyControlRemoved(control)
        control.onRemoved(parent)

        if (control is AbstractControl) {
            control.entity = null
        }
    }

    private val controlListeners = ArrayList<ControlListener>()

    /**
     * @param listener the listener to add
     */
    fun addControlListener(listener: ControlListener) {
        controlListeners.add(listener)
    }

    /**
     * @param listener the listener to remove
     */
    fun removeControlListener(listener: ControlListener) {
        controlListeners.remove(listener)
    }

    private fun notifyControlAdded(control: Control) {
        for (i in controlListeners.indices) {
            controlListeners[i].onControlAdded(control)
        }
    }

    private fun notifyControlRemoved(control: Control) {
        for (i in controlListeners.indices) {
            controlListeners[i].onControlRemoved(control)
        }
    }

    private fun checkRequirementsMet(type: Class<*>) {
        val required = type.getAnnotationsByType(Required::class.java)

        for (r in required) {
            if (!parent.hasComponent(r.value.java)) {
                throw IllegalStateException("Required component: [" + r.value.java.getSimpleName() + "] for: " + type.simpleName + " is missing")
            }
        }
    }

    /**
     * Checks if given type is not required by any other type.

     * @param type the type to check
     * *
     * @throws IllegalArgumentException if the type is required by any other type
     */
    private fun checkNotRequiredByAny(type: Class<out Component>) {
        // check for components
        for (t in parent.components.keys()) {

            for (required in t.getAnnotationsByType(Required::class.java)) {
                if (required.value == type) {
                    throw IllegalArgumentException("Required component: [" + required.value.java.getSimpleName() + "] by: " + t.getSimpleName())
                }
            }
        }

        // check for controls
        for (t in controls.keys()) {

            for (required in t.getAnnotationsByType(Required::class.java)) {
                if (required.value == type) {
                    throw IllegalArgumentException("Required component: [" + required.value.java.getSimpleName() + "] by: " + t.simpleName)
                }
            }
        }
    }
}