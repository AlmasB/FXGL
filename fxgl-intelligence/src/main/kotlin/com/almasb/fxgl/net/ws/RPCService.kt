/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net.ws

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.reflect.ReflectionFunctionCaller
import com.almasb.fxgl.logging.Logger

private const val SEPARATOR = "*,,*"
private const val FUNCTION_CALL_TAG = "F_CALL:"
private const val FUNCTION_RETURN_TAG = "F_RETURN:"

/**
 * Allows a remote application (possibly written in a different language)
 * to issue function calls to and accept function calls from subclasses of this service.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
abstract class RPCService(

        /**
         * The server to which clients connect.
         * Maintenance responsibility of the server object lies with this RPC service.
         */
        protected val server: LocalWebSocketServer
) : EngineService() {

    private val log = Logger.get(RPCService::class.java)

    private val rfc = ReflectionFunctionCaller()

    init {
        server.addMessageHandler { message ->
            if (message.startsWith(FUNCTION_CALL_TAG)) {
                val funcName = message.substringAfter(FUNCTION_CALL_TAG).substringBefore(SEPARATOR)
                var remainder = message.substringAfter(SEPARATOR)

                // this means there is at least 1 arg
                if (remainder.endsWith(SEPARATOR)) {
                    // remove last unnecessary separator
                    remainder = remainder.removeSuffix(SEPARATOR)
                }

                if (remainder.isEmpty()) {
                    // there are no arguments, call no-arg version
                    rfc.call(funcName, emptyList())
                } else {
                    val args = remainder.split(SEPARATOR)

                    rfc.call(funcName, args)
                }
            }

            if (message.startsWith(FUNCTION_RETURN_TAG)) {
                // TODO:
            }
        }
    }

    final override fun onInit() {
        rfc.addFunctionCallTarget(this)
        log.debug("Added ${javaClass.simpleName} methods: ${rfc.methods.map { it.name }}")

        server.start()
    }

    fun rpcRun(funcName: String, vararg args: String) {
        rpcRun(funcName, args.toList())
    }

    fun rpcRun(funcName: String, args: List<String>) {
        var argsString = ""

        args.forEach { argsString += it + SEPARATOR }

        if (argsString.isNotEmpty()) {
            argsString = argsString.removeSuffix(SEPARATOR)
        }

        server.send("$FUNCTION_CALL_TAG$funcName$SEPARATOR$argsString")
    }

    private fun rpcReturn() {
        // TODO:
    }

    override fun onExit() {
        server.stop()
    }
}