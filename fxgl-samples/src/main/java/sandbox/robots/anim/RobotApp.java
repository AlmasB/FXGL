/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.robots.anim;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.RaycastResult;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import static com.almasb.fxgl.app.DSLKt.spawn;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RobotApp extends GameApplication {

    private RobotComponent robot;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1000);
        settings.setHeight(800);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                robot.left();
            }

            @Override
            protected void onActionEnd() {
                robot.stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                robot.right();
            }

            @Override
            protected void onActionEnd() {
                robot.stop();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Run Left") {
            @Override
            protected void onAction() {
                robot.runLeft();
            }

            @Override
            protected void onActionEnd() {
                robot.stop();
            }
        }, KeyCode.Q);

        getInput().addAction(new UserAction("Run Right") {
            @Override
            protected void onAction() {
                robot.runRight();
            }

            @Override
            protected void onActionEnd() {
                robot.stop();
            }
        }, KeyCode.E);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                robot.jump();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Crouch") {
            @Override
            protected void onActionBegin() {
                robot.crouch();
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                robot.shoot();
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new RobotFactory());

        Entity r = spawn("robot", 100, 0);
        robot = r.getComponent(RobotComponent.class);

        spawn("platform", new SpawnData(0, getHeight()).put("width", 3000).put("height", 40));

        spawn("platform", new SpawnData(300, getHeight() - 100).put("width", 120).put("height", 40));

        spawn("platform", new SpawnData(450, getHeight() - 250).put("width", 520).put("height", 40));
        spawn("platform", new SpawnData(950, getHeight() - 100).put("width", 120).put("height", 40));

        getGameScene().getViewport().bindToEntity(r, getWidth() / 2, 300);
        getGameScene().getViewport().setBounds(0, 0, 10000, getHeight() + 40);

        coin = spawn("coin", 1000, 450);

        //spawn("robot", new SpawnData(600, 0).put("color", Color.RED));
    }

    private Entity coin;
    private Line line = new Line();
    private Glow glow = new Glow(0.5);
    private Entity lastEntity;

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 1400);

        getGameScene().addGameView(new EntityView(line), RenderLayer.BOTTOM);
        line.setStartX(100);
        line.setStartY(465);
        line.setStroke(Color.RED);
        line.setStrokeWidth(2);
    }

    @Override
    protected void onUpdate(double tpf) {
        RaycastResult result = getPhysicsWorld().raycast(new Point2D(100, 465), new Point2D(1200, 465));

        result.getPoint().ifPresent(p -> {
            line.setEndX(p.getX());
            line.setEndY(p.getY());
        });

        result.getEntity().ifPresent(c -> {
            if (lastEntity != null) {
                lastEntity.getView().setEffect(null);
            }

            lastEntity = c;
            c.getView().setEffect(glow);
        });
    }

    //    @Override
//    protected void initUI() {
//        Texture t = DSLKt.texture("robot_stand.png").subTexture(new Rectangle2D(0, 0, 275, 275));
//
//        getGameScene().addUINode(t);
//
//
//        getMasterTimer().runOnceAfter(() -> {
//
//            for (int i = 1; i <= 50; i++) {
//                Texture t0 = t.copy();
//                t0.setScaleX(1.0 / i);
//                t0.setScaleY(1.0 / i);
//                t0.translateXProperty().bind(robot.getEntity().xProperty());
//                t0.translateYProperty().bind(robot.getEntity().yProperty());
//                t0.setOpacity(0.25);
//
//                EntityView view = new EntityView(t0);
//
//                getGameScene().addGameView(view, RenderLayer.BOTTOM);
//
//                getMasterTimer().runOnceAfter(() -> {
//                    getGameScene().removeGameView(view, RenderLayer.BOTTOM);
//                }, Duration.millis(300 * i));
//            }
//
//
//            Entities.animationBuilder()
//                    .duration(Duration.seconds(0.5))
//                    .scale(robot.getEntity())
//                    .from(new Point2D(1, 1))
//                    .to(new Point2D(0.2, 0.2))
//                    .buildAndPlay();
//        }, Duration.seconds(2));
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
