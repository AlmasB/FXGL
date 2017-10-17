/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.component.ColorComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * This is an example of a minimalistic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AnimColorSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AnimColorSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setProfilingEnabled(false);
    }

    @Override
    protected void initGame() {
        Rectangle playerView = new Rectangle(40, 40);

        Entity player = Entities.builder()
                .at(100, 100)
                .viewFromNode(playerView)
                .with(new ColorComponent())
                .buildAndAttach();

        playerView.fillProperty().bind(player.getComponent(ColorComponent.class).valueProperty());

        Entities.animationBuilder()
                .duration(Duration.seconds(2))
                .repeat(4)
                .color(player)
                .fromColor(Color.AQUA)
                .toColor(Color.BURLYWOOD)
                .buildAndPlay();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
