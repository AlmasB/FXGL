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

import com.almasb.ents.Entity;
import com.almasb.ents.EntityWorld;
import com.almasb.ents.EntityWorldListener;
import com.almasb.fxeventbus.EventBus;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.MainViewComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.TypeComponent;
import com.almasb.fxgl.event.FXGLEvent;
import com.almasb.fxgl.event.UpdateEvent;
import com.almasb.fxgl.event.WorldEvent;
import com.almasb.fxgl.util.FXGLLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

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
public final class GameWorld extends EntityWorld {

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
            update(event.tpf());
        });
        eventBus.addEventHandler(FXGLEvent.RESET, event -> {
            log.finer("Resetting game world");
            reset();
        });

        addWorldListener(new EntityWorldListener() {
            @Override
            public void onEntityAdded(Entity entity) {
                eventBus.fireEvent(WorldEvent.entityAdded(entity));
            }

            @Override
            public void onEntityRemoved(Entity entity) {
                eventBus.fireEvent(WorldEvent.entityRemoved(entity));
            }
        });

        log.finer("Game world initialized");
    }

    /**
     * This query only works on entities with TypeComponent.
     * If called with no arguments, all entities are returned.
     *
     * @param types entity types
     * @return entities
     */
    public List<Entity> getEntitiesByType(Enum<?>... types) {
        if (types.length == 0)
            return getEntities();

        return getEntitiesByComponent(TypeComponent.class).stream()
                .filter(e -> {
                    for (Enum<?> type : types) {
                        if (Entities.getType(e).isType(type)) {
                            return true;
                        }
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * Returns the closest entity to the given entity with given
     * filter. The given
     * entity itself is never returned.
     * <p>
     * If there no entities satisfying the requirement, {@link Optional#empty()}
     * is returned.
     *
     * @param entity selected entity
     * @param filter requirements
     * @return closest entity to selected entity with type
     */
    public Optional<Entity> getClosestEntity(Entity entity, Predicate<Entity> filter) {
        return getEntitiesByComponent(PositionComponent.class).stream()
                .filter(e -> filter.test(e) && e != entity)
                .sorted((e1, e2) -> (int) (Entities.getPosition(e1).distance(Entities.getPosition(entity))
                        - Entities.getPosition(e2).distance(Entities.getPosition(entity))))
                .findFirst();
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
     * Returns a list of entities
     * which are partially or entirely
     * in the specified rectangular selection.
     * This query only works on entities with BoundingBoxComponent.
     *
     * @param selection Rectangle2D that describes the selection box
     * @return  list of entities in the range
     */
    public List<Entity> getEntitiesInRange(Rectangle2D selection) {
        return getEntitiesByComponent(BoundingBoxComponent.class).stream()
                .filter(e -> {
                    BoundingBoxComponent bbox = Entities.getBBox(e);
                    return bbox.isWithin(selection);
                })
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of entities which have the given render layer index.
     * This query only works on entities with MainViewComponent.
     *
     * @param layer render layer
     * @return list of entities in the layer
     */
    public List<Entity> getEntitiesByLayer(RenderLayer layer) {
        return getEntitiesByComponent(MainViewComponent.class).stream()
                .filter(e -> {
                    MainViewComponent view = Entities.getMainView(e);
                    return view.getRenderLayer().index() == layer.index();
                })
                .collect(Collectors.toList());
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
        return getEntitiesByComponent(PositionComponent.class).stream()
                .filter(e -> {
                    PositionComponent positionComponent = Entities.getPosition(e);
                    return positionComponent.getValue().equals(position);
                })
                .findAny();
    }
}
