/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.fxgl.time.TimerAction;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.util.Duration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link TimerActionComponent} class.
 *
 * @author Michael Pearson (<a href="https://github.com/michqql/">https://github.com/michqql/</a>)
 */
public class TimerActionComponentTest {

    /**
     * Tests that the action is ran multiple times as the interval elapses.
     * Tests the method {@link TimerActionComponent#runAtInterval(Runnable, Duration, int) runAtInterval}.
     */
    @Test
    public void testRunAtInterval() {
        final TimerActionComponent timerActionComponent = new TimerActionComponent();
        final IntegerProperty executionCounter = new SimpleIntegerProperty();
        final Runnable action = () -> executionCounter.set(executionCounter.get() + 1);

        timerActionComponent.runAtInterval(action, Duration.seconds(0.5f));

        assertEquals(0, executionCounter.get());
        timerActionComponent.onUpdate(0.5f);
        assertEquals(1, executionCounter.get());
        timerActionComponent.onUpdate(0.4f);
        assertEquals(1, executionCounter.get());
        timerActionComponent.onUpdate(0.2f);
        assertEquals(2, executionCounter.get());
    }

    /**
     * Tests that the repeating action will not be executed after being cancelled.
     */
    @Test
    public void testRunAtIntervalThenCancel() {
        final TimerActionComponent timerActionComponent = new TimerActionComponent();
        final IntegerProperty executionCounter = new SimpleIntegerProperty();
        final Runnable action = () -> executionCounter.set(executionCounter.get() + 1);

        TimerAction timerAction = timerActionComponent.runAtInterval(action, Duration.seconds(0.5f));

        assertEquals(0, executionCounter.get());
        timerActionComponent.onUpdate(0.5f);
        assertEquals(1, executionCounter.get());

        timerAction.expire();

        timerActionComponent.onUpdate(0.5f);
        assertEquals(1, executionCounter.get());
    }

    /**
     * Tests that the action can be executed multiple times.
     */
    @Test
    public void testRunAtIntervalInLoop() {
        final TimerActionComponent timerActionComponent = new TimerActionComponent();
        final IntegerProperty executionCounter = new SimpleIntegerProperty();
        final Runnable action = () -> executionCounter.set(executionCounter.get() + 1);

        timerActionComponent.runAtInterval(action, Duration.seconds(0.5f));

        for(int i = 0; i < 10; ++i) {
            timerActionComponent.onUpdate(0.5f);
            assertEquals(i + 1, executionCounter.get());
        }
    }

    /**
     * Tests that the action expires with the limit and will not execute again.
     */
    @Test
    public void testRunAtIntervalWithLimit() {
        final TimerActionComponent timerActionComponent = new TimerActionComponent();
        final IntegerProperty executionCounter = new SimpleIntegerProperty();
        final Runnable action = () -> executionCounter.set(executionCounter.get() + 1);

        timerActionComponent.runAtInterval(action, Duration.seconds(0.5f), 4);

        for(int i = 0; i < 10; ++i) {
            timerActionComponent.onUpdate(0.5f);
        }

        assertEquals(4, executionCounter.get());
    }

    /**
     * Tests that the action expires when the condition becomes false.
     */
    @Test
    public void testRunAtIntervalConditional() {
        final TimerActionComponent timerActionComponent = new TimerActionComponent();
        final IntegerProperty executionCounter = new SimpleIntegerProperty();
        final Runnable action = () -> executionCounter.set(executionCounter.get() + 1);

        IntegerProperty iterationCount = new SimpleIntegerProperty();
        BooleanBinding condition = iterationCount.lessThan(5);

        BooleanProperty conditionProperty = new SimpleBooleanProperty();
        conditionProperty.bind(condition);

        timerActionComponent.runAtIntervalWhile(action, Duration.seconds(0.25f), conditionProperty);

        for(int i = 0; i < 10; ++i) {
            timerActionComponent.onUpdate(0.5f);
            iterationCount.set(iterationCount.get() + 1);
        }

        assertEquals(5, executionCounter.get());
    }

    /**
     * Tests that the conditional action can be cancelled by the user.
     */
    @Test
    public void testRunAtIntervalConditionalCancelledEarly() {
        final TimerActionComponent timerActionComponent = new TimerActionComponent();
        final IntegerProperty executionCounter = new SimpleIntegerProperty();
        final Runnable action = () -> executionCounter.set(executionCounter.get() + 1);

        IntegerProperty iterationCount = new SimpleIntegerProperty();
        BooleanBinding condition = iterationCount.lessThan(5);

        BooleanProperty conditionProperty = new SimpleBooleanProperty();
        conditionProperty.bind(condition);

        TimerAction timerAction = timerActionComponent.runAtIntervalWhile(action, Duration.seconds(0.25f), conditionProperty);

        for(int i = 0; i < 10; ++i) {
            timerActionComponent.onUpdate(0.5f);
            iterationCount.set(iterationCount.get() + 1);

            /* Cancel on the 3rd iteration */
            if(i == 2)
                timerAction.expire();
        }

        assertEquals(3, executionCounter.get());
    }

    /**
     * Tests that the action is ran exactly once after the delay has elapsed
     * when using {@link TimerActionComponent#runOnceAfter(Runnable, Duration) runOnceAfter}.
     */
    @Test
    public void testRunOnceAfter() {
        final TimerActionComponent timerActionComponent = new TimerActionComponent();
        final IntegerProperty executionCounter = new SimpleIntegerProperty();
        final Runnable action = () -> executionCounter.set(executionCounter.get() + 1);

        timerActionComponent.runOnceAfter(action, Duration.seconds(1));

        assertEquals(0, executionCounter.get());
        timerActionComponent.onUpdate(1.0f);
        assertEquals(1, executionCounter.get());
        timerActionComponent.onUpdate(1.0f);
        assertEquals(1, executionCounter.get());
    }

    /**
     * Tests that the action is only ran after the delay has elapsed,
     * when the component has already seen time elapse.
     * Tests the method {@link TimerActionComponent#runOnceAfter(Runnable, Duration) runOnceAfter}.
     */
    @Test
    public void testRunOnceAfterWithStartingTime() {
        final TimerActionComponent timerActionComponent = new TimerActionComponent();
        final IntegerProperty executionCounter = new SimpleIntegerProperty();
        final Runnable action = () -> executionCounter.set(executionCounter.get() + 1);

        timerActionComponent.onUpdate(2.0f);
        timerActionComponent.runOnceAfter(action, Duration.seconds(1));

        assertEquals(0, executionCounter.get());
        timerActionComponent.onUpdate(1.0f);
        assertEquals(1, executionCounter.get());
    }

    /**
     * Tests that the action is not ran if not enough time elapses.
     * Tests the method {@link TimerActionComponent#runOnceAfter(Runnable, Duration) runOnceAfter}.
     */
    @Test
    public void testRunOnceNotElapsed() {
        final TimerActionComponent timerActionComponent = new TimerActionComponent();
        final IntegerProperty executionCounter = new SimpleIntegerProperty();
        final Runnable action = () -> executionCounter.set(executionCounter.get() + 1);

        timerActionComponent.runOnceAfter(action, Duration.seconds(1));

        assertEquals(0, executionCounter.get());
        timerActionComponent.onUpdate(0.999f);
        assertEquals(0, executionCounter.get());
    }

    /**
     * Tests that the action is not executed if cancelled before the delay elapses.
     * Tests the method {@link TimerActionComponent#runOnceAfter(Runnable, Duration) runOnceAfter}.
     */
    @Test
    public void testRunOnceCancelled() {
        final TimerActionComponent timerActionComponent = new TimerActionComponent();
        final IntegerProperty executionCounter = new SimpleIntegerProperty();
        final Runnable action = () -> executionCounter.set(executionCounter.get() + 1);

        TimerAction timerAction = timerActionComponent.runOnceAfter(action, Duration.seconds(1));
        timerAction.expire();

        assertEquals(0, executionCounter.get());
        timerActionComponent.onUpdate(1.0f);
        assertEquals(0, executionCounter.get());
    }

    /**
     * Tests that multiple actions can be scheduled.
     */
    @Test
    public void testRunOnceWithMultiple() {
        final TimerActionComponent timerActionComponent = new TimerActionComponent();
        final IntegerProperty executionCounter = new SimpleIntegerProperty();
        final Runnable action = () -> executionCounter.set(executionCounter.get() + 1);

        timerActionComponent.runOnceAfter(action, Duration.seconds(0.1f));
        timerActionComponent.runOnceAfter(action, Duration.seconds(0.2f));
        timerActionComponent.runOnceAfter(action, Duration.seconds(0.29f)); /* Floating point cannot represent 0.3 well */
        timerActionComponent.runOnceAfter(action, Duration.seconds(0.4f));
        timerActionComponent.runOnceAfter(action, Duration.seconds(0.5f));


        for(int i = 0; i < 5; ++i) {
            timerActionComponent.onUpdate(0.1f);
            assertEquals(i + 1, executionCounter.get());
        }
    }

    /**
     * Tests that clearing the component will remove all actions.
     */
    @Test
    public void testClear() {
        final TimerActionComponent timerActionComponent = new TimerActionComponent();
        final IntegerProperty executionCounter = new SimpleIntegerProperty();
        final Runnable action = () -> executionCounter.set(executionCounter.get() + 1);

        timerActionComponent.runAtInterval(action, Duration.seconds(1));
        timerActionComponent.runOnceAfter(action, Duration.seconds(0.1f));

        timerActionComponent.clear();
        timerActionComponent.onUpdate(1.0f);
        assertEquals(0, executionCounter.get());
    }

}
