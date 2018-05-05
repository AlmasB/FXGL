/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.fxgl.util.Optional;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * A general entity event. Keeps track of its trigger and target entities.
 * Trigger entity is the one that triggered the event.
 * Target entity (if exists) is the one that is targeted by the event.
 * If target entity doesn't exist then it is the same as trigger entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class EntityEvent extends Event {

    public static final EventType<EntityEvent> ANY = new EventType<>(Event.ANY, "ENTITY_EVENT");

    private ObjectMap<String, Object> data = new ObjectMap<>();

    private Entity triggerEntity;
    private Entity targetEntity;

    private String name;

    public String getName() {
        return name;
    }

    public ObjectMap<String, Object> getData() {
        return data;
    }

    /**
     * @return entity that triggered this event
     */
    public Entity getTriggerEntity() {
        return triggerEntity;
    }

    /**
     * Returns entity that was target of the event.
     * Note that if there is no target entity then it is the same as trigger entity.
     *
     * @return target entity
     */
    public Entity getTargetEntity() {
        return targetEntity;
    }

    /**
     * Constructs entity event with given type and trigger entity.
     * Target entity is set to trigger entity.
     *
     * @param name event name
     * @param triggerEntity trigger entity
     */
    public EntityEvent(String name, Entity triggerEntity) {
        this(name, triggerEntity, triggerEntity);
    }

    /**
     * Constructs entity event with given type, trigger entity and the entity being
     * targeted by the event.
     *
     * @param name event name
     * @param triggerEntity trigger entity
     * @param targetEntity target entity
     */
    public EntityEvent(String name, Entity triggerEntity, Entity targetEntity) {
        super(ANY);
        this.name = name;
        this.triggerEntity = triggerEntity;
        this.targetEntity = targetEntity;
    }

    public EntityEvent(EventType<? extends Event> eventType, Entity triggerEntity) {
        this(eventType, triggerEntity, triggerEntity);
    }

    public EntityEvent(EventType<? extends Event> eventType, Entity triggerEntity, Entity targetEntity) {
        super(eventType);
        this.name = eventType.getName();
        this.triggerEntity = triggerEntity;
        this.targetEntity = targetEntity;
    }

    private static final Object NULL = new Object();

    /**
     * @param key property key
     * @param value property value
     */
    public void setData(String key, Object value) {
        data.put(key, value);
    }

    /**
     * @param key property key
     * @return property value or null if key not present
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        Object value = data.get(key, NULL);
        if (value == NULL) {
            return null;
        }

        return (T) value;
    }

    /**
     * @param key property key
     * @return property value or Optional.empty() if value is null or key not present
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getDataOptional(String key) {
        Object value = data.get(key, null);
        return Optional.ofNullable((T) value);
    }

    @Override
    public String toString() {
        return "EntityEvent[trigger=" + triggerEntity + ",target=" + targetEntity + "]";
    }
}
