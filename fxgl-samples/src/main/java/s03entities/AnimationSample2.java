/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.AnimatedTexture;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * Shows how to use sprite sheet animations.
 * Shows how to properly dispose of textures.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AnimationSample2 extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AnimationSample2");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Clean") {
            @Override
            protected void onActionBegin() {

                if (playerTexture != null) {

                    // when cleaning first remove the entity using the texture
                    getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);

                    // dispose of the texture
                    playerTexture.dispose();

                    // nullify the reference to texture
                    playerTexture = null;
                }
            }
        }, KeyCode.F);
    }

    private AnimatedTexture playerTexture;

    @Override
    protected void initAssets() {
        playerTexture = getAssetLoader().loadTexture("bird.png")
                .toAnimatedTexture(2, Duration.seconds(0.33));
    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(150, 150)
                .viewFromNode(playerTexture)
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
