/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use viewport to fit entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ViewportZoomSample extends GameApplication {

    private GameEntity e1, e2, e3;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ViewportZoomSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                e1.translateX(-5);
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                e1.translateX(5);
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                e1.translateY(-5);
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                e1.translateY(5);
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Move Left2") {
            @Override
            protected void onAction() {
                e2.translateX(-5);
            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("Move Right2") {
            @Override
            protected void onAction() {
                e2.translateX(5);
            }
        }, KeyCode.RIGHT);

        input.addAction(new UserAction("Move Up2") {
            @Override
            protected void onAction() {
                e2.translateY(-5);
            }
        }, KeyCode.UP);

        input.addAction(new UserAction("Move Down2") {
            @Override
            protected void onAction() {
                e2.translateY(5);
            }
        }, KeyCode.DOWN);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        e1 = Entities.builder()
                .at(0, 0)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.BLUE))
                .buildAndAttach(getGameWorld());

        e2 = Entities.builder()
                .at(800, 0)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.RED))
                .buildAndAttach(getGameWorld());

        e3 = Entities.builder()
                .at(600, 560)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.GREEN))
                .buildAndAttach(getGameWorld());

        // 1. bind viewport so it can fit those entities at any time
        getGameScene().getViewport().bindToFit(40, 100, e1, e2, e3);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
