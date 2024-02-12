/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.quest.Quest;
import com.almasb.fxgl.quest.QuestService;
import com.almasb.fxgl.ui.SoftwareCursor;
import com.sun.glass.events.MouseEvent;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Alex Moore (alexstephenmoore@gmail.com)
 * @author Leo Waters (li0nleo117@gmail.com)
 * @author Poppy Eyres (eyres.poppy@gmail.com)
 */
public class SoftwareCursorSample extends GameApplication {
    SoftwareCursor Cursor;

    @Override
    protected void initSettings(GameSettings settings) {
      //  settings.addEngineService(QuestService.class);
    }


    @Override
    protected void initInput() {
        //hide default cursor
        getGameScene().getRoot().setCursor(javafx.scene.Cursor.NONE);
    }
    @Override
    protected void initGame() {
        //create software cursor
        Cursor = new SoftwareCursor(Color.PINK);
        getGameScene().getRoot().getChildren().add(Cursor.getCursorNode());

        //initialize cursor position
        Cursor.setPositionX((double) getGameScene().getAppWidth() / 2);
        Cursor.setPositionY((double) getGameScene().getAppHeight() / 2);
    }



    @Override
    protected void onUpdate(double tpf) {
        //Cursor.setPosition(getInput().getMouseXWorld(), getInput().getMouseYWorld());
        //Cursor.setPositionX(getInput().getMouseXWorld());
        //Cursor.setPositionY(getInput().getMouseYWorld());
        Cursor.translatePosition(tpf,tpf);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
