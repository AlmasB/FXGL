/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.entity.component.BoundingBoxComponent
import com.almasb.fxgl.entity.component.MainViewComponent
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
        val entityType = com.almasb.fxgl.entity.Entities.getType(entity)

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
        return entities.filter { it.getComponentUnsafe(BoundingBoxComponent::class.java)?.isWithin(selection) ?: false }
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
        val bbox = com.almasb.fxgl.entity.Entities.getBBox(entity)

        return entities.filter { it.getComponentUnsafe(BoundingBoxComponent::class.java)?.isCollidingWith(bbox) ?: false && it !== entity }
    }

    /**
     * Returns a list of entities which have the given render layer index.
     * This query only works on entities with MainViewComponent.

     * @param layer render layer
     * *
     * @return list of entities in the layer
     */
    fun getEntitiesByLayer(layer: com.almasb.fxgl.entity.RenderLayer): List<Entity> {
        return entities.filter {
            val view = it.getComponentUnsafe(MainViewComponent::class.java)

            if (view != null) {
                return@filter view.renderLayer.index() == layer.index()
            }

            return@filter false
        }
    }

//
//    /**
//     * Returns the closest entity to the given entity with given
//     * filter. The given
//     * entity itself is never returned.
//     *
//     *
//     * If there no entities satisfying the requirement, [Optional.empty]
//     * is returned.
//     * Warning: object allocation.
//
//     * @param entity selected entity
//     * *
//     * @param filter requirements
//     * *
//     * @return closest entity to selected entity with type
//     */
//    fun getClosestEntity(entity: Entity, filter: Predicate<Entity>): Optional<Entity> {
//        val array = Array<Entity>(false, 64)
//
//        for (e in getEntitiesByComponent(PositionComponent::class.java)) {
//            if (filter.test(e) && e !== entity) {
//                array.add(e)
//            }
//        }
//
//        if (array.size() == 0)
//            return Optional.empty<Entity>()
//
//        array.sort { e1, e2 -> (Entities.getPosition(e1).distance(Entities.getPosition(entity)) - Entities.getPosition(e2).distance(Entities.getPosition(entity))).toInt() }
//
//        return Optional.of(array.get(0))
//    }
//
//



//
//    /**
//     * Returns an entity at given position. The position x and y
//     * must equal to entity's position x and y.
//     *
//     *
//     * Returns [Optional.empty] if no entity was found at
//     * given position.
//     * This query only works on entities with PositionComponent.
//
//     * @param position point in the world
//     * *
//     * @return entity at point
//     */
//    fun getEntityAt(position: Point2D): Optional<Entity> {
//        for (e in getEntitiesByComponent(PositionComponent::class.java)) {
//            if (Entities.getPosition(e).value == position) {
//                return Optional.of(e)
//            }
//        }
//
//        return Optional.empty<Entity>()
//    }
//
//    /**
//     * Returns an entity whose IDComponent matches given name and id.
//     *
//     *
//     * Returns [Optional.empty] if no entity was found with such combination.
//     * This query only works on entities with IDComponent.
//
//     * @param name entity name
//     * *
//     * @param id entity id
//     * *
//     * @return entity that matches the query or [Optional.empty]
//     */
//    fun getEntityByID(name: String, id: Int): Optional<Entity> {
//        for (e in getEntitiesByComponent(IDComponent::class.java)) {
//            val idComponent = e.getComponentUnsafe(IDComponent::class.java)
//            if (idComponent.name == name && idComponent.id == id) {
//                return Optional.of(e)
//            }
//        }
//
//        return Optional.empty<Entity>()
//    }
}
