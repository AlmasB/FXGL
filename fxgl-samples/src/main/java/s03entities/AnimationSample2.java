/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.AnimatedTexture;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
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
    }

    private AnimatedTexture playerTexture;

    @Override
    protected void preInit() {
        playerTexture = getAssetLoader().loadTexture("bird.png")
                .toAnimatedTexture(2, Duration.seconds(0.53));
        playerTexture.loop();
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

        getInput().addAction(new UserAction("Explosion") {
            @Override
            protected void onActionBegin() {
                Entities.builder()
                        .at(getInput().getMousePositionWorld())
                        .viewFromAnimatedTexture("explosion.png", 48, Duration.seconds(0.5), false, true)
                        .buildAndAttach(getGameWorld());
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(150, 150)
                .viewFromNode(playerTexture)
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(350, 150)
                .viewFromAnimatedTexture("explosion2.png", 16, Duration.seconds(1.0))
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
