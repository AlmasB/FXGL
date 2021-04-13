/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.ui.ProgressBar;
import dev.DeveloperWASDControl;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BeatEmUpSample extends GameApplication {

    private enum Type {
        PLAYER, WEAPON, ENEMY;
    }

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.PRIMARY, () -> {
            player.call("attack");

            spawn("weapon", player.getPosition().add(150, 25));
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.DARKGRAY);

        getGameWorld().addEntityFactory(new ZombieFactory());

        for (int i = 0; i < 5; i++) {
            var e = spawn("zombie", 600, 15 + i * 140);
            e.setScaleX(-1);
        }

        player = spawn("player", 100, 100);
        player.addComponent(new DeveloperWASDControl());
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(Type.WEAPON, Type.ENEMY, (w, e) -> {
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

    public static class PlayerComponent extends Component {

        private AnimatedTexture texture;

        @Override
        public void onAdded() {
            var w = 536 / 4.0;
            var h = 495 / 4.0;

            var channel = new AnimationChannel(List.of(
                    image("anim/ninja/Attack__000.png", w, h),
                    image("anim/ninja/Attack__001.png", w, h),
                    image("anim/ninja/Attack__002.png", w, h),
                    image("anim/ninja/Attack__003.png", w, h),
                    image("anim/ninja/Attack__004.png", w, h),
                    image("anim/ninja/Attack__005.png", w, h),
                    image("anim/ninja/Attack__006.png", w, h),
                    image("anim/ninja/Attack__007.png", w, h),
                    image("anim/ninja/Attack__008.png", w, h),
                    image("anim/ninja/Attack__009.png", w, h)
            ), Duration.seconds(0.7));

            texture = new AnimatedTexture(channel);

            entity.getViewComponent().addChild(texture);

            entity.setScaleOrigin(new Point2D(w / 2.0, h / 2.0));
        }

        public void attack() {
            texture.play();
        }
    }

    public static class ZombieFactory implements EntityFactory {

        @Spawns("weapon")
        public Entity newWeapon(SpawnData data) {
            return entityBuilder(data)
                    .type(Type.WEAPON)
                    .bbox(BoundingShape.box(20, 20))
                    .collidable()
                    // TODO: based on anim
                    .with(new ExpireCleanComponent(Duration.seconds(0.3)))
                    .build();
        }

        @Spawns("player")
        public Entity newPlayer(SpawnData data) {
            var hpBar = new ProgressBar();
            hpBar.setWidth(100);
            hpBar.setHeight(15);
            hpBar.setTranslateY(-10);
            hpBar.setMaxValue(10);
            hpBar.setFill(Color.GREEN);

            var hpComp = new HealthIntComponent(10);
            hpBar.currentValueProperty().bind(hpComp.valueProperty());

            return entityBuilder(data)
                    .type(Type.PLAYER)
                    .view(hpBar)
                    .with(hpComp)
                    .with(new PlayerComponent())
                    .build();
        }

        @Spawns("zombie")
        public Entity newZombie(SpawnData data) {
            var hpBar = new ProgressBar(false);
            hpBar.setLabelVisible(false);
            hpBar.setWidth(100);
            hpBar.setHeight(25);
            hpBar.setTranslateY(-10);
            hpBar.setMaxValue(10);
            hpBar.setFill(Color.RED);

            var hpComp = new HealthIntComponent(10);
            hpBar.currentValueProperty().bind(hpComp.valueProperty());

            var w = 430 / 4.0;
            var h = 519 / 4.0;

            var channel = new AnimationChannel(List.of(
                    image("anim/Attack (1).png", w, h),
                    image("anim/Attack (2).png", w, h),
                    image("anim/Attack (3).png", w, h),
                    image("anim/Attack (4).png", w, h),
                    image("anim/Attack (5).png", w, h),
                    image("anim/Attack (6).png", w, h),
                    image("anim/Attack (7).png", w, h),
                    image("anim/Attack (8).png", w, h)
            ), Duration.seconds(0.7));

            return entityBuilder(data)
                    .type(Type.ENEMY)
                    .bbox(BoundingShape.box(w, h))
                    .view(new AnimatedTexture(channel).loop())
                    .view(hpBar)
                    .with(hpComp)
                    .collidable()
                    .scaleOrigin(w / 2.0, h / 2.0)
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
