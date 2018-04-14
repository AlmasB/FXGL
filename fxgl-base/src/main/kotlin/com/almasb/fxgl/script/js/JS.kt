/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.script.js

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.script.Script
import jdk.nashorn.api.scripting.JSObject
import javax.script.*

/**
 * Allows to parse valid javascript source files.
 * Once parsed, {@link #callFunction(String, Object...)}
 * can be used to invoke a JS function.
 */

/**
 * Constructs new javascript parser for given .js file or source string.
 * The file will be loaded with AssetLoader.
 *
 * @param scriptFileName name of script file under "/assets/scripts/" or source string
 * @throws IllegalArgumentException if syntax error
 */

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class JS(val scriptCode: String) : Script {

    companion object {
        private val manager = ScriptEngineManager()

        private val scriptEngine: ScriptEngine
        private val engine: Invocable

        init {
            manager.put("HOME_DIR", JS::class.java.getResource("/assets/scripts/"))
            manager.put("FXGL", FXGL.Companion)
            manager.put("APP", FXGL.getApp())

            scriptEngine = manager.getEngineByName("nashorn")

            val global = SimpleScriptContext()
            global.setBindings(scriptEngine.createBindings(), ScriptContext.GLOBAL_SCOPE)

            // evaluate our library code in global context
            scriptEngine.eval(FXGL.getAssetLoader().loadScriptRaw("FXGL.js"))

            engine = scriptEngine as Invocable
        }
    }

    /**
     * The local context in which this JS script runs.
     */
    private val context: ScriptContext = SimpleScriptContext()

    init {
        context.setBindings(scriptEngine.createBindings(), ScriptContext.ENGINE_SCOPE)

        eval<Void>(scriptCode)
    }

    /**
     * Invokes a JS function.
     *
     * @param name function name
     * @param args function arguments
     * @param <T> return type
     * @return object returned by function
     * @throws IllegalArgumentException if any error occurred during invocation
     */
    override fun <T> call(functionName: String, vararg args: Any): T {
        try {
            // from https://stackoverflow.com/questions/30140103/should-i-use-a-separate-scriptengine-and-compiledscript-instances-per-each-threa
            // ((JSObject)bindings.get(fnName).call(this, args...)

            // call the function from the code in local context
            return (context.getBindings(ScriptContext.ENGINE_SCOPE)[functionName] as JSObject).call(this, *args) as T
        } catch (e: Exception) {
            throw IllegalArgumentException("Function call failed: " + e)
        }
    }

    /**
     * evaluate the code in local context
     */
    override fun <T> eval(script: String): T {
        try {
            //return (context.getBindings(ScriptContext.ENGINE_SCOPE) as JSObject).eval(script) as T
            return scriptEngine.eval(script, context) as T
        } catch (e: Exception) {
            throw IllegalArgumentException("Evaluation failed: " + e)
        }
    }

    /**
     * @param name function name
     * @return true if this script has a function with given name
     */
    override fun hasFunction(functionName: String): Boolean {
        // https://stackoverflow.com/questions/20578299/checking-if-a-function-exists-within-java-scriptengine

        try {
            val test = ("typeof " + functionName
                    + " === 'function' ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE")
            return scriptEngine.eval(test, context) as Boolean
        } catch (e: Exception) {
            throw IllegalArgumentException("Function check failed: " + e)
        }
    }
}