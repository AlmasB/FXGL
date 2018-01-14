/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.EffectControl;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.Map;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AnimSample extends GameApplication {

    // 1. define types of entities in the game using Enum
    private enum Type {
        PLAYER
    }

    // make the field instance level
    // but do NOT init here for properly functioning save-load system
    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AnimSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("key", 0);
    }

    @Override
    protected void initGame() {


        getGameState().<Integer>addListener("key", (prev, now) -> {
            System.out.println(prev + " " + now);
        });
    }

    private void anim() {
        Entity life = Entities.builder()
                .at(200, 200)
                .viewFromTexture("life.png")
                .buildAndAttach();

        Entity life2 = Entities.builder()
                .at(200, 200)
                .viewFromTexture("life.png")
                .buildAndAttach();

        Entity life3 = Entities.builder()
                .at(200, 200)
                .viewFromTexture("life.png")
                .buildAndAttach();

        Entities.animationBuilder()
                .delay(Duration.seconds(1))
                .autoReverse(true)
                .repeat(2)
                .duration(Duration.seconds(0.36))
                .translate(life3)
                .from(new Point2D(200, 200))
                .to(new Point2D(200, 190))
                .buildAndPlay();

        Entities.animationBuilder()
                .delay(Duration.seconds(1.2))
                .autoReverse(true)
                .repeat(2)
                .duration(Duration.seconds(0.36))
                .translate(life2)
                .from(new Point2D(200, 200))
                .to(new Point2D(200, 190))
                .buildAndPlay();

        Entities.animationBuilder()
                .delay(Duration.seconds(1.4))
                .autoReverse(true)
                .repeat(2)
                .duration(Duration.seconds(0.36))
                .translate(life)
                .from(new Point2D(200, 200))
                .to(new Point2D(200, 190))
                .buildAndPlay();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
