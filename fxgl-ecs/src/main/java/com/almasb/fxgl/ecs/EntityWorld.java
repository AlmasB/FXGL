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

package com.almasb.fxgl.ecs;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.ObjectMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages entities and allows queries.
 * Can be extended to provide specific functionality.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityWorld {

    private static final Logger log = LogManager.getLogger(EntityWorld.class);

    /**
     * List of entities currently in the world.
     */
    protected List<Entity> entities;

    /**
     * List of entities waiting to be added to the world.
     */
    private List<Entity> addQueue;

    /**
     * List of entities waiting to be removed from the world.
     */
    private List<Entity> removeQueue;

    private ObjectMap<Class<? extends Component>, Array<Entity> > componentMap = new ObjectMap<>();

    //private Array<EntitySystem> entitySystems = new Array<>(true, 32);

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
        entities = new ArrayList<>(initialCapacity);
        addQueue = new ArrayList<>(initialCapacity);
        removeQueue = new ArrayList<>(initialCapacity);
    }

    /**
     * Places an entity in the queue to be added to the world.
     * The entity will be added to the world in the next tick.
     *
     * @param entity the entity to add to world
     */
    public final void addEntity(Entity entity) {
        if (entities.contains(entity))
            throw new IllegalArgumentException("Entity is already attached to world");

        addQueue.add(entity);
    }

    /**
     * Places entities in the queue to be added to the world.
     * The entities will be added to the world in the next tick.
     *
     * @param entitiesToAdd the entities to add to world
     */
    public final void addEntities(Entity... entitiesToAdd) {
        for (Entity e : entitiesToAdd) {
            addEntity(e);
        }
    }

    /**
     * Places an entity in the queue to be removed from the world.
     * The entity will be removed in the next tick.
     *
     * @param entity the entity to remove from world
     */
    public final void removeEntity(Entity entity) {
        if (!entities.contains(entity)) {
            log.warn("Attempted to remove entity not attached to world");
            return;
        }

        removeQueue.add(entity);
    }

    /**
     * Places entities in the queue to be removed from the world.
     * The entities will be removed in the next tick.
     *
     * @param entitiesToRemove the entity to remove from world
     */
    public final void removeEntities(Entity... entitiesToRemove) {
        for (Entity e : entitiesToRemove) {
            removeEntity(e);
        }
    }

//    /**
//     * @param type
//     * @param <T>
//     * @return entity system with given type or null
//     */
//    public final <T extends EntitySystem> T getEntitySystem(Class<T> type) {
//        for (EntitySystem system : entitySystems) {
//            if (system.getClass().equals(type)) {
//                return (T) system;
//            }
//        }
//
//        return null;
//    }

//    public final void addEntitySystem(EntitySystem system) {
//        entitySystems.add(system);
//    }
//
//    public final void removeEntitySystem(EntitySystem system) {
//        entitySystems.removeValue(system, true);
//    }

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
        worldListeners.removeValue(listener, true);
    }

    private void notifyEntityAdded(Entity e) {
        for (EntityWorldListener l : worldListeners)
            l.onEntityAdded(e);
    }

    private void notifyEntityRemoved(Entity e) {
        for (EntityWorldListener l : worldListeners)
            l.onEntityRemoved(e);
    }

    private void notifyWorldUpdated(double tpf) {
        for (EntityWorldListener l : worldListeners)
            l.onWorldUpdate(tpf);
    }

    private void notifyWorldReset() {
        for (EntityWorldListener l : worldListeners)
            l.onWorldReset();
    }

    private void registerAndInitPendingEntities() {
        entities.addAll(addQueue);

        for (int i = 0; i < addQueue.size(); i++) {
            Entity e = addQueue.get(i);

            for (Class<? extends Component> type : e.components.keys()) {
                addComponentMap(type, e);
            }

            e.init(this);
            notifyEntityAdded(e);
        }

        addQueue.clear();
    }

    private void removeAndCleanPendingEntities() {
        entities.removeAll(removeQueue);

        for (int i = 0; i < removeQueue.size(); i++) {
            Entity e = removeQueue.get(i);

            for (Class<? extends Component> type : e.components.keys()) {
                removeComponentMap(type, e);
            }

            notifyEntityRemoved(e);
            e.clean();
        }

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
     * <li>Notifies world reset</li>
     * </ol>
     */
    protected void reset() {
        log.trace("Resetting entity world");

        registerAndInitPendingEntities();
        removeAndCleanPendingEntities();

        entities.forEach(Entity::clean);
        entities.clear();

        componentMap.clear();

        notifyWorldReset();
    }

    /**
     * Performs a single world update tick.
     * <p>
     * <ol>
     * <li>Registers waiting "add" entities</li>
     * <li>Removes waiting "remove" entities</li>
     * <li>Updates all entities</li>
     * <li>Notifies world update</li>
     * </ol>
     *
     * @param tpf time per frame
     */
    protected void update(double tpf) {
        registerAndInitPendingEntities();
        removeAndCleanPendingEntities();

        // NOT IMPLEMENTED
//        for (EntitySystem system : entitySystems) {
//            if (system.isPaused())
//                continue;
//
//            for (Class<? extends Component> type : system.getRequiredComponents()) {
//                for (Entity e : getEntitiesByComponent(type)) {
//                    system.onUpdate(e, tpf);
//                }
//            }
//        }

        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).update(tpf);
        }

        notifyWorldUpdated(tpf);
    }

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

        array.removeValue(e, true);
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

    private static final Array<Entity> EMPTY = new Array<>(0);

    /**
     * @param type component type
     * @return array of entities that have given component (do NOT modify)
     */
    public final Array<Entity> getEntitiesByComponent(Class<? extends Component> type) {
        return componentMap.get(type, EMPTY);
    }

    void onComponentAdded(Component component, Entity e) {
        addComponentMap(component.getClass(), e);
    }

    void onComponentRemoved(Component component, Entity e) {
        removeComponentMap(component.getClass(), e);
    }
}
