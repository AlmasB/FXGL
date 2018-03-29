/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Effect;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.TimeComponent;
import com.almasb.fxgl.extra.entity.controls.CircularMovementControl;
import com.almasb.fxgl.entity.control.EffectControl;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

/**
 * Shows how to init a basic game object and attach it to the world
 * using fluent API.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EffectSample extends GameApplication {


    // make the field instance level
    // but do NOT init here for properly functioning save-load system
    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("InitSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initInput() {

        getInput().addAction(new UserAction("Print Line") {
            @Override
            protected void onActionBegin() {
                player.getComponent(EffectControl.class).startEffect(new SlowEffect());
            }
        }, KeyCode.F);
    }

    @Override
    protected void initGame() {
        player = Entities.builder()
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .with(new TimeComponent(1.0))
                .with(new CircularMovementControl(10, 10), new EffectControl())
                .buildAndAttach(getGameWorld());
    }

    private class SlowEffect extends Effect {

        public SlowEffect() {
            super(Duration.seconds(2));
        }

        @Override
        public void onStart(@NotNull Entity entity) {
            entity.getComponent(TimeComponent.class).setValue(0.2);
        }

        @Override
        public void onEnd(@NotNull Entity entity) {
            entity.getComponent(TimeComponent.class).setValue(1.0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
