/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package dev

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityFactory
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.Spawns

/**
 * Creates all entities.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ZephFactory : EntityFactory {

    @Spawns("char")
    fun newCharacter(data: SpawnData): Entity {

        return Entity()
    }

    @Spawns("player")
    fun newPlayer(data: SpawnData): Entity {
        return Entity()
    }

    @Spawns("item")
    fun newItem(data: SpawnData): Entity {
        return Entity()
    }

    @Spawns("nav")
    fun newWalkableCell(data: SpawnData): Entity {
        return Entity()
    }

    @Spawns("portal")
    fun newPoral(data: SpawnData): Entity {
        return Entity()
    }

    @Spawns("cellSelection")
    fun newCellSelection(data: SpawnData): Entity {
        return Entity()
    }
}