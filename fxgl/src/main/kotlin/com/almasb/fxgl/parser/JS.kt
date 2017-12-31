/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.parser

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

private var cacheParser: JavaScriptParser? = null

/**
 * // TODO: reword
 * Generates a native JS object by creating evaluating a function
 * that takes map values and assigns to keys in the object.
 * Finally, passes Java objects as arguments to the function.
 */
fun newJSObject(objectProperties: Map<String, Any>): Any {
    if (cacheParser == null)
        cacheParser = JavaScriptParser("")

    val sb = StringBuilder("function e(" + objectProperties.keys.joinToString(",") + ") { var obj = {};")

    objectProperties.keys.forEach {
        sb.append("obj.$it = $it;")
    }

    sb.append("return obj; }")

    // TODO: add toString() to generated JS object for debugging
    //println("JS: " + sb.toString())

    cacheParser!!.eval<Any>(sb.toString())

    return cacheParser!!.callFunction("e", *objectProperties.values.toTypedArray())
}