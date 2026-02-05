/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.reflect

import java.lang.RuntimeException
import java.lang.reflect.Method
import java.util.function.BiFunction

/**
 * Allows calling methods of any object using its String name
 * and String arguments.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
class ReflectionFunctionCaller {

    private val functions = hashMapOf<FunctionSignature, ReflectionFunction>()

    /**
     * Provides conversions from [String] to other types.
     */
    private val stringToObject = hashMapOf<Class<*>, (String) -> Any>()

    var defaultFunctionHandler: BiFunction<String, List<String>, Any> = BiFunction { name, args ->
        throw RuntimeException("No function handler for $name with args $args")
    }

    val methods: List<Method>
        get() = functions.values.map { it.method }

    init {
        // register common types
        stringToObject[String::class.java] = { it }
        stringToObject[Int::class.java] = { it.toInt() }
        stringToObject[Double::class.java] = { it.toDouble() }
        stringToObject[Boolean::class.java] = {
            when (it) {
                "true" -> true
                "false" -> false
                else -> throw RuntimeException("Cannot convert $it to Boolean")
            }
        }
        stringToObject[List::class.java] = { it.split(",") }
    }

    fun <T : Any> addStringToObjectConverter(type: Class<T>, converter: (String) -> T) {
        stringToObject[type] = converter
    }

    fun removeStringToObjectConverter(type: Class<*>) {
        stringToObject.remove(type)
    }

    /**
     * Add all methods (incl. private) of [targetObject] to be invokable
     * by this reflection function caller.
     */
    fun addFunctionCallTarget(targetObject: Any) {
        targetObject.javaClass.declaredMethods.forEach {
            val signature = FunctionSignature(it.name, it.parameterCount)
            val method = ReflectionFunction(targetObject, it)
            it.isAccessible = true

            functions[signature] = method
        }
    }

    /**
     * Remove all methods of a previously added [targetObject].
     */
    fun removeFunctionCallTarget(targetObject: Any) {
        functions.filterValues { it.functionCallTarget === targetObject }
                .forEach { functions.remove(it.key) }
    }

    /**
     * @return true if [functionName] with [paramCount] number of parameters exists
     */
    fun exists(functionName: String, paramCount: Int): Boolean {
        return functions.containsKey(FunctionSignature(functionName, paramCount))
    }

    fun call(functionName: String, args: List<String>): Any {
        return call(functionName, args.toTypedArray())
    }

    fun call(functionName: String, args: Array<String>): Any {
        val function = functions[FunctionSignature(functionName, args.size)]

        if (function != null) {
            val argsAsObjects = function.method.parameterTypes.mapIndexed { index, type ->
                val converter = stringToObject[type] ?: throw java.lang.RuntimeException("No converter found from String to $type")
                converter.invoke(args[index])
            }

            // void returns null, but Any is expected, so we return 0 in such cases
            return function.method.invoke(function.functionCallTarget, *argsAsObjects.toTypedArray()) ?: 0
        }

        return defaultFunctionHandler.apply(functionName, args.toList())
    }

    fun invoke(functionName: String, args: List<Any>): Any {
        return invoke(functionName, args.toTypedArray())
    }

    /**
     * An equivalent of [call], but allows any type of arguments, not just String.
     *
     * @return 0 for void type functions, otherwise return value from the invoked function
     */
    fun invoke(functionName: String, args: Array<Any>): Any {
        val function = functions[FunctionSignature(functionName, args.size)]

        if (function != null) {
            // void returns null, but Any is expected, so we return 0 in such cases
            return function.method.invoke(function.functionCallTarget, *args) ?: 0
        }

        return defaultFunctionHandler.apply(functionName, args.toList().map { it.toString() })
    }

    private data class FunctionSignature(val name: String, val paramCount: Int)

    /**
     * Stores the object [functionCallTarget] and the function [method] that can be invoked on the object.
     */
    private data class ReflectionFunction(val functionCallTarget: Any, val method: Method)
}