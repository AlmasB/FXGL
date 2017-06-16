/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.scifi;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.tiled.TiledMap;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScifiSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(640);
        settings.setHeight(640);
        settings.setTitle("ScifiSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
    }

    private GameEntity player;
    private PlayerControl playerControl;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Use") {
            @Override
            protected void onActionBegin() {
                playerControl.punch();
//                getGameWorld().getCollidingEntities(player)
//                        .stream()
//                        .filter(e -> e.hasControl(UsableControl.class))
//                        .map(e -> e.getControl(UsableControl.class))
//                        .forEach(UsableControl::use);
            }
        }, KeyCode.F);

        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                playerControl.jump();
            }
        }, KeyCode.W);
    }

    @Override
    protected void initGame() {
        TiledMap map = getAssetLoader().loadJSON("test_level.json", TiledMap.class);

        getGameWorld().setLevelFromMap(map);

        getGameWorld().spawn("button", 30, 340);

        player = (GameEntity) getGameWorld().spawn("player", 100, 100);
        playerControl = player.getControl(PlayerControl.class);

        getGameWorld().addEntities(Entities.makeScreenBounds(40));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
