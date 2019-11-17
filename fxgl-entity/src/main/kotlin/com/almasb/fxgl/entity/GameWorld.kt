/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.core.collection.Array
import com.almasb.fxgl.core.collection.UnorderedArray
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.core.util.Predicate
import com.almasb.fxgl.core.util.tryCatchRoot
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.components.IDComponent
import com.almasb.fxgl.entity.components.IrremovableComponent
import com.almasb.fxgl.entity.components.TimeComponent
import com.almasb.fxgl.entity.level.Level
import com.almasb.sslogger.Logger
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import java.util.*
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList

/**
 * Represents pure logical state of the game.
 * Manages all entities and allows queries.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class GameWorld {

    companion object {
        private val log = Logger.get("GameWorld")
    }

    private val updateList = Array<Entity>()

    /**
     * List of entities added to the update list in the next tick.
     */
    private val waitingList = UnorderedArray<Entity>()

    /**
     * List of entities in the world.
     *
     * @return direct list of entities in the world (do NOT modify)
     */
    val entities = ArrayList<Entity>()

    /**
     * @return shallow copy of the entities list (new list)
     */
    val entitiesCopy: List<Entity>
        get(): List<Entity> = ArrayList(entities)

    init {
        log.debug("Game world initialized")
    }

    /**
     * The entity will be added to update list in the next tick.
     *
     * @param entity the entity to add to world
     */
    fun addEntity(entity: Entity) {
        require(!entity.isActive){ "Entity is already attached to world" }

        waitingList.add(entity)
        entities.add(entity)

        add(entity)
    }

    fun addEntities(vararg entitiesToAdd: Entity) {
        for (e in entitiesToAdd) {
            addEntity(e)
        }
    }

    private fun add(entity: Entity) {
        entity.init(this)
        notifyEntityAdded(entity)
    }

    /**
     * Entity will be removed from world and parties notified of removal in the same frame.
     * The components and controls of entity will be removed in the next frame to avoid
     * concurrency issues.
     */
    fun removeEntity(entity: Entity) {
        if (!entity.isActive) {
            log.warning("Attempted to remove entity which is not active")
            return
        }

        if (!canRemove(entity))
            return

        require(entity.world === this) { "Attempted to remove entity not attached to this world" }

        entities.remove(entity)

        entity.markForRemoval()
        notifyEntityRemoved(entity)

        // we cannot clean entities here because this may have been called through a control
        // while entity is being updated
        // so, we clean the entity on next frame
    }

    fun removeEntities(vararg entitiesToRemove: Entity) {
        for (e in entitiesToRemove) {
            removeEntity(e)
        }
    }

    fun removeEntities(entitiesToRemove: Collection<Entity>) {
        for (e in entitiesToRemove) {
            removeEntity(e)
        }
    }

    private fun canRemove(entity: Entity): Boolean {
        return !entity.hasComponent(IrremovableComponent::class.java)
    }

    /**
     * Performs a single world update tick.
     *
     * @param tpf time per frame
     */
    fun onUpdate(tpf: Double) {
        updateList.addAll(waitingList)
        waitingList.clear()

        val it = updateList.iterator()
        while (it.hasNext()) {
            val e = it.next()

            if (!e.isActive) {
                // clean entities removed in the last frame
                e.clean()
                it.remove()
            } else {
                val tpfRatio = e.getComponentOptional(TimeComponent::class.java)
                        .map { it.value }
                        .orElse(1.0)

                e.update(tpf * tpfRatio)
            }
        }
    }

    /**
     * Removes all (including with IrremovableComponent) entities.
     * Does NOT clear state listeners.
     * Do NOT call this method manually.
     * It is called automatically by FXGL during initGame().
     */
    fun clear() {
        log.debug("Clearing game world")

        waitingList.clear()

        // we may still have some entities that have been removed but not yet cleaned
        run {
            val it = updateList.iterator()
            while (it.hasNext()) {
                val e = it.next()

                if (!e.isActive) {
                    e.clean()
                }

                it.remove()
            }
        }

        // entities list does not contain "not active" entities, so we do full clean
        // also we copy since during removal notification components may remove other entities
        entitiesCopy.forEach { e ->
            e.markForRemoval()
            notifyEntityRemoved(e)
            e.clean()
        }

        entities.clear()
        entityFactories.clear()
        entitySpawners.clear()
    }

    private val worldListeners = Array<EntityWorldListener>()

    fun addWorldListener(listener: EntityWorldListener) {
        worldListeners.add(listener)
    }

    fun removeWorldListener(listener: EntityWorldListener) {
        worldListeners.removeValueByIdentity(listener)
    }

    private fun notifyEntityAdded(e: Entity) {
        worldListeners.forEach { it.onEntityAdded(e) }
    }

    private fun notifyEntityRemoved(e: Entity) {
        worldListeners.forEach { it.onEntityRemoved(e) }
    }

    /**
     * Set level to given.
     * Resets the world.
     * Adds all level entities to the world.
     *
     * @param level the level
     */
    fun setLevel(level: Level) {
        clearLevel()

        log.debug("Setting level: $level")
        level.entities.forEach { addEntity(it) }
    }

    /**
     * Removes removable entities.
     */
    private fun clearLevel() {
        log.debug("Clearing removable entities")

        waitingList.clear()

        // we may still have some entities that have been removed but not yet cleaned
        // but we do not want to remove Irremovables
        run {
            val it = updateList.iterator()
            while (it.hasNext()) {
                val e = it.next()

                if (canRemove(e)) {
                    if (!e.isActive) {
                        // clean it here because "entities" list does not have "e"
                        e.clean()
                    }

                    it.remove()
                }
            }
        }

        // entities list does not contain "not active" entities, so we do full clean
        // but we do not want to remove Irremovables
        val it = entities.iterator()
        while (it.hasNext()) {
            val e = it.next()

            if (canRemove(e)) {
                e.markForRemoval()
                notifyEntityRemoved(e)
                e.clean()

                it.remove()
            }
        }
    }

    private val entityFactories = hashMapOf<EntityFactory, List<String>>()

    /**
     * Maps entity spawn name to the method that creates the entity.
     */
    private val entitySpawners = hashMapOf<String, EntitySpawner>()

    /**
     * @param entityFactory factory for creating entities
     */
    fun addEntityFactory(entityFactory: EntityFactory) {
        val entityNames = arrayListOf<String>()

        ReflectionUtils.findMethodsMapToFunctions(entityFactory, Spawns::class.java, EntitySpawner::class.java)
                .forEach { annotation, entitySpawner ->

                    val entityAliases = annotation.value.split(",".toRegex())
                    entityAliases.forEach { entityName ->
                        checkDuplicateSpawners(entityFactory, entityName)

                        entitySpawners.put(entityName, entitySpawner)
                        entityNames.add(entityName)
                    }
                }

//        ReflectionUtils.findMethods(entityFactory, Preload::class.java)
//                .forEach { preload, method ->
//
//                    val ann = method.getDeclaredAnnotation(Spawns::class.java)
//                    val entityAliases = ann.value.split(",".toRegex())
//
//                    val numToPreload = preload.value
//
//                    entityPreloader.addForPreloading(entityAliases, numToPreload)
//                }

        entityFactories.put(entityFactory, entityNames)
    }

    private fun checkDuplicateSpawners(entityFactory: EntityFactory, entityName: String) {
        if (entitySpawners.containsKey(entityName)) {

            // find the factory that already has entityName spawner
            val factory = entityFactories.entries.find { entityName in it.value }

            throw IllegalArgumentException("Duplicated @Spawns($entityName) in $entityFactory. Already exists in $factory")
        }
    }

    fun removeEntityFactory(entityFactory: EntityFactory) {
        entityFactories.remove(entityFactory)?.forEach {
            entitySpawners.remove(it)
        }
    }

    /**
     * Creates an entity with given name at 0, 0 using a previously added entity factory.
     * Adds created entity to this game world.
     *
     * @param entityName name of entity as specified by [Spawns]
     * @return spawned entity
     */
    fun spawn(entityName: String): Entity {
        return spawn(entityName, 0.0, 0.0)
    }

    /**
     * Creates an entity with given name at x, y using a previously added entity factory.
     * Adds created entity to this game world.
     *
     * @param entityName name of entity as specified by [Spawns]
     * @param position spawn location
     * @return spawned entity
     */
    fun spawn(entityName: String, position: Point2D): Entity {
        return spawn(entityName, position.x, position.y)
    }

    /**
     * Creates an entity with given name at x, y using a previously added entity factory.
     * Adds created entity to this game world.
     *
     * @param entityName name of entity as specified by [Spawns]
     * @param x x position
     * @param y y position
     * @return spawned entity
     */
    fun spawn(entityName: String, x: Double, y: Double): Entity {
        return spawn(entityName, SpawnData(x, y))
    }

    /**
     * Creates an entity with given name and data using a previously added entity factory.
     * Adds created entity to this game world.
     *
     * @param entityName name of entity as specified by [Spawns]
     * @param data spawn data, such as x, y and any extra info
     * @return spawned entity
     */
    fun spawn(entityName: String, data: SpawnData): Entity {
        val entity = create(entityName, data)
        addEntity(entity)
        return entity
    }

    /**
     * Creates an entity with given name and data using a previously added entity factory.
     * Does NOT add created entity to the game world.
     *
     * @param entityName name of entity as specified by [Spawns]
     * @param data spawn data, such as x, y and any extra info
     * @return created entity
     */
    fun create(entityName: String, data: SpawnData): Entity {
        check(entityFactories.isNotEmpty()) { "No EntityFactory was added! Call gameWorld.addEntityFactory()" }

        val spawner = entitySpawners.get(entityName)
                ?: throw IllegalArgumentException("No EntityFactory has a method annotated @Spawns($entityName)")

        if (!data.hasKey("type")) {
            data.put("type", entityName)
        }

//        if (entityPreloader.isPreloadingEnabled(entityName)) {
//            return entityPreloader.obtain(entityName, data)
//        }

        return tryCatchRoot { spawner.apply(data) }
    }

    /* QUERIES */

    fun getSingleton(type: Enum<*>): Entity {
        return getSingleton(Predicate { it.isType(type) })
    }

    fun getSingleton(predicate: Predicate<Entity>): Entity {
        return entities.find { predicate.test(it) } ?: throw NoSuchElementException("No entity found satisfying the predicate")
    }

    /**
     * Useful for singleton type entities, e.g. Player.
     *
     * @return first occurrence matching given type
     */
    fun getSingletonOptional(type: Enum<*>): Optional<Entity> {
        return getSingletonOptional(Predicate { it.isType(type) })
    }

    /**
     * @return first occurrence matching given predicate
     */
    fun getSingletonOptional(predicate: Predicate<Entity>): Optional<Entity> {
        return Optional.ofNullable(entities.find { predicate.test(it) })
    }

    /**
     * @param type entity type
     * @return a random entity with given type
     */
    fun getRandom(type: Enum<*>): Optional<Entity> {
        return FXGLMath.random(getEntitiesByType(type))
    }

    fun getRandom(predicate: Predicate<Entity>): Optional<Entity> {
        return FXGLMath.random(getEntitiesFiltered(predicate))
    }

    /**
     * @param type component type
     * @return array of entities that have given component
     */
    fun getEntitiesByComponent(type: Class<out Component>): List<Entity> {
        return entities.filter { it.hasComponent(type) }
    }

    /**
     * Returns a list of entities which are filtered by
     * given predicate.
     *
     * @param predicate filter
     * @return new list containing entities that satisfy query filters
     */
    fun getEntitiesFiltered(predicate: Predicate<Entity>): List<Entity> {
        return entities.filter { predicate.test(it) }
    }

    /**
     * If called with no arguments, all entities are returned.
     *
     * @param types entity types
     * @return new list containing entities that satisfy query filters
     */
    fun getEntitiesByType(vararg types: Enum<*>): List<Entity> {
        if (types.isEmpty())
            return entitiesCopy

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
     *
     * @param selection Rectangle2D that describes the selection box
     * @return new list containing entities that satisfy query filters
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
     * @return new list containing entities that satisfy query filters
     */
    fun getCollidingEntities(entity: Entity): List<Entity> {
        return entities.filter { it.isColliding(entity) && it !== entity }
    }

    /**
     * Returns a list of entities at given position.
     * The position x and y must equal to entity's position x and y.
     *
     * @param position point in the world
     * @return entities at given point
     */
    fun getEntitiesAt(position: Point2D): List<Entity> {
        return entities.filter { it.position == position }
    }

    /**
     * Returns the closest entity to the given entity with given
     * filter. The given
     * entity itself is never returned.
     *
     * If there no entities satisfying the requirement, [Optional.empty]
     * is returned.
     *
     * @param entity selected entity
     * @param filter requirements
     * @return closest entity to selected entity with type
     */
    fun getClosestEntity(entity: Entity, filter: Predicate<Entity>): Optional<Entity> {
        return Optional.ofNullable(
                entities.filter { filter.test(it) && it !== entity }
                        .sortedBy { entity.distance(it) }
                        .firstOrNull()
        )
    }

    /**
     * Returns an entity whose IDComponent matches given name and id.
     *
     *
     * Returns [Optional.empty] if no entity was found with such combination.
     * This query only works on entities with IDComponent.
     *
     * @param name entity name
     * @param id entity id
     * @return entity that matches the query or [Optional.empty]
     */
    fun getEntityByID(name: String, id: Int): Optional<Entity> {
        return Optional.ofNullable(
                getEntitiesByComponent(IDComponent::class.java).find {
                    val idComponent = it.getComponent(IDComponent::class.java)

                    return@find idComponent.name == name && idComponent.id == id
                }
        )
    }
}