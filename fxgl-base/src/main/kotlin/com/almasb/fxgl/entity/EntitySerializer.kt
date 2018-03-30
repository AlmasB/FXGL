/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.entity.component.SerializableComponent
import com.almasb.fxgl.io.serialization.Bundle

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal object EntitySerializer {

    private val log = Logger.get(javaClass)

    /**
     * Save entity state into bundle.
     * Only serializable components and controls will be written.
     *
     * @param bundle the bundle to write to
     */
    fun save(entity: Entity, bundle: Bundle) {
        val componentsBundle = Bundle("components")

        entity.components
                .filterIsInstance<SerializableComponent>()
                .forEach {
                    val b = Bundle(it.javaClass.canonicalName)
                    it.write(b)

                    componentsBundle.put(b.name, b)
                }

        bundle.put("components", componentsBundle)
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

        for (component in entity.components) {
            if (component is SerializableComponent) {

                val b = componentsBundle.get<Bundle>(component.javaClass.canonicalName)
                if (b != null)
                    component.read(b)
                else
                    log.warning("Bundle $componentsBundle does not have SerializableComponent: $component")
            }
        }
    }
}