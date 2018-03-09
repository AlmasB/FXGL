/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.core.reflect.ReflectionUtils

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnnotationParser(val appClass: Class<out GameApplication>) {

    private val log = Logger.get(javaClass)

    private val packageName = appClass.`package`?.name

    val isDisabled = true

    private val annotationMap = hashMapOf<Class<*>, List<Class<*>>>()

    init {
        //log.debug("Main app package name: ${packageName ?: "NO PACKAGE"}")

        if (isDisabled) {
            log.warning("Class-level annotations have been deprecated!")
        }
    }

    fun parse(vararg annotationClasses: Class<out Annotation>) {

    }

    fun getClasses(annotationClass: Class<out Annotation>): List<Class<*>> {
        return annotationMap[annotationClass] ?: emptyList()
    }
}