/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.tutorial;

import com.almasb.fxgl.app.FXGL;
import javafx.scene.text.Text;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Tutorial {

    private Text uiText;
    private Runnable onFinish;

    private Queue<TutorialStep> tutorialSteps = new ArrayDeque<>();

    public Tutorial(Text uiText, Runnable onFinish, TutorialStep... steps) {
        this.uiText = uiText;
        this.onFinish = onFinish;
        tutorialSteps.addAll(Arrays.asList(steps));
    }

    public void play() {
        playStep(tutorialSteps.poll());
    }

    private void playStep(TutorialStep step) {
        uiText.setText(step.hint);
        step.action.run();

        FXGL.getAudioPlayer().playMusic(step.fileName);

        FXGL.getMasterTimer().runOnceAfter(() -> {
            if (!tutorialSteps.isEmpty()) {
                playStep(tutorialSteps.poll());
            } else {
                onFinish.run();
            }
        }, step.duration);
    }
}
