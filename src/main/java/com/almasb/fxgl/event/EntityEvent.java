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
package com.almasb.fxgl.event;

import com.almasb.fxgl.entity.Entity;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * A general FXGL event. Keeps track of its source and target.
 * Target is the triggerEntity on which the event was called, source (if exists)
 * is the triggerEntity that triggered the event. If the source doesn't exist,
 * it is equal to target.
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
     * @param targetEntity target entity
     */
    public EntityEvent(@NamedArg("eventType") EventType<? extends Event> eventType, Entity triggerEntity) {
        this(eventType, triggerEntity, triggerEntity);
    }
}
