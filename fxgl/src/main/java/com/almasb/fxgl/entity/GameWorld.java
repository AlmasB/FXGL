/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.fxgl.core.collection.UnorderedArray;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.reflect.ReflectionUtils;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.IDComponent;
import com.almasb.fxgl.entity.component.IrremovableComponent;
import com.almasb.fxgl.entity.component.TimeComponent;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.parser.tiled.TiledMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents pure logical state of the game.
 * Manages all entities and allows queries.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class GameWorld {

    private static Logger log = Logger.get("FXGL.GameWorld");
    
    private Array<Entity> updateList;

    /**
     * List of entities added to the update list in the next tick.
     */
    private Array<Entity> waitingList;

    /**
     * List of entities in the world.
     */
    private List<Entity> entities;

    private GameWorldQuery query;

    /**
     * Constructs the world with initial entity capacity = 32.
     */
    public GameWorld() {
        this(32);
    }

    /**
     * @param initialCapacity initial entity capacity
     */
    public GameWorld(int initialCapacity) {
        updateList = new Array<>(initialCapacity);
        waitingList = new UnorderedArray<>(initialCapacity);
        entities = new ArrayList<>(initialCapacity);

        query = new GameWorldQuery(entities);

        log.debug("Game world initialized");
    }

    /**
     * The entity will be added to update list in the next tick.
     *
     * @param entity the entity to add to world
     */
    public void addEntity(Entity entity) {
        if (entity.isActive())
            throw new IllegalArgumentException("Entity is already attached to world");

        waitingList.add(entity);
        entities.add(entity);

        add(entity);
    }

    public void addEntities(Entity... entitiesToAdd) {
        for (Entity e : entitiesToAdd) {
            addEntity(e);
        }
    }

    /**
     * Entity will be removed from world and parties notified of removal in the same frame.
     * The components and controls of entity will be removed in the next frame to avoid
     * concurrency issues.
     */
    public void removeEntity(Entity entity) {
        if (!entity.isActive()) {
            log.warning("Attempted to remove entity which is not active");
            return;
        }

        if (!canRemove(entity))
            return;

        if (entity.getWorld() != this)
            throw new IllegalArgumentException("Attempted to remove entity not attached to this world");

        entities.remove(entity);

        entity.markForRemoval();
        notifyEntityRemoved(entity);

        // we cannot clean entities here because this may have been called through a control
        // while entity is being updated
        // so, we clean the entity on next frame
    }

    public void removeEntities(Entity... entitiesToRemove) {
        for (Entity e : entitiesToRemove) {
            removeEntity(e);
        }
    }

    /**
     * Performs a single world update tick.
     *
     * @param tpf time per frame
     */
    private void update(double tpf) {
        updateList.addAll(waitingList);
        waitingList.clear();

        for (Iterator<Entity> it = updateList.iterator(); it.hasNext(); ) {
            Entity e = it.next();

            if (!e.isActive()) {
                // clean entities removed in the last frame
                e.clean();
                it.remove();
            } else {
                TimeComponent time = e.getComponent(TimeComponent.class);

                double tpfRatio = time == null ? 1.0 : time.getValue();

                e.update(tpf * tpfRatio);
            }
        }
    }

    /**
     * Removes all (including with IrremovableComponent) entities.
     * Does NOT clear state listeners.
     * Do NOT call this method manually.
     * It is called automatically by FXGL during initGame().
     */
    public void clear() {
        log.debug("Clearing game world");

        waitingList.clear();

        // we may still have some entities that have been removed but not yet cleaned
        for (Iterator<Entity> it = updateList.iterator(); it.hasNext(); ) {
            Entity e = it.next();

            if (!e.isActive()) {
                e.clean();
            }

            it.remove();
        }

        // entities list does not contain "not active" entities, so we do full clean
        for (Iterator<Entity> it = entities.iterator(); it.hasNext(); ) {
            Entity e = it.next();

            e.markForRemoval();
            notifyEntityRemoved(e);
            e.clean();

            it.remove();
        }
    }

    private void add(Entity entity) {
        entity.init(this);
        notifyEntityAdded(entity);
    }

    private boolean canRemove(Entity entity) {
        return !entity.hasComponent(IrremovableComponent.class);
    }

    private Array<EntityWorldListener> worldListeners = new Array<>();

    public void addWorldListener(EntityWorldListener listener) {
        worldListeners.add(listener);
    }

    public void removeWorldListener(EntityWorldListener listener) {
        worldListeners.removeValueByIdentity(listener);
    }

    private void notifyEntityAdded(Entity e) {
        for (int i = 0; i < worldListeners.size(); i++) {
            worldListeners.get(i).onEntityAdded(e);
        }
    }

    private void notifyEntityRemoved(Entity e) {
        for (int i = 0; i < worldListeners.size(); i++) {
            worldListeners.get(i).onEntityRemoved(e);
        }
    }

    /**
     * @return direct list of entities in the world (do NOT modify)
     */
    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * @return shallow copy of the entities list (new list)
     */
    public List<Entity> getEntitiesCopy() {
        return new ArrayList<>(entities);
    }

    public void onUpdate(double tpf) {
        update(tpf);
    }

    private ObjectProperty<Entity> selectedEntity = new SimpleObjectProperty<>();

    /**
     * @return last selected (clicked on by mouse) entity
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
        clearLevel();

        log.debug("Setting level: " + level);
        level.getEntities().forEach(this::addEntity);
    }

    /**
     * @param mapFileName name of the .json file
     */
    public void setLevelFromMap(String mapFileName) {
        setLevelFromMap(FXGL.getAssetLoader().loadJSON(mapFileName, TiledMap.class));
    }

    public void setLevelFromMap(TiledMap map) {
        clearLevel();

        log.debug("Setting level from map");

        map.getLayers()
                .stream()
                .filter(l -> l.getType().equals("tilelayer"))
                .forEach(l -> Entities.builder()
                        .viewFromTiles(map, l.getName(), RenderLayer.BACKGROUND)
                        .buildAndAttach(this));

        map.getLayers()
                .stream()
                .filter(l -> l.getType().equals("objectgroup"))
                .flatMap(l -> l.getObjects().stream())
                .forEach(obj -> spawn(obj.getType(), new SpawnData(obj)));
    }

    /**
     * Removes removable entities.
     */
    private void clearLevel() {
        log.debug("Clearing removable entities");

        waitingList.clear();

        // we may still have some entities that have been removed but not yet cleaned
        // but we do not want to remove Irremovables
        for (Iterator<Entity> it = updateList.iterator(); it.hasNext(); ) {
            Entity e = it.next();

            if (canRemove(e)) {
                if (!e.isActive()) {
                    // clean it here because "entities" list does not have "e"
                    e.clean();
                }

                it.remove();
            }
        }

        // entities list does not contain "not active" entities, so we do full clean
        // but we do not want to remove Irremovables
        for (Iterator<Entity> it = entities.iterator(); it.hasNext(); ) {
            Entity e = it.next();

            if (canRemove(e)) {
                e.markForRemoval();
                notifyEntityRemoved(e);
                e.clean();

                it.remove();
            }
        }
    }

    private EntityFactory entityFactory = null;

    private ObjectMap<String, EntitySpawner> entitySpawners = new ObjectMap<>();

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

        ReflectionUtils.findMethodsMapToFunctions(entityFactory, Spawns.class, EntitySpawner.class)
                .forEach((annotation, entitySpawner) -> entitySpawners.put(annotation.value(), entitySpawner));
    }

    public Entity spawn(String entityName) {
        return spawn(entityName, 0, 0);
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

        EntitySpawner spawner = entitySpawners.get(entityName);
        if (spawner == null)
            throw new IllegalArgumentException("EntityFactory does not have a method annotated @Spawns(" + entityName + ")");

        Entity entity = spawner.spawn(data);
        addEntity(entity);
        return entity;
    }

    /* QUERIES */

    public <T extends Entity> EntityGroup<T> getGroup(Enum<?>... types) {
        return new EntityGroup<T>(this, (List<? extends T>) getEntitiesByType(types), types);
    }

    /**
     * Useful for singleton type entities, e.g. Player.
     * 
     * @return first occurrence matching given type
     */
    public Optional<Entity> getSingleton(Enum<?> type) {
        return getSingleton(e -> e.isType(type));
    }

    /**
     * @return first occurrence matching given predicate
     */
    public Optional<Entity> getSingleton(Predicate<Entity> predicate) {
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (predicate.test(e)) {
                return Optional.of(e);
            }
        }

        return Optional.empty();
    }

    /**
     * @param type entity type
     * @return a random entity with given type
     */
    public Optional<Entity> getRandom(Enum<?> type) {
        return FXGLMath.random(getEntitiesByType(type));
    }

    public Optional<Entity> getRandom(Predicate<Entity> predicate) {
        return FXGLMath.random(getEntitiesFiltered(predicate));
    }

    /**
     * @param type component type
     * @return array of entities that have given component
     */
    public List<Entity> getEntitiesByComponent(Class<? extends Component> type) {
        return entities.stream()
                .filter(e -> e.hasComponent(type))
                .collect(Collectors.toList());
    }

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
        for (Enum<?> type : types) {
            if (e.isType(type)) {
                return true;
            }
        }

        return false;
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
            if (e.getBoundingBoxComponent().isWithin(minX, minY, maxX, maxY)) {
                result.add(e);
            }
        }
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
        BoundingBoxComponent entityBBox = entity.getBoundingBoxComponent();

        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e.getBoundingBoxComponent().isCollidingWith(entityBBox) && e != entity) {
                result.add(e);
            }
        }
    }

    /**
     * Returns a list of entities which have the given render layer index.
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

            if (e.getRenderLayer().index() == layer.index()) {
                result.add(e);
            }
        }
    }

    /**
     * Returns a list of entities at given position.
     * The position x and y must equal to entity's position x and y.
     *
     * @param position point in the world
     * @return entities at given point
     */
    public List<Entity> getEntitiesAt(Point2D position) {
        return query.getEntitiesAt(position);
    }

    /**
     * GC-friendly version of {@link #getEntitiesAt(Point2D)}.
     *
     * @param result the array to collect entities
     * @param position point in the world
     */
    public void getEntitiesAt(Array<Entity> result, Point2D position) {
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);

            if (e.getPosition().equals(position)) {
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
        Array<Entity> array = new UnorderedArray<>(64);

        for (Entity e : entities) {
            if (filter.test(e) && e != entity) {
                array.add(e);
            }
        }

        if (array.size() == 0)
            return Optional.empty();

        array.sort(Comparator.comparingDouble(e -> e.distance(entity)));

        return Optional.of(array.get(0));
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
            IDComponent idComponent = e.getComponent(IDComponent.class);
            if (idComponent.getName().equals(name) && idComponent.getID() == id) {
                return Optional.of(e);
            }
        }

        return Optional.empty();
    }
}
