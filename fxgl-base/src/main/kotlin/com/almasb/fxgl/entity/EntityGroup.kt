/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.core.Disposable
import com.almasb.fxgl.core.collection.Array
import com.almasb.fxgl.util.Consumer
import com.almasb.fxgl.util.Predicate

/**
 * A group of entities of particular types.
 * The group always contains active entities and listens
 * for changes in the game world.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityGroup<T : Entity>(

        private val world: GameWorld,
        initialEntities: List<T>,
        vararg entityTypes: Enum<*>

) : EntityWorldListener, Disposable {

    private val types: List<Enum<*>> = entityTypes.toList()

    private val entities = Array<T>(initialEntities)
    private val addList = Array<T>()
    private val removeList = Array<T>()

    init {
        world.addWorldListener(this)
    }

    fun forEach(action: Consumer<T>) {
        update()

        entities.forEach {
            if (it.isActive) {
                action.accept(it)
            }
        }
    }

    fun forEach(filter: Predicate<T>, action: Consumer<T>) {
        update()

        entities.filter { filter.test(it) }.forEach {
            if (it.isActive) {
                action.accept(it)
            }
        }
    }

    private fun update() {
        entities.addAll(addList)
        entities.removeAllByIdentity(removeList)
        addList.clear()
        removeList.clear()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onEntityAdded(entity: Entity) {
        if (types.any { entity.isType(it) }) {
            addList.add(entity as T)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onEntityRemoved(entity: Entity) {
        if (types.any { entity.isType(it) }) {
            removeList.add(entity as T)
        }
    }

    override fun dispose() {
        world.removeWorldListener(this)

        entities.clear()
        addList.clear()
        removeList.clear()
    }
}