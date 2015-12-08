/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.event.EventBus;
import com.almasb.fxgl.event.UpdateEvent;
import com.almasb.fxgl.event.WorldEvent;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.util.FXGLLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Represents pure logical state of game.
 * Manages all entities and their state.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
public final class GameWorld {

    /**
     * The logger
     */
    protected static final Logger log = FXGLLogger.getLogger("FXGL.GameWorld");

    private ObjectProperty<GameDifficulty> gameDifficulty = new SimpleObjectProperty<>(GameDifficulty.MEDIUM);

    /**
     *
     * @return game difficulty
     */
    public GameDifficulty getGameDifficulty() {
        return gameDifficultyProperty().get();
    }

    /**
     *
     * @return game difficulty property
     */
    public ObjectProperty<GameDifficulty> gameDifficultyProperty() {
        return gameDifficulty;
    }

    private EventBus eventBus;

    @Inject
    private GameWorld(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.addEventHandler(UpdateEvent.ANY, event -> {
            update();
        });
        eventBus.addEventHandler(com.almasb.fxgl.event.FXGLEvent.RESET, event -> {
            reset();
        });

        log.finer("Game world initialized");
    }

    /**
     * List of entities currently in the world.
     */
    private List<Entity> entities = new ArrayList<>();

    /**
     * List of entities waiting to be added to game world.
     */
    private List<Entity> addQueue = new ArrayList<>();

    /**
     * List of entities waiting to be removed from game world.
     */
    private List<Entity> removeQueue = new ArrayList<>();

    /**
     * Places an entity in the queue to be added to the world.
     * The entity will be added to the world in the tick.
     *
     * @param entityToAdd the entity to add to world
     */
    public void addEntity(Entity entityToAdd) {
        addQueue.add(entityToAdd);
    }

    /**
     * Places entities in the queue to be added to the world.
     * The entities will be added to the world in the tick.
     *
     * @param entitiesToAdd the entities to add to world
     */
    public void addEntities(Entity... entitiesToAdd) {
        for (Entity e : entitiesToAdd) {
            addEntity(e);
        }
    }

    /**
     * Places an entity in the queue to be removed from the world.
     * The entity will be removed in the next tick.
     *
     * @param entityToRemove the entity to remove from world
     */
    public void removeEntity(Entity entityToRemove) {
        removeQueue.add(entityToRemove);
    }

    /**
     * Places entities in the queue to be removed from the world.
     * The entities will be removed in the next tick.
     *
     * @param entitiesToRemove the entity to remove from world
     */
    public void removeEntities(Entity... entitiesToRemove) {
        for (Entity e : entitiesToRemove) {
            removeEntity(e);
        }
    }

    private void registerAndInitPendingEntities() {
        entities.addAll(addQueue);
        addQueue.forEach(e -> {
            e.init(this);
            eventBus.fireEvent(WorldEvent.entityAdded(e));
        });
        addQueue.clear();
    }

    private void removeAndCleanPendingEntities() {
        entities.removeAll(removeQueue);
        removeQueue.forEach(e -> {
            eventBus.fireEvent(WorldEvent.entityRemoved(e));
            e.clean();
        });
        removeQueue.clear();
    }

    /**
     * Resets the world to its initial state.
     * Does NOT clear state listeners.
     * <p>
     * <ol>
     * <li>Registers waiting "add" entities</li>
     * <li>Removes waiting "remove" entities</li>
     * <li>Cleans and removes all entities</li>
     * </ol>
     */
    void reset() {
        log.finer("Resetting game world");

        registerAndInitPendingEntities();
        removeAndCleanPendingEntities();

        entities.forEach(Entity::clean);
        entities.clear();
    }

    /**
     * Performs a single world update tick.
     * <p>
     * <ol>
     * <li>Registers waiting "add" entities</li>
     * <li>Removes waiting "remove" entities</li>
     * <li>Notifies state listeners of update event.</li>
     * <li>Updates all entities</li>
     * </ol>
     */
    void update() {
        //log.finer("Game world update");

        registerAndInitPendingEntities();
        removeAndCleanPendingEntities();

        entities.forEach(Entity::update);
    }

    /**
     * Returns a list of entities whose type matches given
     * arguments. If no arguments were given, returns list
     * of ALL entities currently registered in the scene graph.
     *
     * @param types entity types to select
     * @return list of entities with types
     */
    public List<Entity> getEntities(EntityType... types) {
        if (types.length == 0)
            return new ArrayList<>(entities);

        List<String> list = Arrays.asList(types).stream()
                .map(EntityType::getUniqueType)
                .collect(Collectors.toList());

        return entities.stream()
                .filter(entity -> list.contains(entity.getTypeAsString()))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of entities which are filtered by
     * given predicate.
     *
     * @param predicate filter
     * @return list of entities that satisfy given predicate
     */
    public List<Entity> getEntitiesFiltered(Predicate<Entity> predicate) {
        return entities.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of entities whose type matches given arguments and
     * which are partially or entirely
     * in the specified rectangular selection.
     *
     * If no arguments were given, a list of all entities satisfying the
     * requirement is returned.
     *
     * @param selection Rectangle2D that describes the selection box
     * @param types entity types
     * @return  list of entities in the range
     */
    public List<Entity> getEntitiesInRange(Rectangle2D selection, EntityType... types) {
        Entity boundsEntity = Entity.noType();
        boundsEntity.addHitBox(new HitBox("__RANGE__",
                new BoundingBox(selection.getMinX(), selection.getMinY(), selection.getWidth(), selection.getHeight())));

        return getEntities(types).stream()
                .filter(entity -> entity.isCollidingWith(boundsEntity))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of entities whose type matches given arguments and
     * which have the given render layer index
     *
     * If no arguments were given, a list of all entities satisfying the
     * requirement (i.e. render layer) is returned.
     *
     * @param layer render layer
     * @param types entity types
     * @return  list of entities in the layer
     */
    public List<Entity> getEntitiesByLayer(RenderLayer layer, EntityType... types) {
        return getEntities(types).stream()
                .filter(e -> e.getSceneView().isPresent())
                .filter(e -> e.getSceneView().get().getRenderLayer().index() == layer.index())
                .collect(Collectors.toList());
    }

    /**
     * Returns the closest entity to the given entity with given type.
     * If no types were specified, the closest entity is returned. The given
     * entity itself is never returned.
     * <p>
     * If there no entities satisfying the requirement, {@link Optional#empty()}
     * is returned.
     *
     * @param entity selected entity
     * @param types  entity types
     * @return closest entity to selected entity with type
     */
    public Optional<Entity> getClosestEntity(Entity entity, EntityType... types) {
        return getEntities(types).stream()
                .filter(e -> e != entity)
                .sorted((e1, e2) -> (int) e1.distance(entity) - (int) e2.distance(entity))
                .findFirst();
    }

    /**
     * Returns an entity at given position. The position x and y
     * must equal to entity's position x and y.
     * <p>
     * Returns {@link Optional#empty()} if no entity was found at
     * given position.
     *
     * @param position point in the world
     * @return entity at point
     */
    public Optional<Entity> getEntityAt(Point2D position) {
        return getEntities()
                .stream()
                .filter(e -> e.getPosition().equals(position))
                .findAny();
    }
}
