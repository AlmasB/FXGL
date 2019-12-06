/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.ImagesKt;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
        // Image obtained from internet.
        Image background = new Image("https://www.empiraft.com/zh_cn/new/images/background.png");
        Image backgroundResized = ImagesKt.resize(background, 400, 200);
        FXGL.addUINode(new ImageView( backgroundResized ), 400, 0);

        // Image obtained from internet.
        Image original = new Image("https://www.empiraft.com/zh_cn/new/images/icon-game.png");
        // Demo to resize image, note: original image from url ("https://www.empiraft.com/zh_cn/new/images/icon-game.png") is 64px * 64px
        for (int i = 16; i > 0; i--) {
            Image processed = ImagesKt.resize(original, 16 * i, 16 * i);
            FXGL.addUINode(new ImageView( processed ), 5 * i  * i, 0.3 * i * i * i);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
