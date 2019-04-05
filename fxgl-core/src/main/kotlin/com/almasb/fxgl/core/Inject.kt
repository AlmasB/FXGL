/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core

/**
 * Marks a field within a service provider as injectable.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Inject(
        val value: String
)