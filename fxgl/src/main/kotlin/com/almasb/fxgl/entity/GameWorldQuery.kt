/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import java.util.*
import java.util.function.Predicate

/**
 * Represents pure logical state of game.
 * Manages all entities and their state.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
internal class GameWorldQuery(private val entities: List<Entity>) {

    /**
     * Returns a list of entities which are filtered by
     * given predicate.
     * Warning: object allocation.
     *
     * @param predicate filter
     * @return new list containing entities that satisfy query filters
     */
    fun getEntitiesFiltered(predicate: Predicate<Entity>): List<Entity> {
        return entities.filter({ predicate.test(it) })
    }

    /**
     * If called with no arguments, all entities are returned.
     * Warning: object allocation.
     *
     * @param types entity types
     * @return new list containing entities that satisfy query filters
     */
    fun getEntitiesByType(vararg types: Enum<*>): List<Entity> {
        if (types.isEmpty())
            return ArrayList<Entity>(entities);

        return entities.filter { isOneOfTypes(it, *types) }
    }

    private fun isOneOfTypes(entity: Entity, vararg types: Enum<*>): Boolean {
        return types.any { entity.isType(it) }
    }

    /**
     * Returns a list of entities
     * which are partially or entirely
     * in the specified rectangular selection.
     * Warning: object allocation.

     * @param selection Rectangle2D that describes the selection box
     * *
     * @return list of entities in the range
     */
    fun getEntitiesInRange(selection: Rectangle2D): List<Entity> {
        return entities.filter { it.boundingBoxComponent.isWithin(selection) }
    }

    /**
     * Returns a list of entities
     * which colliding with given entity.
     *
     * Note: CollidableComponent is not considered.
     *
     * @param entity the entity
     * @return list of entities colliding with entity
     */
    fun getCollidingEntities(entity: Entity): List<Entity> {
        return entities.filter { it.isColliding(entity) && it !== entity }
    }

    /**
     * Returns a list of entities which have the given render layer index.
     *
     * @param layer render layer
     * @return list of entities in the layer
     */
    fun getEntitiesByLayer(layer: RenderLayer): List<Entity> {
        return entities.filter { it.renderLayer.index() == layer.index() }
    }

    fun getEntitiesAt(position: Point2D): List<Entity> {
        return entities.filter { it.position == position }
    }
}
