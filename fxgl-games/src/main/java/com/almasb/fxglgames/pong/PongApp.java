/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.annotation.OnUserAction;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.TypeComponent;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.net.Server;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.MenuItem;
import com.almasb.fxgl.ui.UI;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.EnumSet;
import java.util.Map;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PongApp extends GameApplication {

    private PongFactory factory;

    private GameEntity ball;
    private GameEntity bat1;
    private GameEntity bat2;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Pong");
        settings.setVersion("0.3dev");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(true);
        settings.setEnabledMenuItems(EnumSet.of(MenuItem.ONLINE));
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void preInit() {
        FXGL.getNet().addDataParser(ServerMessage.class, message -> {
            Platform.runLater(() -> {
                if (ball != null) {

                    ball.setPosition(new Point2D(message.ballPosition.x, message.ballPosition.y));
                    bat1.setY(message.bat1PositionY);
                    bat2.setY(message.bat2PositionY);
                }
            });
        });

        FXGL.getNet().addDataParser(ClientMessage.class, message -> {
            Platform.runLater(() -> {
                if (bat2 != null) {
                    if (message.up) {
                        bat2.getControl(BatControl.class).up();
                    } else if (message.down) {
                        bat2.getControl(BatControl.class).down();
                    } else {
                        bat2.getControl(BatControl.class).stop();
                    }
                }
            });
        });

        addExitListener(() -> {
            getNet().getConnection().ifPresent(conn -> conn.close());
        });
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addInputMapping(new InputMapping("Up", KeyCode.W));
        input.addInputMapping(new InputMapping("Down", KeyCode.S));
    }

    private GameMode mode;

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("player1score", 0);
        vars.put("player2score", 0);
    }

    @Override
    protected void initGame() {
        if (getNet().getConnection().isPresent()) {
            mode = getNet().getConnection().get() instanceof Server ? GameMode.MP_HOST : GameMode.MP_CLIENT;
        } else {
            mode = GameMode.SP;
        }

        factory = new PongFactory(mode);

        initBackground();
        initScreenBounds();
        initBall();
        initPlayerBat();
        initEnemyBat();
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BALL, EntityType.WALL) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                if (boxB.getName().equals("LEFT")) {
                    getGameState().increment("player2score", +1);
                } else if (boxB.getName().equals("RIGHT")) {
                    getGameState().increment("player1score", +1);
                }
            }
        });
    }

    @Override
    protected void initUI() {
        AppController controller = new AppController();
        UI ui = getAssetLoader().loadUI("pong/main.fxml", controller);

        controller.getLabelScorePlayer().textProperty().bind(getGameState().intProperty("player1score").asString());
        controller.getLabelScoreEnemy().textProperty().bind(getGameState().intProperty("player2score").asString());

        getGameScene().addUI(ui);
    }

    @Override
    protected void onUpdate(double tpf) {

        if (mode == GameMode.MP_HOST) {
            getNet().getConnection().ifPresent(conn -> {
                conn.send(new ServerMessage(new Vec2((float)ball.getX(), (float)ball.getY()), bat1.getY(), bat2.getY()));
            });
        }
    }

    private void initBackground() {
        GameEntity bg = new GameEntity();
        bg.getViewComponent().setView(new Rectangle(getWidth(), getHeight(), Color.rgb(0, 0, 5)));

        getGameWorld().addEntity(bg);
    }

    private void initScreenBounds() {
        Entity walls = Entities.makeScreenBounds(150);
        walls.addComponent(new TypeComponent(EntityType.WALL));
        walls.addComponent(new CollidableComponent(true));

        getGameWorld().addEntity(walls);
    }

    private void initBall() {
        ball = factory.newBall(getWidth() / 2 - 5, getHeight() / 2 - 5);
        getGameWorld().addEntity(ball);
    }

    private BatControl playerBat;

    private void initPlayerBat() {
        bat1 = factory.newBat(getWidth() / 4, getHeight() / 2 - 30, true);
        getGameWorld().addEntity(bat1);

        playerBat = bat1.getControl(BatControl.class);
    }

    private void initEnemyBat() {
        bat2 = factory.newBat(3 * getWidth() / 4 - 20, getHeight() / 2 - 30, false);

        getGameWorld().addEntity(bat2);
    }

    @OnUserAction(name = "Up", type = ActionType.ON_ACTION)
    public void up() {
        if (mode == GameMode.MP_CLIENT) {
            getNet().getConnection().ifPresent(conn -> {
                conn.send(new ClientMessage(true, false, false));
            });
        } else {
            playerBat.up();
        }
    }

    @OnUserAction(name = "Down", type = ActionType.ON_ACTION)
    public void down() {
        if (mode == GameMode.MP_CLIENT) {
            getNet().getConnection().ifPresent(conn -> {
                conn.send(new ClientMessage(false, true, false));
            });
        } else {
            playerBat.down();
        }
    }

    @OnUserAction(name = "Up", type = ActionType.ON_ACTION_END)
    public void stopBat() {
        if (mode == GameMode.MP_CLIENT) {
            getNet().getConnection().ifPresent(conn -> {
                conn.send(new ClientMessage(false, false, true));
            });
        } else {
            playerBat.stop();
        }
    }

    @OnUserAction(name = "Down", type = ActionType.ON_ACTION_END)
    public void stopBat2() {
        if (mode == GameMode.MP_CLIENT) {
            getNet().getConnection().ifPresent(conn -> {
                conn.send(new ClientMessage(false, false, true));
            });
        } else {
            playerBat.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
