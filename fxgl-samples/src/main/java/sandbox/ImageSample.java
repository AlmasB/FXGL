/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.texture.ImagesKt;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is a sample class for testing image rescaling.
 */
public class ImageSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(800);
        settings.setTitle("ImageSample");
    }

    @Override
    protected void initGame() {
        Image background = image("background.png");
        Image backgroundResized = ImagesKt.resize(background, 400, 300);
        addUINode(new ImageView(backgroundResized));

        // 64x64
        Image original = image("brick.png");
        for (int i = 16; i > 0; i--) {
            Image processed = ImagesKt.resize(original, 16 * i, 16 * i);
            addUINode(new ImageView(processed), 5 * i  * i, 0.3 * i * i * i);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
