/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.multiplayer

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.input.*
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.Connection

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MultiplayerService : EngineService() {

    private val log = Logger.get(javaClass)

    private val replicatedEntitiesMap = hashMapOf<Connection<Bundle>, MutableList<Entity>>()

    override fun onGameUpdate(tpf: Double) {
        if (replicatedEntitiesMap.isEmpty())
            return

        // TODO: can (should) we move this to NetworkComponent to act on a per entity basis ...
        replicatedEntitiesMap.forEach { conn, entities ->
            if (entities.isNotEmpty()) {
                updateReplicatedEntities(conn, entities)
            }
        }
    }

    private fun updateReplicatedEntities(connection: Connection<Bundle>, entities: MutableList<Entity>) {
        val updateBundle = Bundle("ENTITY_UPDATES_EVENT")
        val removeBundle = Bundle("ENTITY_REMOVALS_EVENT")

        val removeIDs = arrayListOf<Int>()

        entities.forEach {
            val networkID = it.getComponent(NetworkComponent::class.java).id

            if (it.isActive) {
                updateBundle.put("$networkID", Vec2(it.position))
            } else {
                // TODO: if not present?
                removeIDs += networkID
            }
        }

        if (updateBundle.data.isNotEmpty()) {
            connection.send(updateBundle)
        }

        if (removeIDs.isNotEmpty()) {
            removeBundle.put("removeIDs", removeIDs)
            connection.send(removeBundle)
        }

        entities.removeIf { !it.isActive }
    }

    fun spawn(connection: Connection<Bundle>, entity: Entity, entityName: String) {
        if (!entity.hasComponent(NetworkComponent::class.java)) {
            log.warning("Attempted to network-spawn entity $entityName, but it does not have NetworkComponent")
            return
        }

        val networkComponent = entity.getComponent(NetworkComponent::class.java)

        // TODO: these events should be constant-extracted somewhere
        val bundle = Bundle("ENTITY_SPAWN_EVENT_${networkComponent.id}")
        bundle.put("entityName", entityName)
        bundle.put("x", entity.x)
        bundle.put("y", entity.y)

        val list = replicatedEntitiesMap.getOrDefault(connection, ArrayList())
        list += entity
        replicatedEntitiesMap[connection] = list

        connection.send(bundle)
    }

    fun addEntityReplicationReceiver(connection: Connection<Bundle>, gameWorld: GameWorld) {
        connection.addMessageHandlerFX { _, message ->
            if (message.name.startsWith("ENTITY_SPAWN_EVENT")) {
                val id = message.name.removePrefix("ENTITY_SPAWN_EVENT_").toInt()
                val entityName = message.get<String>("entityName")
                val x = message.get<Double>("x")
                val y = message.get<Double>("y")

                val e = gameWorld.spawn(entityName, x, y)

                // TODO: show warning if not present
                e.getComponentOptional(NetworkComponent::class.java)
                        .ifPresent { it.id = id }
            }

            if (message.name.startsWith("ENTITY_UPDATES_EVENT")) {
                message.data.forEach { idString, vec2 ->
                    val id = idString.toInt()
                    val position = vec2 as Vec2

                    gameWorld.getEntitiesByComponent(NetworkComponent::class.java)
                            .filter { it.getComponent(NetworkComponent::class.java).id == id }
                            .forEach { it.setPosition(position) }
                }
            }

            if (message.name.startsWith("ENTITY_REMOVALS_EVENT")) {
                val removeIDs: List<Int> = message.get("removeIDs")

                removeIDs.forEach { id ->
                    // TODO: new func getEntitiesByComponent() that also provides actual components as BiConsumer?
                    gameWorld.getEntitiesByComponent(NetworkComponent::class.java)
                            .filter { it.getComponent(NetworkComponent::class.java).id == id }
                            .forEach { it.removeFromWorld() }
                }
            }
        }
    }

    fun addInputReplicationSender(connection: Connection<Bundle>, input: Input) {
        input.addTriggerListener(object : TriggerListener() {
            override fun onActionBegin(trigger: Trigger) {
                val bundle = Bundle("ActionBegin")

                // TODO: refactor into read() write() as part of Trigger
                if (trigger.isKey) {
                    bundle.put("key", ((trigger) as KeyTrigger).key)
                } else {
                    bundle.put("btn", ((trigger) as MouseTrigger).button)
                }

                connection.send(bundle)
            }

            override fun onActionEnd(trigger: Trigger) {
                val bundle = Bundle("ActionEnd")

                if (trigger.isKey) {
                    bundle.put("key", ((trigger) as KeyTrigger).key)
                } else {
                    bundle.put("btn", ((trigger) as MouseTrigger).button)
                }

                connection.send(bundle)
            }
        })
    }

    fun addInputReplicationReceiver(connection: Connection<Bundle>, input: Input) {
        connection.addMessageHandlerFX { _, message ->
            when (message.name) {
                "ActionBegin", "ActionEnd" -> {
                    val isKeyTrigger = message.exists("key")

                    val trigger: Trigger = if (isKeyTrigger) {
                        KeyTrigger(message.get("key"))
                    } else {
                        MouseTrigger(message.get("btn"))
                    }

                    if (message.name == "ActionBegin") {
                        input.mockTriggerPress(trigger)
                    } else {
                        input.mockTriggerRelease(trigger)
                    }
                }
            }
        }
    }
}