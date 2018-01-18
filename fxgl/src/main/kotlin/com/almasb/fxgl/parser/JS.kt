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

//private var cacheParser: JavaScriptParser? = null
//
///**
// * Generates a native JS object by evaluating a dynamic function with [objectProperties]
// * as the arguments to that function.
// *
// * So if [objectProperties] has (name = "hi") then
// * the returned JS object will have a property "name" and its value "hi", i.e. obj.name = "hi";
// */
//fun newJSObject(objectProperties: Map<String, Any>): Any {
//    if (cacheParser == null)
//        cacheParser = JavaScriptParser("")
//
//    val sb = StringBuilder("function e(" + objectProperties.keys.joinToString(",") + ") { var obj = {};")
//
//    objectProperties.keys.forEach {
//        sb.append("obj.$it = $it;")
//    }
//
//    sb.append("return obj; }")
//
//    cacheParser!!.eval<Any>(sb.toString())
//
//    return cacheParser!!.callFunction("e", *objectProperties.values.toTypedArray())
//}