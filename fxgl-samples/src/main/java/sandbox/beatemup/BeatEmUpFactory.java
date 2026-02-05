/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.beatemup;

import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.KeepInBoundsComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.ui.ProgressBar;
import dev.DeveloperWASDControl;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BeatEmUpFactory implements EntityFactory {

    @Spawns("weapon")
    public Entity newWeapon(SpawnData data) {
        return entityBuilder(data)
                .type(BeatEmUpEntityType.WEAPON)
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

        var shadow = new Ellipse(30, 10, 30, 10);
        shadow.setFill(Color.color(0, 0, 0, 0.5));
        shadow.setTranslateX(5);
        shadow.setTranslateY(102);

        return entityBuilder(data)
                .type(BeatEmUpEntityType.PLAYER)
                .bbox(BoundingShape.box(100, 100))
                .view(hpBar)
                .with(hpComp)
                .with(new KeepInBoundsComponent(new Rectangle2D(0, getAppHeight() - 200, getAppWidth() - 100, 170)))
                .view(shadow)
                .with(new PlayerComponent())
                .with(new DeveloperWASDControl())
                //.view(new Rectangle(100, 100, Color.BLUE))
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
                .type(BeatEmUpEntityType.ENEMY)
                .bbox(BoundingShape.box(w, h))
                .view(new AnimatedTexture(channel).loop())
                .view(hpBar)
                .with(hpComp)
                .collidable()
                .scaleOrigin(w / 2.0, h / 2.0)
                .build();
    }
}