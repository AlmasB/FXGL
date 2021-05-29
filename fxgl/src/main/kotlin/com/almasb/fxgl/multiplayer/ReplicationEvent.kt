/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.multiplayer

import javafx.event.Event
import javafx.event.EventType
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton

/**
 * Any event that extends ReplicationEvent is replicated for the other endpoint of a network connection.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class ReplicationEvent(eventType: EventType<out ReplicationEvent>) : Event(eventType) {

    companion object {
        @JvmField val ANY = EventType<ReplicationEvent>(Event.ANY, "REPLICATION_EVENT")

        @JvmField val ENTITY_SPAWN = EventType(ANY, "ENTITY_SPAWN")
        @JvmField val ENTITY_UPDATE = EventType(ANY, "ENTITY_UPDATE")
        @JvmField val ENTITY_REMOVE = EventType(ANY, "ENTITY_REMOVE")

        @JvmField val INPUT_ACTION_BEGIN = EventType(ANY, "INPUT_ACTION_BEGIN")
        @JvmField val INPUT_ACTION_END = EventType(ANY, "INPUT_ACTION_END")

        @JvmField val PROPERTY_UPDATE = EventType(ANY, "PROPERTY_UPDATE")
        @JvmField val PROPERTY_REMOVE = EventType(ANY, "PROPERTY_REMOVE")
    }
}

// TODO: consider SpawnData properties in the future
class EntitySpawnEvent(
        val networkID: Long,
        val entityName: String,
        val x: Double,
        val y: Double,
        val z: Double
) : ReplicationEvent(ENTITY_SPAWN)

class EntityUpdateEvent(
        val networkID: Long,
        val x: Double,
        val y: Double,
        val z: Double
) : ReplicationEvent(ENTITY_UPDATE)

class EntityRemoveEvent(
        val networkID: Long
) : ReplicationEvent(ENTITY_REMOVE)

class ActionBeginReplicationEvent(
        val key: KeyCode? = null,
        val btn: MouseButton? = null
) : ReplicationEvent(INPUT_ACTION_BEGIN)

class ActionEndReplicationEvent(
        val key: KeyCode? = null,
        val btn: MouseButton? = null
) : ReplicationEvent(INPUT_ACTION_END)

class PropertyUpdateReplicationEvent(
        val propertyName: String,
        val propertyValue: Any
) : ReplicationEvent(PROPERTY_UPDATE)

class PropertyRemoveReplicationEvent(
        val propertyName: String
) : ReplicationEvent(PROPERTY_REMOVE)