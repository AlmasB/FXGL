/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s02assets;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.settings.GameSettings;

/**
 * Example of loading resized textures.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DifferentSizeTextureSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("DifferentSizeTextureSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGame() {
        Entities.builder()
                // 1. tell asset loader to load resized texture
                .viewFromNode(getAssetLoader().loadTexture("bg.jpg", 128, 128))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(300, 300)
                .viewFromNode(getAssetLoader().loadTexture("bg.jpg", 256, 128))
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
