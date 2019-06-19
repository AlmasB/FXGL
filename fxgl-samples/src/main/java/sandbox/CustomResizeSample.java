/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameScene;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CustomResizeSample extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("CustomResizeSample");
    }

    @Override
    protected void initInput() {
        Input input = FXGL.getInput();
        input.addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                player.translateY(-5.0);
            }
        }, KeyCode.W);
        input.addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.translateX(5.0);
            }
        }, KeyCode.D);
        input.addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                player.translateY(5.0);
            }
        }, KeyCode.S);
        input.addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.translateX(-5.0);
            }
        }, KeyCode.A);
    }

    @Override
    protected void initGame() {
        GameWorld gameWorld = FXGL.getGameWorld();
        gameWorld.addEntityFactory(new DemoEntityFactory());
        player = gameWorld.spawn("player", new SpawnData(200, 200));
    }

    @Override
    protected void initUI() {
        GameScene gameScene = FXGL.getGameScene();
        gameScene.addUINode(new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), new Color(.0, .0, 1.0, .5)));
    }

    public static void main(String[] args) {
        GameApplication.launch(args);
    }

    public class DemoEntityFactory implements EntityFactory {

        @Spawns("player")
        public Entity newPlayer(SpawnData data) {
            return FXGL.entityBuilder()
                    .at(data.getX(), data.getY())
                    .view(new Rectangle(100.0, 100.0, Color.GREEN))
                    .build();
        }
    }
}
