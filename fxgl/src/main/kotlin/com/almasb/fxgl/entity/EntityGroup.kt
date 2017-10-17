/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.core.Disposable
import com.almasb.fxgl.core.collection.Array
import com.almasb.fxgl.core.collection.Predicate
import com.almasb.fxgl.entity.component.TypeComponent
import java.util.function.Consumer

/**
 * A group of entities of particular types.
 * The group always contains active entities and listens
 * for changes in the game world.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityGroup<T : Entity>(private val world: GameWorld, initialEntities: List<T>, vararg entityTypes: Enum<*>) : EntityWorldListener, Disposable {

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

        entities.filter { filter.evaluate(it) }.forEach {
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

    override fun onEntityAdded(entity: Entity) {
        entity.getComponentOptional(TypeComponent::class.java).ifPresent { component ->
            val isType = types.any { component.isType(it) }

            if (isType) {
                addList.add(entity as T)
            }
        }
    }

    override fun onEntityRemoved(entity: Entity) {
        entity.getComponentOptional(TypeComponent::class.java).ifPresent { component ->
            val isType = types.any { component.isType(it) }

            if (isType) {
                removeList.add(entity as T)
            }
        }
    }

    override fun dispose() {
        world.removeWorldListener(this)

        entities.clear()
        addList.clear()
        removeList.clear()
    }
}