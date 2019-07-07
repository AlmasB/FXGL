/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.view;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.sslogger.Logger;
import com.almasb.sslogger.LoggerLevel;
import com.almasb.sslogger.LoggerOutput;
import dev.DeveloperWASDControl;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BasicEntityViewApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, "test", () -> {
            debug("Game time: " + getGameTimer().getNow());
        });
    }

    @Override
    protected void initGame() {


        //getGameScene().addUINode(new Rectangle(getAppWidth(), getAppHeight()));

        entityBuilder()
                .at(100, 100)
                .view(new Rectangle(40, 40, Color.BLUE))
                .with(new DeveloperWASDControl())
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
