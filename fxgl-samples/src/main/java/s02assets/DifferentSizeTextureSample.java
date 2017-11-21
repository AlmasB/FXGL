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
    }

    @Override
    protected void initGame() {
        // the actual size of texture brick is 64x64

        Entities.builder()
                .at(200, 300)
                // 1. tell asset loader to load resized texture
                .viewFromNode(getAssetLoader().loadTexture("brick.png", 32, 32))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(300, 300)
                // you can also load with different ratio
                .viewFromNode(getAssetLoader().loadTexture("brick.png", 64, 32))
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
