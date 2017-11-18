/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.rts;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.MouseButton;

import java.util.Map;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RTSSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("RTSSample");
        settings.setVersion("0.1");




    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("gold", 0);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Spawn Worker") {
            @Override
            protected void onActionBegin() {
                getGameWorld().spawn("Worker", getInput().getMousePositionWorld());
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        getGameWorld().spawn("TownHall", 500, 100);
        getGameWorld().spawn("GoldMine", 100, 100);
        getGameWorld().spawn("GoldMine", 500, 400);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
