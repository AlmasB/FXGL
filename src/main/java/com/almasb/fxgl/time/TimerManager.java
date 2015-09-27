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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.almasb.fxgl.time.TimerAction.TimerType;
import com.almasb.fxgl.util.UpdateTickListener;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

public final class TimerManager implements UpdateTickListener {

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
     * @param seconds
     * @return
     */
    public static long secondsToNanos(double seconds) {
        return (long)(seconds * 1000000000L);
    }

    /**
     * Converts type Duration to nanoseconds.
     *
     * @param duration
     * @return
     */
    public static long toNanos(Duration duration) {
        return secondsToNanos(duration.toSeconds());
    }

    /**
     * List for all timer based actions
     */
    private List<TimerAction> timerActions = new CopyOnWriteArrayList<>();

    /**
     * Holds current tick (frame)
     */
    private ReadOnlyLongWrapper tick = new ReadOnlyLongWrapper(0);

    public ReadOnlyLongProperty tickProperty() {
        return tick.getReadOnlyProperty();
    }

    /**
     * Returns current tick (frame). When the game has just started,
     * the first cycle in the loop will have tick == 1,
     * second cycle - 2 and so on.
     *
     * The update to this number happens when a new update cycle starts.
     *
     * @return current tick
     */
    public long getTick() {
        return tick.get();
    }

    /**
     * Resets current tick to 0.
     */
    public void resetTicks() {
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
     * @return
     */
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
     *
     * @return average render FPS
     */
    public int getFPS() {
        return fps.get();
    }

    /**
     *
     * @return average render FPS property
     */
    public IntegerProperty fpsProperty() {
        return fps;
    }

    /**
     * Average performance FPS
     */
    private IntegerProperty performanceFPS = new SimpleIntegerProperty();

    /**
     *
     * @return Average performance FPS
     */
    public int getPerformanceFPS() {
        return performanceFPS.get();
    }

    /**
     *
     * @return Average performance FPS property
     */
    public IntegerProperty performanceFPSProperty() {
        return performanceFPS;
    }

    private long startNanos = -1;
    private long realFPS = -1;

    /**
     * Called at the start of a game update tick.
     * This is where tick becomes tick + 1.
     *
     * @param internalTime
     */
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
    public void tickEnd() {
        performanceFPS.set(Math.round(fpsPerformanceCounter.count(secondsToNanos(1) / (System.nanoTime() - startNanos))));
        fps.set(Math.round(fpsCounter.count(secondsToNanos(1) / realFPS)));
    }

    @Override
    public void onUpdate() {
        timerActions.forEach(action -> action.update(now));
        timerActions.removeIf(TimerAction::isExpired);
    }

    /**
     * The Runnable action will be scheduled to run at given interval.
     * The action will run for the first time after given interval.
     *
     * Note: the scheduled action will not run while the game is paused.
     *
     * @param action the action
     * @param interval time
     */
    public void runAtInterval(Runnable action, Duration interval) {
        timerActions.add(new TimerAction(getNow(), interval, action, TimerType.INDEFINITE));
    }

    /**
     * The Runnable action will be scheduled for execution iff
     * whileCondition is initially true. If that's the case
     * then the Runnable action will be scheduled to run at given interval.
     * The action will run for the first time after given interval
     *
     * The action will be removed from schedule when whileCondition becomes {@code false}.
     *
     * Note: the scheduled action will not run while the game is paused
     *
     * @param action
     * @param interval
     * @param whileCondition
     */
    public void runAtIntervalWhile(Runnable action, Duration interval, ReadOnlyBooleanProperty whileCondition) {
        if (!whileCondition.get()) {
            return;
        }
        TimerAction act = new TimerAction(getNow(), interval, action, TimerType.INDEFINITE);
        timerActions.add(act);

        whileCondition.addListener((obs, old, newValue) -> {
            if (!newValue.booleanValue())
                act.expire();
        });
    }

    /**
     * The Runnable action will be executed once after given delay
     *
     * Note: the scheduled action will not run while the game is paused
     *
     * @param action
     * @param delay
     */
    public void runOnceAfter(Runnable action, Duration delay) {
        timerActions.add(new TimerAction(getNow(), delay, action, TimerType.ONCE));
    }

    /**
     * Clears all registered timer based actions.
     */
    public void clearActions() {
        timerActions.clear();
    }

    public Timer newTimer() {
        return new Timer(this);
    }
}
