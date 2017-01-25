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

package com.almasb.fxgl.gameplay;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.fxgl.core.reflect.ReflectionUtils;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.EntityWorld;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.*;
import com.almasb.fxgl.event.EventTrigger;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.time.UpdateEvent;
import com.almasb.fxgl.time.UpdateEventListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents pure logical state of game.
 * Manages all entities and their state.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
public final class GameWorld extends EntityWorld implements UpdateEventListener {

    private static final Logger log = FXGL.getLogger("FXGL.GameWorld");

    private ObjectProperty<GameDifficulty> gameDifficulty = new SimpleObjectProperty<>(GameDifficulty.MEDIUM);

    /**
     * @return game difficulty
     */
    public GameDifficulty getGameDifficulty() {
        return gameDifficultyProperty().get();
    }

    /**
     * @return game difficulty property
     */
    public ObjectProperty<GameDifficulty> gameDifficultyProperty() {
        return gameDifficulty;
    }

    private Array<EventTrigger<?> > eventTriggers = new Array<>(false, 32);

    @Inject
    protected GameWorld() {
        log.debug("Game world initialized");
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        update(event.tpf());
        updateTriggers(event);
    }

    private void updateTriggers(UpdateEvent event) {
        for (Iterator<EventTrigger<?> > it = eventTriggers.iterator(); it.hasNext(); ) {
            EventTrigger trigger = it.next();

            trigger.onUpdateEvent(event);

            if (trigger.reachedLimit()) {
                it.remove();
            }
        }
    }

    @Override
    public void reset() {
        log.debug("Resetting game world");
        super.reset();
    }

    /**
     * Add event trigger to the world.
     *
     * @param trigger the event trigger
     */
    public void addEventTrigger(EventTrigger<?> trigger) {
        eventTriggers.add(trigger);
    }

    /**
     * Remove event trigger from the world.
     *
     * @param trigger the event trigger
     */
    public void removeEventTrigger(EventTrigger<?> trigger) {
        eventTriggers.removeValue(trigger, true);
    }

    private ObjectProperty<Entity> selectedEntity = new SimpleObjectProperty<>();

    /**
     * Returns last selected (clicked on by mouse) entity.
     *
     * @return selected entity
     */
    public Optional<Entity> getSelectedEntity() {
        return Optional.ofNullable(selectedEntity.get());
    }

    /**
     * @return selected entity property
     */
    public ObjectProperty<Entity> selectedEntityProperty() {
        return selectedEntity;
    }

    /**
     * Set level to given.
     * Resets the world.
     * Adds all level entities to the world.
     *
     * @param level the level
     */
    public void setLevel(Level level) {
        reset();

        log.debug("Setting level: " + level);
        level.getEntities().forEach(this::addEntity);
    }

    private EntityFactory entityFactory = null;

    private ObjectMap<String, Function<SpawnData, Entity>> entitySpawners = new ObjectMap<>();

    /**
     * @return entity factory or null if not set
     */
    @SuppressWarnings("unchecked")
    public <T extends EntityFactory> T getEntityFactory() {
        return (T) entityFactory;
    }

    /**
     * Set main entity factory to be used via {@link GameWorld#spawn(String, SpawnData)}.
     *
     * @param entityFactory factory for creating entities
     */
    public void setEntityFactory(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;

        entitySpawners.clear();

        ReflectionUtils.<SpawnData, Entity, Spawns>findMethodsMapFunctions(entityFactory, Spawns.class)
                .forEach((annotation, spawnerFunction) -> entitySpawners.put(annotation.value(), spawnerFunction));
    }

    /**
     * Creates an entity with given name at x, y using specified entity factory.
     * Adds created entity to this game world.
     *
     * @param entityName name of entity as specified by {@link Spawns}
     * @param position spawn location
     * @return spawned entity
     */
    public Entity spawn(String entityName, Point2D position) {
        return spawn(entityName, position.getX(), position.getY());
    }

    /**
     * Creates an entity with given name at x, y using specified entity factory.
     * Adds created entity to this game world.
     *
     * @param entityName name of entity as specified by {@link Spawns}
     * @param x x position
     * @param y y position
     * @return spawned entity
     */
    public Entity spawn(String entityName, double x, double y) {
        return spawn(entityName, new SpawnData(x, y));
    }

    /**
     * Creates an entity with given name and data using specified entity factory.
     * Adds created entity to this game world.
     *
     * @param entityName name of entity as specified by {@link Spawns}
     * @param data spawn data, such as x, y and any extra info
     * @return spawned entity
     */
    public Entity spawn(String entityName, SpawnData data) {
        if (entityFactory == null)
            throw new IllegalStateException("EntityFactory was not set!");

        Function<SpawnData, Entity> spawner = entitySpawners.get(entityName);
        if (spawner == null)
            throw new IllegalArgumentException("EntityFactory does not have a method annotated @Spawns(" + entityName + ")");

        Entity entity = spawner.apply(data);
        addEntity(entity);
        return entity;
    }

    /* QUERIES */

    private GameWorldQuery query = new GameWorldQuery(entities);

    /**
     * Returns a list of entities which are filtered by
     * given predicate.
     * Warning: object allocation.
     *
     * @param predicate filter
     * @return new list containing entities that satisfy query filters
     */
    public List<Entity> getEntitiesFiltered(Predicate<Entity> predicate) {
        return query.getEntitiesFiltered(predicate);
    }

    /**
     * GC-friendly version of {@link #getEntitiesFiltered(Predicate)}.
     *
     * @param result the array to collect entities
     * @param predicate filter
     */
    public void getEntitiesFiltered(Array<Entity> result, Predicate<Entity> predicate) {
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (predicate.test(e)) {
                result.add(e);
            }
        }
    }

    /**
     * This query only works on entities with TypeComponent.
     * If called with no arguments, all entities are returned.
     * Warning: object allocation.
     *
     * @param types entity types
     * @return new list containing entities that satisfy query filters
     */
    public List<Entity> getEntitiesByType(Enum<?>... types) {
        return query.getEntitiesByType(types);
    }

    /**
     * GC-friendly version of {@link #getEntitiesByType(Enum[])}.
     *
     * @param result the array to collect entities
     * @param types entity types
     */
    public void getEntitiesByType(Array<Entity> result, Enum<?>... types) {
        if (types.length == 0) {
            for (int i = 0; i < entities.size(); i++) {
                Entity e = entities.get(i);
                result.add(e);
            }
            return;
        }

        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (isOfType(e, types)) {
                result.add(e);
            }
        }
    }

    private boolean isOfType(Entity e, Enum<?>... types) {
        TypeComponent entityType = Entities.getType(e);
        if (entityType != null) {
            for (Enum<?> type : types) {
                if (entityType.isType(type)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns a list of entities
     * which are partially or entirely
     * in the specified rectangular selection.
     * This query only works on entities with BoundingBoxComponent.
     * Warning: object allocation.
     *
     * @param selection Rectangle2D that describes the selection box
     * @return new list containing entities that satisfy query filters
     */
    public List<Entity> getEntitiesInRange(Rectangle2D selection) {
        return query.getEntitiesInRange(selection);
    }

    /**
     * GC-friendly version of {@link #getEntitiesInRange(Rectangle2D)}.
     *
     * @param result the array to collect entities
     * @param minX min x
     * @param minY min y
     * @param maxX max x
     * @param maxY max y
     */
    public void getEntitiesInRange(Array<Entity> result, double minX, double minY, double maxX, double maxY) {
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            BoundingBoxComponent bbox = Entities.getBBox(e);
            if (bbox != null && bbox.isWithin(minX, minY, maxX, maxY)) {
                result.add(e);
            }
        }
    }

    /**
     * Returns a list of entities
     * which colliding with given entity.
     *
     * Note: CollidableComponent is not considered.
     * This query only works on entities with BoundingBoxComponent.
     *
     * @param entity the entity
     * @return new list containing entities that satisfy query filters
     */
    public List<Entity> getCollidingEntities(Entity entity) {
        return query.getCollidingEntities(entity);
    }

    /**
     * GC-friendly version of {@link #getCollidingEntities(Entity)}.
     *
     * @param result the array to collect entities
     * @param entity the entity
     */
    public void getCollidingEntities(Array<Entity> result, Entity entity) {
        BoundingBoxComponent entityBBox = Entities.getBBox(entity);

        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            BoundingBoxComponent bbox = Entities.getBBox(e);
            if (bbox != null && bbox.isCollidingWith(entityBBox) && e != entity) {
                result.add(e);
            }
        }
    }

    /**
     * Returns a list of entities which have the given render layer index.
     * This query only works on entities with ViewComponent.
     *
     * @param layer render layer
     * @return new list containing entities that satisfy query filters
     */
    public List<Entity> getEntitiesByLayer(RenderLayer layer) {
        return query.getEntitiesByLayer(layer);
    }

    /**
     * GC-friendly version of {@link #getEntitiesByLayer(RenderLayer)}.
     *
     * @param result the array to collect entities
     * @param layer render layer
     */
    public void getEntitiesByLayer(Array<Entity> result, RenderLayer layer) {
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);

            ViewComponent view = Entities.getView(e);

            if (view != null && view.getRenderLayer().index() == layer.index()) {
                result.add(e);
            }
        }
    }

    /**
     * Returns the closest entity to the given entity with given
     * filter. The given
     * entity itself is never returned.
     * <p>
     * If there no entities satisfying the requirement, {@link Optional#empty()}
     * is returned.
     * Warning: object allocation.
     *
     * @param entity selected entity
     * @param filter requirements
     * @return closest entity to selected entity with type
     */
    public Optional<Entity> getClosestEntity(Entity entity, Predicate<Entity> filter) {
        Array<Entity> array = new Array<>(false, 64);

        for (Entity e : getEntitiesByComponent(PositionComponent.class)) {
            if (filter.test(e) && e != entity) {
                array.add(e);
            }
        }

        if (array.size() == 0)
            return Optional.empty();

        array.sort((e1, e2) -> (int) (Entities.getPosition(e1).distance(Entities.getPosition(entity))
                        - Entities.getPosition(e2).distance(Entities.getPosition(entity))));

        return Optional.of(array.get(0));
    }

    /**
     * Returns an entity at given position. The position x and y
     * must equal to entity's position x and y.
     * <p>
     * Returns {@link Optional#empty()} if no entity was found at
     * given position.
     * This query only works on entities with PositionComponent.
     *
     * @param position point in the world
     * @return entity at point
     */
    public Optional<Entity> getEntityAt(Point2D position) {
        for (Entity e : getEntitiesByComponent(PositionComponent.class)) {
            if (Entities.getPosition(e).getValue().equals(position)) {
                return Optional.of(e);
            }
        }

        return Optional.empty();
    }

    /**
     * Returns an entity whose IDComponent matches given name and id.
     * <p>
     * Returns {@link Optional#empty()} if no entity was found with such combination.
     * This query only works on entities with IDComponent.
     *
     * @param name entity name
     * @param id entity id
     * @return entity that matches the query or {@link Optional#empty()}
     */
    public Optional<Entity> getEntityByID(String name, int id) {
        for (Entity e : getEntitiesByComponent(IDComponent.class)) {
            IDComponent idComponent = e.getComponentUnsafe(IDComponent.class);
            if (idComponent.getName().equals(name) && idComponent.getID() == id) {
                return Optional.of(e);
            }
        }

        return Optional.empty();
    }
}
