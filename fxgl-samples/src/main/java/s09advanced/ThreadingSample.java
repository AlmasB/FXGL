/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s09advanced;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.settings.GameSettings;
import javafx.concurrent.Task;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use multiple threads.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ThreadingSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ThreadingSample");
        settings.setVersion("0.1");





    }

    // 1. isolate code that represents some heavy work
    // ensure it doesn't modify world or the scene graph
    private void doHeavyWork() throws Exception {
        Thread.sleep(2000);
    }

    @Override
    protected void initGame() {
        // 2. get executor service
        // 3. create a new task that performs heavy work

        FXGL.getExecutor().execute(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("Heavy work started!");
                doHeavyWork();
                return null;
            }

            @Override
            protected void succeeded() {
                // 4. it is OK to modify world/scene graph here
                System.out.println("Heavy work complete!");

                Entities.builder()
                        .at(300, 300)
                        .viewFromNode(new Rectangle(40, 40))
                        .buildAndAttach(getGameWorld());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
