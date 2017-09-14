/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.util.InputPredicates;
import javafx.scene.input.KeyCode;

/**
 * Shows an example of FXGL dialogs.
 * Press F to open a dialog.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DialogsSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("DialogsSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Dialog") {
            @Override
            protected void onActionBegin() {
                // 1. get display service and use one of the show* methods
                // this shows a dialog that only takes alphanumerical characters as input
                getDisplay().showInputBox("Simple Dialog", InputPredicates.ALPHANUM, System.out::println);
            }
        }, KeyCode.F);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
