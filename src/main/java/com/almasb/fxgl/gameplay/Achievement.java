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

package com.almasb.fxgl.gameplay;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.event.AchievementProgressEvent;
import com.almasb.fxgl.logging.Logger;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ChangeListener;

/**
 * A game achievement.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Achievement {
    private static final Logger log = FXGL.getLogger("FXGL.Achievement");

    private String name;
    private String description;

    private Runnable onAchieved;

    private ReadOnlyBooleanWrapper achieved = new ReadOnlyBooleanWrapper(false);
    private ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> {
        if (newValue) {
            setAchieved();
            if (onAchieved != null)
                onAchieved.run();
            else
                log.warning("onAchieved was not set. Unmanaged achievement!");
        }
    };

    /**
     * Constructs a new achievement with given name and description.
     *
     * @param name the name
     * @param description the description on how to unlock achievement
     */
    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
        achieved.addListener(listener);
    }

    void setOnAchieved(Runnable onAchieved) {
        this.onAchieved = onAchieved;
    }

    void setAchieved() {
        achieved.removeListener(listener);
        achieved.unbind();
        achieved.set(true);
    }

    /**
     * @return true iff the achievement has been unlocked
     */
    public boolean isAchieved() {
        return achievedProperty().get();
    }

    /**
     * @return achieved boolean property (read-only)
     */
    public ReadOnlyBooleanProperty achievedProperty() {
        return achieved.getReadOnlyProperty();
    }

    /**
     * Binds achievement to given binding (condition).
     * No-op if achievement is already unlocked.
     *
     * @param binding condition on which achievement is unlocked
     */
    public void bind(BooleanBinding binding) {
        if (!isAchieved())
            achieved.bind(binding);
    }

    private ChangeListener<Boolean> progressListener;

    /**
     * Bind achievement condition to given property.
     *
     * @param property the property
     * @param value the value at which the achievement is unlocked
     */
    public void bind(IntegerProperty property, int value) {
        if (isAchieved())
            return;

        bind(property.greaterThanOrEqualTo(value));
        BooleanBinding bb = property.greaterThanOrEqualTo(value / 2);

        progressListener = (o, fired, reachedHalf) -> {
            if (reachedHalf && !fired) {
                FXGL.getEventBus().fireEvent(new AchievementProgressEvent(this, property.get(), value));
                bb.removeListener(progressListener);
            }
        };

        bb.addListener(progressListener);
    }

    /**
     * @return achievement name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns description. This usually contains info on how
     * to unlock the achievement.
     *
     * @return achievement description
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + ":achieved(" + isAchieved() + ")";
    }
}
