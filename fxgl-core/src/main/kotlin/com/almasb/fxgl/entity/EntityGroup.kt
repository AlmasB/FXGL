/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.core.Disposable
import java.util.function.Consumer

/**
 * A group of entities of particular types.
 * The group always contains active entities and listens
 * for changes in the game world.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityGroup(
        private val world: GameWorld,
        initialEntities: List<Entity>,
        vararg entityTypes: Enum<*>

) : EntityWorldListener, Disposable {

    private val types: List<Enum<*>> = entityTypes.toList()

    private val entities = ArrayList<Entity>(initialEntities)
    private val addList = ArrayList<Entity>()
    private val removeList = ArrayList<Entity>()

    /**
     * @return shallow copy of the entities list (new list)
     */
    val entitiesCopy: List<Entity>
        get(): List<Entity> {
            update()
            return entities.filter { it.isActive }
        }

    val size: Int
        get() = entitiesCopy.size

    init {
        world.addWorldListener(this)
    }

    fun forEach(action: (Entity) -> Unit) {
        entitiesCopy.forEach(action)
    }

    fun forEach(action: Consumer<Entity>) {
        entitiesCopy.forEach(action)
    }

    private fun update() {
        entities.addAll(addList)
        entities.removeAll(removeList)
        addList.clear()
        removeList.clear()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onEntityAdded(entity: Entity) {
        if (types.any { entity.isType(it) }) {
            addList.add(entity)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onEntityRemoved(entity: Entity) {
        if (types.any { entity.isType(it) }) {
            removeList.add(entity)
        }
    }

    override fun dispose() {
        world.removeWorldListener(this)

        entities.clear()
        addList.clear()
        removeList.clear()
    }
}