/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;

public class CursorSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(400);
        settings.setTitle("ImageSample");
    }

    @Override
    protected void initGame() {
        FXGL.getGameScene().setCursorInvisible();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
