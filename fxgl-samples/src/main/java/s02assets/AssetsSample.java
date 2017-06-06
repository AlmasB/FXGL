/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package s02assets;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;

/**
 * Example of loading assets.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AssetsSample extends GameApplication {

    private GameEntity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AssetsSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {}

    // 1. define Texture object
    private Texture brickTexture;

    @Override
    protected void initAssets() {
        // 2. load texture
        brickTexture = getAssetLoader().loadTexture("brick.png");
    }

    @Override
    protected void initGame() {
        player = new GameEntity();
        player.getPositionComponent().setValue(400, 300);

        // 3. add texture as main view
        player.getViewComponent().setView(brickTexture);

        getGameWorld().addEntity(player);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
