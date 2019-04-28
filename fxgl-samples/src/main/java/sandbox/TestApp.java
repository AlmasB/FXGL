/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TestApp extends Application {

    private static final int DOUBLE_PRESS_DELAY_MILLIS = 150;

    private List<KeyData> activeKeys = new ArrayList<>();

    private Parent createContent() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                var nowMillis = System.currentTimeMillis();

                var it = activeKeys.iterator();
                while (it.hasNext()) {
                    var data = it.next();

                    if (nowMillis - data.timePressedMillis > DOUBLE_PRESS_DELAY_MILLIS) {
                        onSinglePress(data.key);
                        it.remove();
                    }
                }
            }
        };
        timer.start();

        return new Pane();
    }

    private static class KeyData {
        private KeyCode key;
        private long timePressedMillis;

        KeyData(KeyCode key, long timePressedMillis) {
            this.key = key;
            this.timePressedMillis = timePressedMillis;
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        var scene = new Scene(createContent(), 800, 600);
        scene.setOnKeyPressed(event -> {
            var key = event.getCode();
            var now = System.currentTimeMillis();

            var keyAlreadyPressed = activeKeys.stream()
                    .anyMatch(data -> data.key == key);

            if (keyAlreadyPressed) {
                onDoublePress(key);
                activeKeys.removeIf(data -> data.key == key);
            } else {
                activeKeys.add(new KeyData(key, now));
            }
        });

        stage.setScene(scene);
        stage.show();
    }

    private void onSinglePress(KeyCode key) {
        System.out.println("Single press: " + key);
    }

    private void onDoublePress(KeyCode key) {
        System.out.println("Double press: " + key);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
