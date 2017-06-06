/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s09advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.concurrent.Async;
import com.almasb.fxgl.settings.GameSettings;

/**
 * This is an example of using async tasks.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AsyncSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AsyncSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
    }

    @Override
    protected void initGame() {

        // arbitrary example

        Async<Integer> async = getExecutor().async(() -> {
            System.out.println("AI thread: " + Thread.currentThread().getName());
            System.out.println("AI tick");
            Thread.sleep(2000);
            System.out.println("AI Done");
            return 999;
        });

        Async<Double> async2 = getExecutor().async(() -> {
            System.out.println("Render thread: " + Thread.currentThread().getName());
            System.out.println("Render tick");
            Thread.sleep(300);
            System.out.println("Render Done");
            return 399.0;
        });

        Async<Void> async3 = getExecutor().async(() -> {
            System.out.println("Running some code");
        });

        System.out.println("Physics thread: " + Thread.currentThread().getName());
        System.out.println("Physics tick Done. Waiting for AI & Render");

        int value = async.await();
        double value2 = async2.await();

        System.out.println("Values: " + value + " " + value2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
