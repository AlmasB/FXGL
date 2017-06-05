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

import com.almasb.fxgl.core.logging.FXGLLogger
import com.almasb.fxgl.ecs.serialization.SerializableComponent
import com.almasb.fxgl.ecs.serialization.SerializableControl
import com.almasb.fxgl.io.serialization.Bundle

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal object EntitySerializer {

    private val log = FXGLLogger.get(javaClass)

    /**
     * Save entity state into bundle.
     * Only serializable components and controls will be written.
     *
     * @param bundle the bundle to write to
     */
    fun save(entity: Entity, bundle: Bundle) {
        val componentsBundle = Bundle("components")

        entity.components.values()
                .filterIsInstance<SerializableComponent>()
                .forEach {
                    val b = Bundle(it.javaClass.canonicalName)
                    it.write(b)

                    componentsBundle.put(b.name, b)
                }


        val controlsBundle = Bundle("controls")

        entity.controls
                .filterIsInstance<SerializableControl>()
                .forEach {
                    val b = Bundle(it.javaClass.canonicalName)
                    it.write(b)

                    controlsBundle.put(b.name, b)
                }


        bundle.put("components", componentsBundle)
        bundle.put("controls", controlsBundle)
    }

    /**
     * Load entity state from a bundle.
     * Only serializable components and controls will be read.
     * If an entity has a serializable type that is not present in the bundle,
     * a warning will be logged but no exception thrown.
     *
     * @param bundle bundle to read from
     */
    fun load(entity: Entity, bundle: Bundle) {
        val componentsBundle = bundle.get<Bundle>("components")

        for (component in entity.components.values()) {
            if (component is SerializableComponent) {

                val b = componentsBundle.get<Bundle>(component.javaClass.canonicalName)
                if (b != null)
                    component.read(b)
                else
                    log.warning("Bundle $componentsBundle does not have SerializableComponent: $component")
            }
        }

        val controlsBundle = bundle.get<Bundle>("controls")

        for (control in entity.controls) {
            if (control is SerializableControl) {

                val b = controlsBundle.get<Bundle>(control.javaClass.canonicalName)
                if (b != null)
                    control.read(b)
                else
                    log.warning("Bundle $componentsBundle does not have SerializableControl: $control")
            }
        }
    }
}