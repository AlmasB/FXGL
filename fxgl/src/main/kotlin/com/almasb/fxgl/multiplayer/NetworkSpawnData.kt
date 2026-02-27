/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.multiplayer

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.entity.SpawnData
import java.io.Serializable

/**
 * Wrapper around SpawnData to be sent over network.
 *
 * @author Jonas Andersen (dev@jonasandersen.no)
 */
class NetworkSpawnData(spawnData: SpawnData) : Serializable {

    val bundle = Bundle("NetworkSpawnData")
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0

    init {
        x = spawnData.x
        y = spawnData.y
        z = spawnData.z

        spawnData.data.forEach { (key, value) ->
            if (value is Serializable) {
                bundle.put(key, value)
            }
        };
    }
}