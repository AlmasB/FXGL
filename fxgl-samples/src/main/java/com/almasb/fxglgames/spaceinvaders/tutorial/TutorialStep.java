/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.tutorial;

import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class TutorialStep {
    Duration duration;
    String fileName, hint;
    Runnable action;

    public TutorialStep(String hint, String fileName, Runnable action) {
        this.duration = Duration.seconds(3);
        this.fileName = fileName;
        this.hint = hint;
        this.action = action;
    }
}
