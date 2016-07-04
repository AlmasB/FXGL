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

package com.almasb.fxgl.event;

import com.almasb.fxgl.gameplay.Achievement;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * Occurs on achievement unlocked.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AchievementEvent extends Event {

    public static final EventType<AchievementEvent> ANY =
            new EventType<>(Event.ANY, "ACHIEVEMENT_EVENT");

    public static final EventType<AchievementEvent> ACHIEVED =
            new EventType<>(ANY, "ACHIEVED");

    private Achievement achievement;

    public AchievementEvent(Achievement achievement) {
        this(ANY, achievement);
    }

    public AchievementEvent(EventType<? extends AchievementEvent> eventType, Achievement achievement) {
        super(eventType);
        this.achievement = achievement;
    }

    /**
     * @return achievement associated with the event
     */
    public Achievement getAchievement() {
        return achievement;
    }

    @Override
    public String toString() {
        return "AchievementEvent[name=" + achievement.getName()
                + ",description= " + achievement.getDescription() + "]";
    }
}
