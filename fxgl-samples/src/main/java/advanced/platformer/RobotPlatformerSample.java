/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced.platformer;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RobotPlatformerSample extends GameApplication {

    private RobotComponent getControl() {
        return getGameWorld().getSingleton(e -> e.hasComponent(RobotComponent.class))
                .getComponent(RobotComponent.class);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Robot platformer sample");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setDeveloperMenuEnabled(true);
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> getControl().jump());
        onKey(KeyCode.F, () -> getControl().roll());
        onKey(KeyCode.X, () -> getControl().die());

        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                getControl().walkLeft();
            }

            @Override
            protected void onActionEnd() {
                getControl().stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                getControl().walkRight();
            }

            @Override
            protected void onActionEnd() {
                getControl().stop();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Run Left") {
            @Override
            protected void onAction() {
                getControl().runLeft();
            }

            @Override
            protected void onActionEnd() {
                getControl().stop();
            }
        }, KeyCode.Q);

        getInput().addAction(new UserAction("Run Right") {
            @Override
            protected void onAction() {
                getControl().runRight();
            }

            @Override
            protected void onActionEnd() {
                getControl().stop();
            }
        }, KeyCode.E);

        getInput().addAction(new UserAction("Crouch Left") {
            @Override
            protected void onAction() {
                getControl().crouchLeft();
            }

            @Override
            protected void onActionEnd() {
                getControl().stop();
            }
        }, KeyCode.Z);

        getInput().addAction(new UserAction("Crouch Right") {
            @Override
            protected void onAction() {
                getControl().crouchRight();
            }

            @Override
            protected void onActionEnd() {
                getControl().stop();
            }
        }, KeyCode.C);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new RobotFactory());

        setLevelFromMap("robot/level1.tmx");

        entityBuilder().buildScreenBoundsAndAttach(20);

        spawn("robot", 200, 100);

        getPhysicsWorld().setGravity(0, 1250);
    }

    @Override
    protected void initUI() {
        var text = getUIFactoryService().newText("", Color.BLACK, 26.0);
        text.textProperty().bind(
                getGameWorld()
                        .getEntitiesByComponent(StateComponent.class)
                        .get(0)
                        .getComponent(StateComponent.class)
                        .currentStateProperty()
                        .asString("State: %s")
        );

        addUINode(text, 50, 50);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
