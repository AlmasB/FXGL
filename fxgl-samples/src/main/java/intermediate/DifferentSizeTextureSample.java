/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Example of loading resized textures.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DifferentSizeTextureSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        // the actual size of texture brick is 64x64

        entityBuilder()
                .at(200, 300)
                // 1. tell asset loader to load resized texture
                .view(getAssetLoader().loadTexture("brick.png", 32, 32))
                .buildAndAttach();

        entityBuilder()
                .at(300, 300)
                // you can also load with different ratio
                .view(getAssetLoader().loadTexture("brick.png", 64, 32))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
