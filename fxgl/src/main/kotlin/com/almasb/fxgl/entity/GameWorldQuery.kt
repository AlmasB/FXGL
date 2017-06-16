/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.entity.component.BoundingBoxComponent
import com.almasb.fxgl.entity.component.PositionComponent
import com.almasb.fxgl.entity.component.ViewComponent
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
     * This query only works on entities with TypeComponent.
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
        val entityType = Entities.getType(entity)

        if (entityType != null)
            return types.any { entityType.isType(it) }

        return false
    }

    /**
     * Returns a list of entities
     * which are partially or entirely
     * in the specified rectangular selection.
     * This query only works on entities with BoundingBoxComponent.
     * Warning: object allocation.

     * @param selection Rectangle2D that describes the selection box
     * *
     * @return list of entities in the range (do NOT modify)
     */
    fun getEntitiesInRange(selection: Rectangle2D): List<Entity> {
        return entities.filter { it.getComponent(BoundingBoxComponent::class.java)?.isWithin(selection) ?: false }
    }


    /**
     * Returns a list of entities
     * which colliding with given entity.

     * Note: CollidableComponent is not considered.
     * This query only works on entities with BoundingBoxComponent.

     * @param entity the entity
     * *
     * @return list of entities colliding with entity
     */
    fun getCollidingEntities(entity: Entity): List<Entity> {
        val bbox = Entities.getBBox(entity)

        return entities.filter { it.getComponent(BoundingBoxComponent::class.java)?.isCollidingWith(bbox) ?: false && it !== entity }
    }

    /**
     * Returns a list of entities which have the given render layer index.
     * This query only works on entities with ViewComponent.

     * @param layer render layer
     * *
     * @return list of entities in the layer
     */
    fun getEntitiesByLayer(layer: RenderLayer): List<Entity> {
        return entities.filter {
            val view = it.getComponent(ViewComponent::class.java)

            if (view != null) {
                return@filter view.renderLayer.index() == layer.index()
            }

            return@filter false
        }
    }

    fun getEntitiesAt(position: Point2D): List<Entity> {
        return entities.filter {
            val p = it.getComponent(PositionComponent::class.java)?.value ?: return@filter false

            p == position
        }
    }
}
