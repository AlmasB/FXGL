/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

import com.almasb.fxgl.ecs.Entity;
import javafx.beans.NamedArg;
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

    public static final EventType<EntityEvent> ANY
            = new EventType<>(Event.ANY, "ENTITY_EVENT");

    private Entity triggerEntity;
    private Entity targetEntity;

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
     * Constructs entity event with given type, trigger entity and the entity being
     * targeted by the event.
     *
     * @param eventType type
     * @param triggerEntity trigger entity
     * @param targetEntity target entity
     */
    public EntityEvent(@NamedArg("eventType") EventType<? extends Event> eventType, Entity triggerEntity, Entity targetEntity) {
        super(eventType);
        this.triggerEntity = triggerEntity;
        this.targetEntity = targetEntity;
    }

    /**
     * Constructs entity event with given type and trigger entity.
     * Target entity is set to trigger entity.
     *
     * @param eventType type
     * @param triggerEntity trigger entity
     */
    public EntityEvent(@NamedArg("eventType") EventType<? extends Event> eventType, Entity triggerEntity) {
        this(eventType, triggerEntity, triggerEntity);
    }

    @Override
    public String toString() {
        return "EntityEvent[trigger=" + triggerEntity + ",target=" + targetEntity + "]";
    }
}
