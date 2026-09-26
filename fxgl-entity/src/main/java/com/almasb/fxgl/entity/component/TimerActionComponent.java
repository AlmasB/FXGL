/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.fxgl.time.Timer;
import com.almasb.fxgl.time.TimerAction;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.util.Duration;

/**
 * Component to schedule the execution of actions.
 * The timer's time per frame (TPF) is tied to the entity's TPF.
 * The entity's TPF can be modified by {@link com.almasb.fxgl.entity.components.TimeComponent}.
 *
 * @implNote - A wrapper around a Timer class, as a component.
 *
 * @author Michael Pearson (<a href="https://github.com/michqql/">https://github.com/michqql/</a>)
 */
public final class TimerActionComponent extends Component {

    private final Timer timer = new Timer();

    @Override
    public void onUpdate(double tpf) {
        this.timer.update(tpf);
    }

    /**
     * The Runnable [action] will be scheduled to start at given [interval].
     * The action will start for the first time after given interval.
     * The action will be scheduled unlimited number of times unless user cancels it
     * via the returned action object.
     *
     * @return timer action
     */
    public TimerAction runAtInterval(Runnable action, Duration interval) {
        return timer.runAtInterval(action, interval);
    }

    /**
     * The Runnable [action] will be scheduled to start at given [interval].
     * The action will start for the first time after given interval.
     * The action will be scheduled [limit] number of times unless user cancels it
     * via the returned action object.
     *
     * @return timer action
     */
    public TimerAction runAtInterval(Runnable action, Duration interval, int limit) {
        return timer.runAtInterval(action, interval, limit);
    }

    /**
     * The Runnable [action] will be scheduled to start at given [interval].
     * The Runnable action will be scheduled IFF
     * [whileCondition] is initially true.
     * The action will start for the first time after given interval.
     * The action will be removed from schedule when [whileCondition] becomes "false".
     * Note: you must retain the reference to the [whileCondition] property to avoid it being
     * garbage collected, otherwise the [action] may never stop.
     *
     * @return timer action
     */
    public TimerAction runAtIntervalWhile(Runnable action, Duration interval, ReadOnlyBooleanProperty whileCondition) {
        return timer.runAtIntervalWhile(action, interval, whileCondition);
    }

    /**
     * The Runnable [action] will be scheduled to run once after given [delay].
     * The action can be cancelled before it starts via the returned action object.
     *
     * @return timer action
     */
    public TimerAction runOnceAfter(Runnable action, Duration delay) {
        return timer.runOnceAfter(action, delay);
    }

    /**
     * Remove all scheduled actions.
     */
    public void clear() {
        timer.clear();
    }
}
