/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.multiplayer

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.collection.PropertyChangeListener
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.collection.PropertyMapChangeListener
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.input.*
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.Connection

/**
 * TODO: symmetric remove API, e.g. removeReplicationSender()
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MultiplayerService : EngineService() {

    private val log = Logger.get(javaClass)

    private val replicatedEntitiesMap = hashMapOf<Connection<Bundle>, ConnectionData>()

    override fun onGameUpdate(tpf: Double) {
        if (replicatedEntitiesMap.isEmpty())
            return

        // TODO: can (should) we move this to NetworkComponent to act on a per entity basis ...
        replicatedEntitiesMap.forEach { conn, data ->
            if (data.entities.isNotEmpty()) {
                updateReplicatedEntities(conn, data.entities)
            }
        }
    }

    private fun updateReplicatedEntities(connection: Connection<Bundle>, entities: MutableList<Entity>) {
        val events = arrayListOf<ReplicationEvent>()

        entities.forEach {
            // TODO: if not present?
            val networkID = it.getComponent(NetworkComponent::class.java).id

            if (it.isActive) {
                events += EntityUpdateEvent(networkID, it.x, it.y, it.z)
            } else {
                events += EntityRemoveEvent(networkID)
            }
        }

        if (events.isNotEmpty()) {
            fire(connection, *events.toTypedArray())
        }

        entities.removeIf { !it.isActive }
    }

    fun spawn(connection: Connection<Bundle>, entity: Entity, entityName: String) {
        if (!entity.hasComponent(NetworkComponent::class.java)) {
            log.warning("Attempted to network-spawn entity $entityName, but it does not have NetworkComponent")
            return
        }

        val networkComponent = entity.getComponent(NetworkComponent::class.java)

        val event = EntitySpawnEvent(networkComponent.id, entityName, entity.x, entity.y, entity.z)

        val data = replicatedEntitiesMap.getOrDefault(connection, ConnectionData())
        data.entities += entity
        replicatedEntitiesMap[connection] = data

        fire(connection, event)
    }

    fun addEntityReplicationReceiver(connection: Connection<Bundle>, gameWorld: GameWorld) {
        connection.addMessageHandlerFX { _, message ->

            handleIfReplicationBundle(message) { event ->
                when (event) {
                    is EntitySpawnEvent -> {
                        val id = event.networkID
                        val entityName = event.entityName

                        val e = gameWorld.spawn(entityName, SpawnData(event.x, event.y, event.z))

                        // TODO: show warning if not present
                        e.getComponentOptional(NetworkComponent::class.java)
                                .ifPresent { it.id = id }
                    }

                    is EntityUpdateEvent -> {
                        val id = event.networkID

                        gameWorld.getEntitiesByComponentMapped(NetworkComponent::class.java)
                                .filterValues { it.id == id }
                                .forEach { (e, _) -> e.setPosition3D(event.x, event.y, event.z) }
                    }

                    is EntityRemoveEvent -> {
                        val id = event.networkID

                        gameWorld.getEntitiesByComponentMapped(NetworkComponent::class.java)
                                .filterValues { it.id == id }
                                .forEach { (e, _) -> e.removeFromWorld() }
                    }
                }
            }
        }
    }

    fun addInputReplicationSender(connection: Connection<Bundle>, input: Input) {
        input.addTriggerListener(object : TriggerListener() {
            override fun onActionBegin(trigger: Trigger) {

                val event = if (trigger.isKey) {
                    ActionBeginReplicationEvent(key = ((trigger) as KeyTrigger).key)
                } else {
                    ActionBeginReplicationEvent(btn = ((trigger) as MouseTrigger).button)
                }

                fire(connection, event)
            }

            override fun onActionEnd(trigger: Trigger) {
                val event = if (trigger.isKey) {
                    ActionEndReplicationEvent(key = ((trigger) as KeyTrigger).key)
                } else {
                    ActionEndReplicationEvent(btn = ((trigger) as MouseTrigger).button)
                }

                fire(connection, event)
            }
        })
    }

    fun addPropertyReplicationSender(connection: Connection<Bundle>, map: PropertyMap) {
        map.addListener(object : PropertyMapChangeListener {
            override fun onUpdated(propertyName: String, propertyValue: Any) {
                val event = PropertyUpdateReplicationEvent(propertyName, propertyValue)

                fire(connection, event)
            }

            override fun onRemoved(propertyName: String, propertyValue: Any) {
                val event = PropertyRemoveReplicationEvent(propertyName)

                fire(connection, event)
            }
        })
    }

    fun addPropertyReplicationReceiver(connection: Connection<Bundle>, map: PropertyMap) {
        connection.addMessageHandlerFX { _, message ->
            handleIfReplicationBundle(message) { event ->

                when (event) {
                    is PropertyUpdateReplicationEvent -> {
                        map.setValue(event.propertyName, event.propertyValue)
                    }

                    is PropertyRemoveReplicationEvent -> {
                        map.remove(event.propertyName)
                    }
                }
            }
        }
    }

    fun addInputReplicationReceiver(connection: Connection<Bundle>, input: Input) {
        connection.addMessageHandlerFX { _, message ->

            handleIfReplicationBundle(message) { event ->

                if (event is ActionBeginReplicationEvent) {
                    event.key?.let {
                        input.mockTriggerPress(KeyTrigger(it))
                    }

                    event.btn?.let {
                        input.mockTriggerPress(MouseTrigger(it))
                    }

                } else if (event is ActionEndReplicationEvent) {
                    event.key?.let {
                        input.mockTriggerRelease(KeyTrigger(it))
                    }

                    event.btn?.let {
                        input.mockTriggerRelease(MouseTrigger(it))
                    }
                }
            }
        }
    }

    fun addEventReplicationSender(connection: Connection<Bundle>, eventBus: EventBus) {
        eventBus.addEventHandler(ReplicationEvent.ANY) { event ->
            fire(connection, event)
        }
    }

    fun addEventReplicationReceiver(connection: Connection<Bundle>, eventBus: EventBus) {
        connection.addMessageHandlerFX { _, message ->
            handleIfReplicationBundle(message) {
                eventBus.fireEvent(it)
            }
        }
    }

    private fun fire(connection: Connection<Bundle>, vararg events: ReplicationEvent) {
        if (!connection.isConnected)
            return

        // this is the only place where we create a replication event carrying bundle
        val bundle = Bundle("REPLICATION_EVENT")

        val list = ArrayList<ReplicationEvent>(events.toList())

        bundle.put("events", list)

        connection.send(bundle)
    }

    private fun handleIfReplicationBundle(bundle: Bundle, handler: (ReplicationEvent) -> Unit) {
        if (bundle.name == "REPLICATION_EVENT") {
            val events: List<ReplicationEvent> = bundle.get("events")

            events.forEach(handler)
        }
    }

    private class ConnectionData {
        val entities = ArrayList<Entity>()
    }
}