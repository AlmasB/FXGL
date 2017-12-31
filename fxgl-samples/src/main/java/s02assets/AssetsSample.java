/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package s02assets;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;

/**
 * Example of loading assets.
 * There are 3 ways to use assets.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AssetsSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AssetsSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGame() {
        approach1();
        approach2();
        approach3();
    }

    private void approach1() {
        // load texture
        Texture brickTexture = getAssetLoader().loadTexture("brick.png");

        Entity brick = new Entity();
        brick.setPosition(100, 300);

        // set texture as main view
        brick.setView(brickTexture);

        getGameWorld().addEntity(brick);
    }

    private void approach2() {
        Entity brick2 = new Entity();
        brick2.setPosition(200, 300);

        // if you don't need texture reference
        brick2.setViewFromTexture("brick.png");

        getGameWorld().addEntity(brick2);
    }

    private void approach3() {
        // OR use fluent API
        Entities.builder()
                .at(300, 300)
                .viewFromTexture("brick.png")
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
