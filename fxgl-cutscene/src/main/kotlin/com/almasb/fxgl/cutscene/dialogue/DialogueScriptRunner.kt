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
     * Called from a branch node.
     */
    fun callBooleanFunction(line: String): Boolean {
        // TODO: parse variables before calling the check function

        val result = if (line.isEqualityCheckFunction()) callEqualityCheckFunction(line) else callFunction(line)

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

        //return functionHandler.handle(funcName, tokens.drop(1).map { it.trim().parseVariables() }.toTypedArray())

        return functionHandler.handle(funcName, tokens.drop(1).map { it.trim() }.toTypedArray())
    }

    fun callAssignmentFunction(line: String) {
        log.debug("callAssignmentFunction( $line )")

        val varName = line.substringBefore('=').trim()
        val varValue = line.substringAfter('=').trim()

        localVars[varName] = varValue.toTypedValue()
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

    throw RuntimeException("Not int or double: $this,$other")
}

private object NullObject