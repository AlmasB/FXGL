/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.dialogue;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;

/**
 * WIP basics of a dialogue editor
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EditorSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("EditorSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initInput() {

    }

    @Override
    protected void initGame() {

//        Async<Void> async = getExecutor().async(() -> {
//            System.out.println("hello");
//        });




        getGameScene().addUINodes(new DialoguePane());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
