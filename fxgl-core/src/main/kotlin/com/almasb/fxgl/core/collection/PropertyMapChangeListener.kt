/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection

/**
 * Notifies the caller of any changes that occur in a [PropertyMap].
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface PropertyMapChangeListener {

    /**
     * This includes add/update operations.
     */
    fun onUpdated(propertyName: String, propertyValue: Any)

    /**
     * This includes only remove operations.
     */
    fun onRemoved(propertyName: String, propertyValue: Any)
}