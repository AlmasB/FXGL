/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs

import com.almasb.fxgl.core.collection.Array
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

    fun get(): Array<Control> {
        return controls.values().toArray()
    }

    fun getRaw() = controls.values()

    fun types() = controls.keys()

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

    fun clean() {
        removeAllControls()
        controlListeners.clear()
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

    override fun toString() = controls.toString()
}