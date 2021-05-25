/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.cutscene.dialogue

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.logging.Logger
import java.lang.RuntimeException

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class DialogueScriptRunner(
        private val gameVars: PropertyMap,
        private val functionHandler: FunctionCallHandler
) {

    private val log = Logger.get<DialogueScriptRunner>()

    private val localVars = hashMapOf<String, Any>()

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
            if (it in localVars) {
                val value = localVars[it]
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
            if (funcName in localVars) {
                return localVars[funcName]!!
            }

            if (gameVars.exists(funcName)) {
                return gameVars.getValue(funcName)
            }
        }

        // if all of above checks did not succeed, then call a default function handler
        return functionHandler.handle(funcName, tokens.drop(1).map { replaceVariablesInText(it.trim()) }.toTypedArray())
    }

    private fun callAssignmentFunction(line: String) {
        log.debug("callAssignmentFunction( $line )")

        val varName = line.substringBefore('=').trim()
        val varValue = line.substringAfter('=').trim().toTypedValue()

        if (varName in localVars) {
            localVars[varName] = varValue
        } else if (gameVars.exists(varName)) {
            gameVars.setValue(varName, varValue)
        } else {
            localVars[varName] = varValue
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