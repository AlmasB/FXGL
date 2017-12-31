/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.settings.GameSettings;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RandomSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("RandomSample");
        settings.setVersion("0.1");

        FXGLMath.getRandom().setSeed(1232323);
    }

    @Override
    protected void initGame() {
        for (int i = 0; i < 10; i++) {
            System.out.println(FXGLMath.random());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
