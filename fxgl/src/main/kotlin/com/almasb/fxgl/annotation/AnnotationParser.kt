package com.almasb.fxgl.annotation

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.core.reflect.ReflectionUtils

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnnotationParser(appClass: Class<out GameApplication>) {

    private val log = Logger.get(javaClass)

    private val packageName = appClass.`package`?.name

    val isDisabled = packageName?.contains("[A-Z]".toRegex()) ?: true

    private val annotationMap = hashMapOf<Class<*>, List<Class<*>>>()

    init {
        if (isDisabled) {
            log.warning("${appClass.simpleName} has no package or contains uppercase letters. Disabling annotations processing")
        }
    }

    fun parse(vararg annotationClasses: Class<out Annotation>) {
        if (!isDisabled) {
            annotationMap.putAll(ReflectionUtils.findClasses(packageName!!, *annotationClasses))
            annotationMap.forEach { annotationClass, list ->
                log.debug("@${annotationClass.simpleName}: ${list.map { it.simpleName }}")
            }
        }
    }

    fun getClasses(annotationClass: Class<out Annotation>): List<Class<*>> {
        return annotationMap[annotationClass] ?: emptyList()
    }
}