/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CustomPathAssetsSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setBasePackageForAssets("com.almasb.testpackage");
    }

    @Override
    protected void initGame() {
        entityBuilder()
                .view("testcoin.png")
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
