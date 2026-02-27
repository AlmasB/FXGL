/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.reflect.ReflectionFunctionCaller
import com.almasb.fxgl.logging.Logger
import java.util.function.BiFunction

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class DialogueScriptRunner(

        /**
         * Variables global to the game.
         */
        private val gameVars: PropertyMap,

        /**
         * Variables local to the dialogue context.
         */
        private val localVars: PropertyMap,
        private val functionHandler: FunctionCallHandler
) {

    private val log = Logger.get<DialogueScriptRunner>()

    private val builtInFunctions = object : FunctionCallHandler() {

        // we check property maps in this order: local first, then global
        private val maps = listOf(localVars, gameVars)

        fun add(varName: String, value: String) {
            applyOP(varName, {
                it.increment(varName, value.toInt())
            }, {
                it.increment(varName, value.toDouble())
            })
        }

        fun sub(varName: String, value: String) {
            add(varName, "-$value")
        }

        fun mul(varName: String, value: String) {
            applyOP(varName, {
                it.multiply(varName, value.toInt())
            }, {
                it.multiply(varName, value.toDouble())
            })
        }

        fun div(varName: String, value: String) {
            applyOP(varName, {
                it.divide(varName, value.toInt())
            }, {
                it.divide(varName, value.toDouble())
            })
        }

        private fun applyOP(varName: String, operation1: (PropertyMap) -> Unit, operation2: (PropertyMap) -> Unit) {
            for (vars in maps) {
                if (vars.exists(varName)) {
                    try {
                        operation1(vars)
                    } catch (e1: Exception) {
                        try {
                            operation2(vars)
                        } catch (e2: Exception) {
                            log.warning("Cannot apply operation on $varName: $e1 $e2")
                        }
                    }

                    // if we found in local, then skip global
                    break
                }
            }
        }
    }

    /**
     * Given a [line], this function replaces all variables with their values.
     */
    fun replaceVariablesInText(line: String): String {
        val varNames = line.split(" +".toRegex())
                .filter { it.startsWith("\$") && it.length > 1 }
                .map {
                    if (!it.last().isLetterOrDigit())
                        it.substring(1, it.length - 1)
                    else
                        it.substring(1)
                }
                .toSet()

        return replaceVariables(line, varNames)
    }

    private fun replaceVariables(line: String, varNames: Set<String>): String {
        var result = line

        varNames.forEach {
            if (localVars.exists(it)) {
                val value = localVars.getValue<Any>(it)
                result = result.replace("\$$it", value.toString())

            } else if (gameVars.exists(it)) {
                val value = gameVars.getValue<Any>(it)
                result = result.replace("\$$it", value.toString())
            }
        }

        return result
    }

    /**
     * Called from a branch node.
     */
    fun callBooleanFunction(line: String): Boolean {
        val result = if (line.isEqualityCheckFunction())
            callEqualityCheckFunction(replaceVariablesInText(line))
        else
            callFunction(line)

        if (result !is Boolean) {
            log.warning("A boolean function call did not return a boolean: ${line}. Assuming result <false>.")
            return false
        }

        return result
    }

    private fun callEqualityCheckFunction(line: String): Any {
        val operators = linkedMapOf<String, (Any, Any) -> Boolean>()
        operators["=="] = { lhs, rhs -> lhs == rhs }
        operators[">="] = { lhs, rhs -> lhs >= rhs }
        operators["<="] = { lhs, rhs -> lhs <= rhs }
        operators[">"] = { lhs, rhs -> lhs > rhs }
        operators["<"] = { lhs, rhs -> lhs < rhs }

        val entry = operators.entries.find { line.contains(it.key) }

        if (entry != null) {
            val (op, func) = entry

            val lhs = line.substringBefore(op).trim().toTypedValue()
            val rhs = line.substringAfter(op).trim().toTypedValue()

            return func.invoke(lhs, rhs)
        }

        return NullObject
    }

    /**
     * Called from a function node.
     */
    fun callFunction(line: String): Any {
        if (line.isAssignmentFunction()) {
            callAssignmentFunction(line)
            return NullObject
        }

        val tokens = line.trim().split(" +".toRegex())

        require(tokens.isNotEmpty()) { "Empty function call: $line" }

        val funcName = tokens[0].trim()

        if (tokens.size == 1) {
            // check if this is a boolean variable -- the only valid variable to use here
            if (localVars.exists(funcName)) {
                return localVars.getValue(funcName)
            }

            if (gameVars.exists(funcName)) {
                return gameVars.getValue(funcName)
            }
        }

        val args = tokens.drop(1).map { replaceVariablesInText(it.trim()) }.toTypedArray()

        // check built-in handler first
        if (builtInFunctions.exists(funcName, args.size)) {
            return builtInFunctions.call(funcName, args)
        }

        return functionHandler.call(funcName, args)
    }

    private fun callAssignmentFunction(line: String) {
        log.debug("callAssignmentFunction( $line )")

        val varName = line.substringBefore('=').trim()
        val varValue = line.substringAfter('=').trim().toTypedValue()

        if (localVars.exists(varName)) {
            localVars.setValue(varName, varValue)
        } else if (gameVars.exists(varName)) {
            gameVars.setValue(varName, varValue)
        } else {
            // does not exist anywhere, create locally
            localVars.setValue(varName, varValue)
        }
    }
}

private fun String.isAssignmentFunction(): Boolean {
    return this.contains('=')
}

private fun String.isEqualityCheckFunction(): Boolean {
    return this.contains("==")
            || this.contains(">=")
            || this.contains("<=")
            || this.contains(">")
            || this.contains("<")
}

private fun String.toTypedValue(): Any {
    val s = this

    if (s == "true")
        return true

    if (s == "false")
        return false

    return s.toIntOrNull() ?: s.toDoubleOrNull() ?: s
}

private operator fun Any.compareTo(other: Any): Int {
    if (this is String && other is String) {
        return this.compareTo(other)
    }

    if (this is Boolean && other is Boolean) {
        return this.compareTo(other)
    }

    if (this is Int && other is Int) {
        return this.compareTo(other)
    }

    if (this is Int && other is Double) {
        return this.compareTo(other)
    }

    if (this is Double && other is Int) {
        return this.compareTo(other)
    }

    if (this is Double && other is Double) {
        return this.compareTo(other)
    }

    throw RuntimeException("Cannot compare: $this and $other")
}

private object NullObject

/**
 * Marks a class as one that contains functions that can be called
 * by [FunctionCallHandler].
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface FunctionCallDelegate

abstract class FunctionCallHandler : FunctionCallDelegate {

    private val log = Logger.get(javaClass)

    val rfc = ReflectionFunctionCaller()

    init {
        // add self as a delegate so that all methods of the implementing class
        // are automatically added into [methods]
        rfc.addFunctionCallTarget(this)
        rfc.defaultFunctionHandler = BiFunction { name, args ->
            handle(name, args.toTypedArray())
        }
    }

    fun addFunctionCallDelegate(obj: FunctionCallDelegate) {
        rfc.addFunctionCallTarget(obj)

        rfc.methods.forEach {
            log.debug("Added cmd: $it")
        }
    }

    fun removeFunctionCallDelegate(obj: FunctionCallDelegate) {
        rfc.removeFunctionCallTarget(obj)
    }

    fun exists(functionName: String, paramCount: Int): Boolean {
        return rfc.exists(functionName, paramCount)
    }

    fun call(functionName: String, args: Array<String>): Any {
        return rfc.call(functionName, args)
    }

    /**
     * A default handle function, which is called when no function delegate with matching method signature was found.
     */
    protected open fun handle(functionName: String, args: Array<String>): Any {
        log.warning("Function call from dialogue graph via default implementation of FunctionCallHandler:")
        log.warning("$functionName ${args.toList()}")
        return 0
    }
}