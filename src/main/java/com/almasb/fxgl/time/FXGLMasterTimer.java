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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.time;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.event.EventBus;
import com.almasb.fxgl.event.Events;
import com.almasb.fxgl.event.FXGLEvent;
import com.almasb.fxgl.event.WorldEvent;
import com.almasb.fxgl.time.TimerAction.TimerType;
import com.almasb.fxgl.util.FXGLLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Contains convenience methods and manages timer based actions.
 * Computes time taken by each frame.
 * Keeps track of tick and global time.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
public class FXGLMasterTimer extends AnimationTimer implements MasterTimer {

    private static final Logger log = FXGLLogger.getLogger("FXGL.MasterTimer");

    private EventBus eventBus;

    @Inject
    private FXGLMasterTimer(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.addEventHandler(WorldEvent.ENTITY_ADDED, event -> {
            Entity entity = event.getEntity();
            Duration expire = entity.getExpireTime();
            if (expire != Duration.ZERO)
                runOnceAfter(entity::removeFromWorld, expire);
        });
        eventBus.addEventHandler(FXGLEvent.RESET, event -> {
            resetTicks();
            clearActions();
        });

        eventBus.addEventHandler(FXGLEvent.INIT_APP_COMPLETE, event -> start());
        eventBus.addEventHandler(FXGLEvent.RESUME, event -> start());
        eventBus.addEventHandler(FXGLEvent.PAUSE, event -> stop());

        log.finer("Service [MasterTimer] initialized");
    }

    /**
     * Time per frame in seconds.
     *
     * @return 0.166(6)
     */
    public static double tpfSeconds() {
        return 1.0 / 60;
    }

    /**
     * Timer per frame in nanoseconds.
     *
     * @return 16666666
     */
    public static long tpfNanos() {
        return 1000000000L / 60;
    }

    /**
     * Converts seconds to nanoseconds.
     *
     * @param seconds value in seconds
     * @return value in nanoseconds
     */
    public static long secondsToNanos(double seconds) {
        return (long) (seconds * 1000000000L);
    }

    /**
     * Converts type Duration to nanoseconds.
     *
     * @param duration value as Duration
     * @return value in nanoseconds
     */
    public static long toNanos(Duration duration) {
        return secondsToNanos(duration.toSeconds());
    }

    /**
     * This is the internal FXGL update tick,
     * executed 60 times a second ~ every 0.166 (6) seconds.
     *
     * @param internalTime - The timestamp of the current frame given in nanoseconds (from JavaFX)
     */
    @Override
    public void handle(long internalTime) {
        // this will set up current tick and current time
        // for the rest of the game modules to use
        tickStart(internalTime);

        timerActions.forEach(action -> action.update(now));
        timerActions.removeIf(TimerAction::isExpired);

        // this is the master update event
        eventBus.fireEvent(Events.UPDATE_EVENT);

        // this is only end for our processing tick for basic profiling
        // the actual JavaFX tick ends when our new tick begins. So
        // JavaFX event callbacks will properly fire within the same "our" tick.
        tickEnd();
    }

    /**
     * List for all timer based actions
     */
    private List<TimerAction> timerActions = new CopyOnWriteArrayList<>();

    /**
     * Holds current tick (frame)
     */
    private ReadOnlyLongWrapper tick = new ReadOnlyLongWrapper(0);

    /**
     *
     * @return tick
     */
    @Override
    public ReadOnlyLongProperty tickProperty() {
        return tick.getReadOnlyProperty();
    }

    /**
     * Returns current tick (frame). When the game has just started,
     * the first cycle in the loop will have tick == 1,
     * second cycle - 2 and so on.
     * <p>
     * The update to this number happens when a new update cycle starts.
     *
     * @return current tick
     */
    @Override
    public long getTick() {
        return tick.get();
    }

    /**
     * Resets current tick to 0.
     */
    @Override
    public void resetTicks() {
        log.finer("Resetting ticks to 0");

        tick.set(0);
    }

    /**
     * Time for this tick in nanoseconds.
     */
    private long now = 0;

    /**
     * Current time for this tick in nanoseconds. Also time elapsed
     * from the start of game. This time does not change while the game is paused.
     * This time does not change while within the same tick.
     *
     * @return current time in nanoseconds
     */
    @Override
    public long getNow() {
        return now;
    }

    /**
     * These are used to approximate FPS value
     */
    private FPSCounter fpsCounter = new FPSCounter();
    private FPSCounter fpsPerformanceCounter = new FPSCounter();

    /**
     * Used as delta from internal JavaFX timestamp to calculate render FPS
     */
    private long fpsTime = 0;

    /**
     * Average render FPS
     */
    private IntegerProperty fps = new SimpleIntegerProperty();

    /**
     * @return average render FPS
     */
    @Override
    public int getFPS() {
        return fps.get();
    }

    /**
     * @return average render FPS property
     */
    @Override
    public IntegerProperty fpsProperty() {
        return fps;
    }

    /**
     * Average performance FPS
     */
    private IntegerProperty performanceFPS = new SimpleIntegerProperty();

    /**
     * @return Average performance FPS
     */
    @Override
    public int getPerformanceFPS() {
        return performanceFPS.get();
    }

    /**
     * @return Average performance FPS property
     */
    @Override
    public IntegerProperty performanceFPSProperty() {
        return performanceFPS;
    }

    private long startNanos = -1;
    private long realFPS = -1;

    /**
     * Called at the start of a game update tick.
     * This is where tick becomes tick + 1.
     *
     * @param internalTime internal JavaFX time
     */
    @Override
    public void tickStart(long internalTime) {
        tick.set(tick.get() + 1);
        now = (getTick() - 1) * tpfNanos();
        startNanos = System.nanoTime();
        realFPS = internalTime - fpsTime;
        fpsTime = internalTime;
    }

    /**
     * Called at the end of a game update tick.
     */
    @Override
    public void tickEnd() {
        performanceFPS.set(Math.round(fpsPerformanceCounter.count(secondsToNanos(1) / (System.nanoTime() - startNanos))));
        fps.set(Math.round(fpsCounter.count(secondsToNanos(1) / realFPS)));
    }

    /**
     * The Runnable action will be scheduled to run at given interval.
     * The action will run for the first time after given interval.
     * <p>
     * Note: the scheduled action will not run while the game is paused.
     *
     * @param action   the action
     * @param interval time
     */
    @Override
    public void runAtInterval(Runnable action, Duration interval) {
        timerActions.add(new TimerAction(getNow(), interval, action, TimerType.INDEFINITE));
    }

    /**
     * The Runnable action will be scheduled for execution iff
     * whileCondition is initially true. If that's the case
     * then the Runnable action will be scheduled to run at given interval.
     * The action will run for the first time after given interval
     * <p>
     * The action will be removed from schedule when whileCondition becomes {@code false}.
     * <p>
     * Note: the scheduled action will not run while the game is paused
     *
     * @param action         action to execute
     * @param interval       interval between executions
     * @param whileCondition condition
     */
    @Override
    public void runAtIntervalWhile(Runnable action, Duration interval, ReadOnlyBooleanProperty whileCondition) {
        if (!whileCondition.get()) {
            return;
        }
        TimerAction act = new TimerAction(getNow(), interval, action, TimerType.INDEFINITE);
        timerActions.add(act);

        whileCondition.addListener((obs, old, isTrue) -> {
            if (!isTrue)
                act.expire();
        });
    }

    /**
     * The Runnable action will be executed once after given delay
     * <p>
     * Note: the scheduled action will not run while the game is paused
     *
     * @param action action to execute
     * @param delay  delay after which to execute
     */
    @Override
    public void runOnceAfter(Runnable action, Duration delay) {
        timerActions.add(new TimerAction(getNow(), delay, action, TimerType.ONCE));
    }

    /**
     * Clears all registered timer based actions.
     */
    @Override
    public void clearActions() {
        log.finer("Clearing all scheduled actions");
        timerActions.clear();
    }
}
