/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.beatemup;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how a beat'em up type game can be put together.
 * All assets are under the src/main/resources/assets/beatemup directory.
 *
 * Background image: https://chris33556.itch.io/urban-streets-level-1
 * Characters: https://pzuh.itch.io/
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
public class BeatEmUpSample extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(600);
    }

    @Override
    protected void initInput() {
        // TODO: spawn, on animation finish?
        onBtnDownPrimary(() -> {
            player.call("attack");

            // - the half bbox
            spawn("weapon", player.getCenter().add(60 * player.getScaleX() - 10, 25 - 10));
        });
    }

    @Override
    protected void initGame() {
        entityBuilder()
                .view(texture("beatemup/background.png").subTexture(new Rectangle2D(0, 0, 1280, 600)).desaturate())
                .buildAndAttach();

        getGameWorld().addEntityFactory(new BeatEmUpFactory());

        spawn("zombie", 600, getAppHeight() - 200);

        player = spawn("player", 100, getAppHeight() - 150);
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(BeatEmUpEntityType.WEAPON, BeatEmUpEntityType.ENEMY, (w, e) -> {
            var hp = e.getComponent(HealthIntComponent.class);
            hp.damage(2);

            animationBuilder()
                    .interpolator(Interpolators.BOUNCE.EASE_OUT())
                    .duration(Duration.seconds(0.25))
                    .translate(e)
                    .from(e.getPosition())
                    .to(e.getPosition().add(5, 0))
                    .buildAndPlay();

            if (hp.isZero()) {
                e.removeFromWorld();
            }
        });
    }

//    @Override
//    protected void initUI() {
//        var texture = new ColoredTexture(320, 320, Color.BLACK)
//                .superTexture(new ColoredTexture(320, 320, Color.WHITE), HorizontalDirection.RIGHT)
//                .superTexture(new ColoredTexture(320, 320, Color.RED), HorizontalDirection.RIGHT)
//                .superTexture(new ColoredTexture(320, 320, Color.GOLD), HorizontalDirection.RIGHT)
//                .superTexture(new ColoredTexture(320, 320, Color.BLUE), HorizontalDirection.RIGHT)
//                .superTexture(new ColoredTexture(320, 320, Color.GREEN), HorizontalDirection.RIGHT)
//                .toAnimatedTexture(6, Duration.seconds(0.6));
//        texture.loop();
//
//        addUINode(texture);
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
