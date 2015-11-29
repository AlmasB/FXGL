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
import com.almasb.fxgl.gameplay.Achievement;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.KeyEvent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Events {

    public static final UpdateEvent UPDATE_EVENT = new UpdateEvent();


    public static final class AchievementEvent extends Event {

        private Achievement achievement;

        public AchievementEvent(Achievement achievement) {
            super(new EventType<>(Event.ANY, "ACHIEVEMENT"));
            this.achievement = achievement;
        }

        public Achievement getAchievement() {
            return achievement;
        }
    }

    public static final class EntityEvent extends Event {

        public static final EventType<EntityEvent> ANY =
                new EventType<>(Event.ANY, "ENTITY");

        public static final EventType<EntityEvent> ADDED_TO_WORLD =
                new EventType<>(ANY, "ADDED_TO_WORLD");

        public static final EventType<EntityEvent> REMOVED_FROM_WORLD =
                new EventType<>(ANY, "REMOVED_FROM_WORLD");

        private Entity entity;

        public Entity getEntity() {
            return entity;
        }

        public EntityEvent(@NamedArg("eventType") EventType<? extends Event> eventType, Entity entity) {
            super(eventType);
            this.entity = entity;
        }
    }

    public static final class SystemEvent extends Event {

        public static final EventType<SystemEvent> ANY =
                new EventType<>(Event.ANY, "SYSTEM");

        public static final EventType<SystemEvent> RESET =
                new EventType<>(ANY, "RESET");

        public static final EventType<SystemEvent> EXIT =
                new EventType<>(ANY, "EXIT");

        public SystemEvent(@NamedArg("eventType") EventType<? extends Event> eventType) {
            super(eventType);
        }
    }





//
//    public final class NotificationEvent extends Event {
//
//        private static final long serialVersionUID = 1L;
//
//        /**
//         * Common super-type for all update event types.
//         */
//        public static final EventType<NotificationEvent> ANY =
//                new EventType<>(Event.ANY, "UPDATE");
//
//        public NotificationEvent() {
//            super(null, null, ANY);
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        public EventType<NotificationEvent> getEventType() {
//            return (EventType<NotificationEvent>) super.getEventType();
//        }
//    }
}
