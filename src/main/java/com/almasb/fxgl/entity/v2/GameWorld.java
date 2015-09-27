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
package com.almasb.fxgl.entity.v2;

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
import com.almasb.fxgl.util.UpdateTickListener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;

public final class GameWorld {

    /**
     * The logger
     */
    protected static final Logger log = FXGLLogger.getLogger("FXGL.GameWorld");

    public GameWorld() {

    }

    /**
     * List of entities currently in the world.
     */
    private ObservableList<Entity> entities = FXCollections.observableArrayList();

    public ObservableList<Entity> entitiesProperty() {
        return FXCollections.unmodifiableObservableList(entities);
    }

    /**
     * List of entities waiting to be added to game world.
     */
    private List<Entity> addQueue = new ArrayList<>();

    /**
     * List of entities waiting to be removed from game world.
     */
    private List<Entity> removeQueue = new ArrayList<>();

    public void addEntity(Entity entityToAdd) {
        addQueue.add(entityToAdd);
    }

    public void addEntities(Entity... entitiesToAdd) {
        for (Entity e : entitiesToAdd) {
            addQueue.add(e);
        }
    }

    public void removeEntity(Entity entityToRemove) {
        removeQueue.add(entityToRemove);
    }

    public void removeEntities(Entity... entitiesToRemove) {
        for (Entity e : entitiesToRemove) {
            removeQueue.add(e);
        }
    }

    /**
     * Returns a list of entities whose type matches given
     * arguments. If no arguments were given, returns list
     * of ALL entities currently registered in the scene graph.
     *
     * @param types
     * @return
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
     * given predicate
     *
     * @param predicate - filter
     * @return  list of entities that satisfy given predicate
     */
    public List<Entity> getEntities(Predicate<Entity> predicate) {
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
     * @param types
     * @return
     */
    public List<Entity> getEntitiesInRange(Rectangle2D selection, EntityType... types) {
        Entity boundsEntity = Entity.noType();
        boundsEntity.setPosition(selection.getMinX(), selection.getMinY());
        boundsEntity.setGraphics(new Rectangle(selection.getWidth(), selection.getHeight()));

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
     * @param layer
     * @param types
     * @return
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
     * @param entity
     * @param types
     * @return
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
     * @param position
     * @return
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
     * @param event
     * @param types
     */
    public void fireFXGLEvent(FXGLEvent event, EntityType... types) {
        getEntities(types).forEach(e -> e.fireFXGLEvent(event));
    }

    private List<UpdateTickListener> listeners = new CopyOnWriteArrayList<>();

    public void addUpdateTickListener(UpdateTickListener listener) {
        listeners.add(listener);
    }

    public void removeUpdateTickListener(UpdateTickListener listener) {
        listeners.remove(listener);
    }

    private void registerPendingEntities() {
        entities.addAll(addQueue);
        addQueue.forEach(Entity::init);
        addQueue.clear();
    }

    private void removeAndCleanPendingEntities() {
        entities.removeAll(removeQueue);
        removeQueue.forEach(Entity::clean);
        removeQueue.clear();
    }

    public void destroy() {
        registerPendingEntities();
        removeAndCleanPendingEntities();

        entities.forEach(Entity::clean);
        entities.clear();
    }

    public void update() {
        registerPendingEntities();
        removeAndCleanPendingEntities();

        listeners.forEach(UpdateTickListener::onUpdate);
        entities.forEach(Entity::update);
    }
}
