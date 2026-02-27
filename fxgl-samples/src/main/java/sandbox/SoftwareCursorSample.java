/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.ui.SoftwareCursor;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Alex Moore (alexstephenmoore@gmail.com)
 * @author Leo Waters (li0nleo117@gmail.com)
 * @author Poppy Eyres (eyres.poppy@gmail.com)
 */
public class SoftwareCursorSample extends GameApplication {
    private SoftwareCursor softwareCursor;

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        onKey(KeyCode.UP, () -> softwareCursor.translatePositionY(-5));
        onKey(KeyCode.DOWN, () -> softwareCursor.translatePositionY(5));
        onKey(KeyCode.LEFT, () -> softwareCursor.translatePositionX(-5));
        onKey(KeyCode.RIGHT, () -> softwareCursor.translatePositionX(5));
    }

    @Override
    protected void initGame() {
        //hide default cursor
        getGameScene().setCursor(Cursor.NONE);

        //create software cursor
        softwareCursor = new SoftwareCursor(Color.PINK);
        getGameScene().addUINode(softwareCursor.getCursorNode());

        //initialize cursor position
        softwareCursor.setPositionX(getGameScene().getAppWidth() / 2.0);
        softwareCursor.setPositionY(getGameScene().getAppHeight() / 2.0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
