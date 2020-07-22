/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.chat

import com.almasb.fxgl.app.services.IOTaskExecutorService
import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.dsl.random
import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.net.*
import com.almasb.fxgl.scene.SceneService
import javafx.scene.input.KeyCode
import java.net.Socket
import java.util.function.Consumer

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ChatService : EngineService() {

    private lateinit var taskService: IOTaskExecutorService
    private lateinit var netService: NetService
    private lateinit var sceneService: SceneService

    private lateinit var chatSubScene: ChatSubScene

    private lateinit var endpoint: Endpoint<Bundle>

    private lateinit var userName: String




    //private val handlers = arrayListOf<OLDMH<Bundle>>()

    //private lateinit var handler: MessageHandler<Bundle>

    // TODO: Attempted to close connection 1 but it is already closed.
    // check connection num all connectinos seem 1

    // TODO: should this go to fxgl-net, idea of User?
    //private val connectionsToUserNames = hashMapOf<Socket, String>()

    override fun onInit() {
        chatSubScene = ChatSubScene(sceneService.appWidth, sceneService.appHeight)
        chatSubScene.inputHandler = Consumer {

            if (endpoint is Server) {
                chatSubScene.appendMessage(userName, it)
            }


            val bundle = Bundle("UserChatMessage")
            bundle.put("userName", userName)
            bundle.put("message", it)

            endpoint.broadcast(bundle)
        }

        sceneService.input.addAction(object : UserAction("Open Chat Window") {
            override fun onActionBegin() {

                // TODO: what if there is another sub scene on top of chat ...
                if (chatSubScene.isActive) {
                    sceneService.popSubScene()
                } else {
                    sceneService.pushSubScene(chatSubScene)
                }
            }
        }, KeyCode.C, InputModifier.ALT)
    }

    fun startServer(port: Int) {
        userName = "Test Server"

        val server = netService.newTCPServer(port)

        endpoint = server

        server.setOnConnected { conn ->

            conn.addMessageHandlerFX(ServerSideMessageHandler())

            val bundle = Bundle("UserChatLogins")

            val list = arrayListOf(userName)
            list.addAll(endpoint.connections.filter { it !== conn }.map { it.localSessionData.getString("userName") })

            bundle.put("userNames", list)

            conn.send(bundle)
        }

        server.setOnDisconnected {
            Async.startAsyncFX {
                val name = it.localSessionData.getString("userName")

                chatSubScene.removeUser(name)

                val bundle = Bundle("UserChatLogoff")
                bundle.put("userName", name)

                endpoint.broadcast(bundle)
            }
        }

        taskService.runAsync(server.startTask())
    }

    fun startClient(ip: String, port: Int) {
        userName = "RandomClient-" + random(1, 100)

        val client = netService.newTCPClient(ip, port)

        endpoint = client

        client.setOnConnected {

            it.addMessageHandlerFX(ClientSideMessageHandler())

            val bundle = Bundle("UserChatLogin")
            bundle.put("userName", userName)

            it.send(bundle)
        }

        taskService.runAsync(client.connectTask())
    }

    private inner class ServerSideMessageHandler : MessageHandler<Bundle> {
        override fun onReceive(connection: Connection<Bundle>, message: Bundle) {
            when (message.name) {
                "UserChatLogin" -> {
                    chatSubScene.addUser(message.get("userName"))

                    connection.localSessionData.setValue("userName", message.get("userName"))
                }

                "UserChatMessage" -> {
                    chatSubScene.appendMessage(message.get("userName"), message.get("message"))
                }
            }

            endpoint.broadcast(message)
        }
    }

    private inner class ClientSideMessageHandler : MessageHandler<Bundle> {
        override fun onReceive(connection: Connection<Bundle>, message: Bundle) {
            when (message.name) {
                "UserChatLogin" -> {
                    chatSubScene.addUser(message.get("userName"))
                }

                "UserChatLogoff" -> {
                    chatSubScene.removeUser(message.get("userName"))
                }

                "UserChatLogins" -> {
                    val list: List<String> = message.get("userNames")

                    list.forEach {
                        chatSubScene.addUser(it)
                    }
                }

                "UserChatMessage" -> {
                    chatSubScene.appendMessage(message.get("userName"), message.get("message"))
                }
            }
        }
    }

    override fun onExit() {
        endpoint.connections.forEach { it.terminate() }

        if (endpoint is Server) {
            (endpoint as Server).stop()
        }
    }
}