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
package com.almasb.fxgl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.FXGLEvent;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.util.FXGLLogger;
import com.almasb.fxgl.util.WorldStateListener;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;

/**
 * Represents pure logical state of game.
 * Manages all entities and their state.
 * {@link com.almasb.fxgl.util.WorldStateListener} objects can be registered to listen
 * for various state changes.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public final class GameWorld {

    /**
     * The logger
     */
    protected static final Logger log = FXGLLogger.getLogger("FXGL.GameWorld");

    /**
     * Registered listeners.
     */
    private List<WorldStateListener> worldStateListeners = new CopyOnWriteArrayList<>();

    /**
     * Register a world state listener.
     *
     * @param listener  the listener to add
     */
    public void addWorldStateListener(WorldStateListener listener) {
        worldStateListeners.add(listener);
    }

    /**
     * Remove registered world state listener.
     *
     * @param listener  the listener to remove
     */
    public void removeWorldStateListener(WorldStateListener listener) {
        worldStateListeners.remove(listener);
    }

    /**
     * Hidden ctor.
     */
    /*package-private*/ GameWorld() {}

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
     * @param entityToAdd    the entity to add to world
     */
    public void addEntity(Entity entityToAdd) {
        addQueue.add(entityToAdd);
    }

    /**
     * Places entities in the queue to be added to the world.
     * The entities will be added to the world in the tick.
     *
     * @param entitiesToAdd      the entities to add to world
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
     * @param entityToRemove    the entity to remove from world
     */
    public void removeEntity(Entity entityToRemove) {
        removeQueue.add(entityToRemove);
    }

    /**
     * Places entities in the queue to be removed from the world.
     * The entities will be removed in the next tick.
     *
     * @param entitiesToRemove   the entity to remove from world
     */
    public void removeEntities(Entity... entitiesToRemove) {
        for (Entity e : entitiesToRemove) {
            removeEntity(e);
        }
    }

    private void registerAndInitPendingEntities() {
        entities.addAll(addQueue);
        addQueue.forEach(e -> {
            e.setWorld(this);
            e.init();
            worldStateListeners.forEach(l -> {
                l.onEntityAdded(e);
            });
        });
        addQueue.clear();
    }

    private void removeAndCleanPendingEntities() {
        entities.removeAll(removeQueue);
        removeQueue.forEach(e -> {
            worldStateListeners.forEach(l -> {
                l.onEntityRemoved(e);
            });
            e.clean();
        });
        removeQueue.clear();
    }

    /**
     * Resets the world to its initial state.
     * Does NOT clear state listeners.
     *
     * <ol>
     * <li>Fires {@link WorldStateListener#onWorldReset()}</li>
     * <li>Registers waiting "add" entities</li>
     * <li>Removes waiting "remove" entities</li>
     * <li>Cleans and removes all entities</li>
     * </ol>
     */
    /*package-private*/ void reset() {
        log.finer("Resetting game world");

        worldStateListeners.forEach(WorldStateListener::onWorldReset);

        registerAndInitPendingEntities();
        removeAndCleanPendingEntities();

        entities.forEach(Entity::clean);
        entities.clear();
    }

    /**
     * Performs a single world update tick.
     *
     * <ol>
     * <li>Registers waiting "add" entities</li>
     * <li>Removes waiting "remove" entities</li>
     * <li>Notifies state listeners of update event.</li>
     * <li>Updates all entities</li>
     * </ol>
     */
    /*package-private*/ void update() {
        //log.finer("Game world update");

        registerAndInitPendingEntities();
        removeAndCleanPendingEntities();

        worldStateListeners.forEach(WorldStateListener::onWorldUpdate);
        entities.forEach(Entity::update);
    }

    /**
     * Returns a list of entities whose type matches given
     * arguments. If no arguments were given, returns list
     * of ALL entities currently registered in the scene graph.
     *
     * @param types      entity types to select
     * @return       list of entities with types
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
     * @param predicate  filter
     * @return   list of entities that satisfy given predicate
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
        boundsEntity.setPosition(selection.getMinX(), selection.getMinY());
        boundsEntity.setView(new Rectangle(selection.getWidth(), selection.getHeight()));

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
                .filter(e -> e.getRenderLayer().index() == layer.index())
                .collect(Collectors.toList());
    }

    /**
     * Returns the closest entity to the given entity with given type.
     * If no types were specified, the closest entity is returned. The given
     * entity itself is never returned.
     *
     * If there no entities satisfying the requirement, {@link Optional#empty()}
     * is returned.
     *
     * @param entity    selected entity
     * @param types entity types
     * @return  closest entity to selected entity with type
     */
    public Optional<Entity> getClosestEntity(Entity entity, EntityType... types) {
        return getEntities(types).stream()
                .filter(e -> e != entity)
                .sorted((e1, e2) -> (int)e1.distance(entity) - (int)e2.distance(entity))
                .findFirst();
    }

    /**
     * Returns an entity at given position. The position x and y
     * must equal to entity's position x and y.
     *
     * Returns {@link Optional#empty()} if no entity was found at
     * given position.
     *
     * @param position  point in the world
     * @return  entity at point
     */
    public Optional<Entity> getEntityAt(Point2D position) {
        return getEntities()
                .stream()
                .filter(e -> e.getPosition().equals(position))
                .findAny();
    }

    /**
     * Fires an FXGL event on all entities whose type
     * matches given arguments. If types were not given,
     * fires an FXGL event on all entities registered in the scene graph.
     *
     * @param event the event
     * @param types entity types
     */
    public void fireFXGLEvent(FXGLEvent event, EntityType... types) {
        getEntities(types).forEach(e -> e.fireFXGLEvent(event));
    }
}
