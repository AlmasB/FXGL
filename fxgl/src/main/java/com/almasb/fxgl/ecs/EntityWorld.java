/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.fxgl.core.logging.FXGLLogger;
import com.almasb.fxgl.core.logging.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages entities and allows queries.
 * Can be extended to provide specific functionality.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityWorld {

    private static final Logger log = FXGLLogger.get(EntityWorld.class);

    /**
     * The update list.
     */
    private Array<Entity> updateList;

    /**
     * List of entities added to the update list in the next tick.
     */
    private Array<Entity> waitingList;

    /**
     * List of entities in the world.
     */
    protected List<Entity> entities;

    /**
     * Constructs the world with initial entity capacity = 32.
     */
    public EntityWorld() {
        this(32);
    }

    /**
     * Constructs the world with given initial entity capacity.
     *
     * @param initialCapacity initial entity capacity
     */
    public EntityWorld(int initialCapacity) {
        updateList = new Array<>(true, initialCapacity);
        waitingList = new Array<>(false, initialCapacity);
        entities = new ArrayList<>(initialCapacity);
    }

    /**
     * Adds entity to this world.
     * The entity will be added to update list in the next tick.
     *
     * @param entity the entity to add to world
     */
    public final void addEntity(Entity entity) {
        if (entity.isActive())
            throw new IllegalArgumentException("Entity is already attached to world");

        waitingList.add(entity);
        entities.add(entity);

        for (Class<? extends Component> type : entity.getComponentTypes()) {
            addComponentMap(type, entity);
        }

        entity.init(this);
        notifyEntityAdded(entity);
    }

    /**
     * @param entitiesToAdd the entities to add to world
     */
    public final void addEntities(Entity... entitiesToAdd) {
        for (Entity e : entitiesToAdd) {
            addEntity(e);
        }
    }

    /**
     *
     * @param entity the entity to remove from world
     */
    public final void removeEntity(Entity entity) {
        if (entity.getWorld() != this)
            throw new IllegalArgumentException("Attempted to remove entity not attached to this world");

        entities.remove(entity);

        for (Class<? extends Component> type : entity.getComponentTypes()) {
            removeComponentMap(type, entity);
        }

        notifyEntityRemoved(entity);
        entity.clean();
    }

    /**
     *
     * @param entitiesToRemove the entity to remove from world
     */
    public final void removeEntities(Entity... entitiesToRemove) {
        for (Entity e : entitiesToRemove) {
            removeEntity(e);
        }
    }

    /**
     * Performs a single world update tick.
     * <p>
     * <ol>
     * </ol>
     *
     * @param tpf time per frame
     */
    protected void update(double tpf) {
        updateList.addAll(waitingList);
        waitingList.clear();

        for (Iterator<Entity> it = updateList.iterator(); it.hasNext(); ) {
            Entity e = it.next();

            if (e.isActive()) {
                e.update(tpf);
            } else {
                it.remove();
            }
        }

        notifyWorldUpdated(tpf);
    }

    /**
     * Resets the world to its initial state.
     * Does NOT clear state listeners.
     * <p>
     * <ol>
     * </ol>
     */
    protected void reset() {
        log.debug("Resetting entity world");

        for (Entity e : updateList) {
            if (e.isActive()) {
                notifyEntityRemoved(e);
                e.clean();
            }
        }

        for (Entity e : waitingList) {
            notifyEntityRemoved(e);
            e.clean();
        }

        waitingList.clear();
        updateList.clear();
        entities.clear();

        componentMap.clear();

        notifyWorldReset();
    }

    private Array<EntityWorldListener> worldListeners = new Array<>(true, 16);

    /**
     * Add world listener to be notified of events.
     *
     * @param listener the listener
     */
    public final void addWorldListener(EntityWorldListener listener) {
        worldListeners.add(listener);
    }

    /**
     * Remove world listener.
     *
     * @param listener the listener
     */
    public final void removeWorldListener(EntityWorldListener listener) {
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

    private void notifyWorldUpdated(double tpf) {
        for (int i = 0; i < worldListeners.size(); i++) {
            worldListeners.get(i).onWorldUpdate(tpf);
        }
    }

    private void notifyWorldReset() {
        for (int i = 0; i < worldListeners.size(); i++) {
            worldListeners.get(i).onWorldReset();
        }
    }

    private ObjectMap<Class<? extends Component>, Array<Entity> > componentMap = new ObjectMap<>();

    private void addComponentMap(Class<? extends Component> type, Entity e) {
        Array<Entity> array = componentMap.get(type);

        if (array == null) {
            array = new Array<>(false, 128);
            componentMap.put(type, array);
        }

        array.add(e);
    }

    private void removeComponentMap(Class<? extends Component> type, Entity e) {
        // assert array exists because entity was added first
        Array<Entity> array = componentMap.get(type);

        array.removeValueByIdentity(e);
    }

    /**
     * @return direct list of entities in the world (do NOT modify)
     */
    public final List<Entity> getEntities() {
        return entities;
    }

    /**
     * Returns entities currently registered in the world.
     *
     * @return shallow copy of the entities list (new list)
     */
    public final List<Entity> getEntitiesCopy() {
        return new ArrayList<>(entities);
    }

    /**
     * @param type component type
     * @return array of entities that have given component (do NOT modify)
     */
    public final Array<Entity> getEntitiesByComponent(Class<? extends Component> type) {
        return componentMap.get(type, Array.empty());
    }

    void onComponentAdded(Component component, Entity e) {
        addComponentMap(component.getClass(), e);
    }

    void onComponentRemoved(Component component, Entity e) {
        removeComponentMap(component.getClass(), e);
    }
}
