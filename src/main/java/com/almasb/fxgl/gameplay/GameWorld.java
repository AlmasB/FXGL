/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import com.almasb.ents.Entity;
import com.almasb.ents.EntityWorld;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.component.*;
import com.almasb.fxgl.time.UpdateEvent;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.time.UpdateEventListener;
import com.almasb.gameutils.collection.Array;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.xml.internal.bind.v2.model.core.ID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents pure logical state of game.
 * Manages all entities and their state.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
public final class GameWorld extends EntityWorld implements UpdateEventListener {

    /**
     * The logger
     */
    protected static final Logger log = FXGL.getLogger("FXGL.GameWorld");

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

    @Inject
    protected GameWorld() {
        log.debug("Game world initialized");
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
        update(event.tpf());
    }

    @Override
    public void reset() {
        log.debug("Resetting game world");
        super.reset();
    }

    /**
     * This query only works on entities with TypeComponent.
     * If called with no arguments, all entities are returned.
     * Warning: object allocation.
     *
     * @param types entity types
     * @return entities (do NOT modify)
     */
    public List<Entity> getEntitiesByType(Enum<?>... types) {
        if (types.length == 0)
            return getEntities();

        List<Entity> list = new ArrayList<>();

        for (Entity e : getEntitiesByComponent(TypeComponent.class)) {
            for (Enum<?> type : types) {
                if (Entities.getType(e).isType(type)) {
                    list.add(e);
                }
            }
        }

        return list;
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

        if (array.size == 0)
            return Optional.empty();

        array.sort((e1, e2) -> (int) (Entities.getPosition(e1).distance(Entities.getPosition(entity))
                        - Entities.getPosition(e2).distance(Entities.getPosition(entity))));

        return Optional.of(array.get(0));
    }

    /**
     * Returns a list of entities which are filtered by
     * given predicate.
     * Warning: object allocation.
     *
     * @param predicate filter
     * @return list of entities that satisfy given predicate (do NOT modify)
     */
    public List<Entity> getEntitiesFiltered(Predicate<Entity> predicate) {
        return entities.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of entities
     * which are partially or entirely
     * in the specified rectangular selection.
     * This query only works on entities with BoundingBoxComponent.
     * Warning: object allocation.
     *
     * @param selection Rectangle2D that describes the selection box
     * @return list of entities in the range (do NOT modify)
     */
    public List<Entity> getEntitiesInRange(Rectangle2D selection) {
        List<Entity> list = new ArrayList<>();

        for (Entity e : getEntitiesByComponent(BoundingBoxComponent.class)) {
            if (Entities.getBBox(e).isWithin(selection)) {
                list.add(e);
            }
        }

        return list;
    }

    /**
     * Returns a list of entities
     * which colliding with given entity.
     *
     * Note: CollidableComponent is not considered.
     * This query only works on entities with BoundingBoxComponent.
     *
     * @param entity the entity
     * @return list of entities colliding with entity
     */
    public List<Entity> getCollidingEntities(Entity entity) {
        BoundingBoxComponent bbox = Entities.getBBox(entity);

        List<Entity> list = new ArrayList<>();

        for (Entity e : getEntitiesByComponent(BoundingBoxComponent.class)) {
            if (Entities.getBBox(e).isCollidingWith(bbox) && e != entity) {
                list.add(e);
            }
        }

        return list;
    }

    /**
     * Returns a list of entities which have the given render layer index.
     * This query only works on entities with MainViewComponent.
     *
     * @param layer render layer
     * @return list of entities in the layer
     */
    public List<Entity> getEntitiesByLayer(RenderLayer layer) {
        List<Entity> list = new ArrayList<>();

        for (Entity e : getEntitiesByComponent(MainViewComponent.class)) {
            if (Entities.getMainView(e).getRenderLayer().index() == layer.index()) {
                list.add(e);
            }
        }

        return list;
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
}
