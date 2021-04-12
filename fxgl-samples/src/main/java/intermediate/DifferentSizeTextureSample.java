/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.texture.Texture;

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
                .at(50, 500)
                // 1. tell asset loader to load resized texture
                .view(getAssetLoader().loadTexture("brick.png", 32, 32))
                .buildAndAttach();

        entityBuilder()
                .at(150, 500)
                // you can also load with different ratio
                .view(getAssetLoader().loadTexture("brick.png", 64, 32))
                .buildAndAttach();

        // you can also resize textures after loading

        var original = image("brick.png");
        for (int i = 1; i < 12; i++) {
            var resized = ImagesKt.resize(original, 16 * i, 16 * i);

            entityBuilder()
                    .at(5 * i  * i, 0.3 * i * i * i)
                    .view(new Texture(resized))
                    .buildAndAttach();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
