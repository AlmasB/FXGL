/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.util.Duration;

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
    protected void initGame() {
        // 2. create entity and attach to world using fluent API
//        player = Entities.builder()
//                .type(Type.PLAYER)
//                .at(100, 100)
//                .viewFromNode(new Rectangle(40, 40))
//                .buildAndAttach(getGameWorld());

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
