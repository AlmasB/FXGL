/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.script

import com.almasb.fxgl.script.js.JS

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ScriptFactory {

    companion object {
        @JvmStatic fun fromCode(scriptCode: String): Script {
            return JS(scriptCode)
        }

//        @JvmStatic fun fromFile(file: Path): Script {
//            return JS(Files.readAllLines(file).joinToString("\n", "", "\n"))
//        }

        private val cacheParser: JS by lazy { JS("") }

        /**
         * Generates a native JS object by evaluating a dynamic function with [objectProperties]
         * as the arguments to that function.
         *
         * So if [objectProperties] has (name = "hi") then
         * the returned JS object will have a property "name" and its value "hi", i.e. obj.name = "hi";
         */
        fun newScriptObject(objectProperties: Map<String, Any>): Any {

            val sb = StringBuilder("function e(" + objectProperties.keys.joinToString(",") + ") { var obj = {};")

            objectProperties.keys.forEach {
                sb.append("obj.$it = $it;")
            }

            sb.append("return obj; }")

            cacheParser.eval<Any>(sb.toString())

            return cacheParser.call("e", *objectProperties.values.toTypedArray())
        }
    }
}