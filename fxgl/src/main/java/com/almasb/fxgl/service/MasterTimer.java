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

package com.almasb.fxgl.service;

import com.almasb.fxgl.service.listener.UserProfileSavable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;

/**
 * Represents master timer, all local timers are synchronized with this.
 * Allows to set up interval based tasks.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface MasterTimer extends UserProfileSavable {

    /**
     * Current time for this tick in nanoseconds. Also time elapsed
     * from the start of game. This time does not change while the game is paused.
     * This time does not change while within the same tick.
     *
     * @return current time in nanoseconds
     */
    long getNow();

    default long getPlaytime() {
        return playtimeProperty().get();
    }

    default long getPlaytimeHours() {
        return (long) (getPlaytime() / 1000000000.0 / 3600);
    }

    default long getPlaytimeMinutes() {
        return (long) (getPlaytime() / 1000000000.0 / 60) % 60;
    }

    default long getPlaytimeSeconds() {
        return (long) (getPlaytime() / 1000000000.0) % 60;
    }

    ReadOnlyLongProperty playtimeProperty();

    /**
     * Returns current tick (frame). When the game has just started,
     * the first cycle in the loop will have tick == 1,
     * second cycle - 2 and so on.
     * <p>
     * The update to this number happens when a new update cycle starts.
     *
     * @return current tick
     */
    default long getTick() {
        return tickProperty().get();
    }

    /**
     * @return time per frame
     */
    double tpf();

    /**
     *
     * @return current tick property
     */
    ReadOnlyLongProperty tickProperty();

    /**
     * @return average render FPS
     */
    default int getFPS() {
        return fpsProperty().get();
    }

    /**
     * @return average render FPS property
     */
    IntegerProperty fpsProperty();

    /**
     * @return Average performance FPS
     */
    default int getPerformanceFPS() {
        return performanceFPSProperty().get();
    }

    /**
     * @return Average performance FPS property
     */
    IntegerProperty performanceFPSProperty();



    void pause();

    void startMainLoop();

    void resume();

    /**
     * Clears all registered timer based actions.
     */
    void reset();
}
