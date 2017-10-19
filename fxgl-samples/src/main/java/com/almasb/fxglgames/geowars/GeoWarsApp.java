/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxglgames.geowars;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.WheelMenu;
import com.almasb.fxglgames.geowars.component.GraphicsComponent;
import com.almasb.fxglgames.geowars.component.HPComponent;
import com.almasb.fxglgames.geowars.component.OldPositionComponent;
import com.almasb.fxglgames.geowars.control.GraphicsUpdateControl;
import com.almasb.fxglgames.geowars.control.PlayerControl;
import com.almasb.fxglgames.geowars.grid.Grid;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.app.DSLKt.getd;
import static com.almasb.fxgl.app.DSLKt.set;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GeoWarsApp extends GameApplication {

    private Entity player;
    private PlayerControl playerControl;

    private Grid grid;

    public Entity getPlayer() {
        return player;
    }

    public Grid getGrid() {
        return grid;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("FXGL Geometry Wars");
        settings.setVersion("0.7");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initAssets() {
        // preload explosion sprite sheet
        getAssetLoader().loadTexture("explosion.png", 80 * 48, 80);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                playerControl.up();
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerControl.down();
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Shoot") {
            @Override
            protected void onAction() {
                playerControl.shoot(input.getMousePositionWorld());
            }
        }, MouseButton.PRIMARY);

        input.addAction(new UserAction("Weapon Menu") {
            @Override
            protected void onActionBegin() {
                openWeaponMenu();
            }
        }, MouseButton.SECONDARY);

        input.addAction(new UserAction("Time") {
            @Override
            protected void onActionBegin() {
                set("timeRatio", getd("timeRatio") == 1.0 ? 0.05 : 1.0);
            }
        }, KeyCode.G);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("multiplier", 1);
        vars.put("kills", 0);
        vars.put("time", 180);
        vars.put("timeRatio", 1.0);
        vars.put("weaponType", WeaponType.NORMAL);
    }

    private Music music;

    @Override
    protected void initGame() {
        getAudioPlayer().setGlobalSoundVolume(0.2);
        getAudioPlayer().setGlobalMusicVolume(0.1);

        initBackground();
        player = (Entity) getGameWorld().spawn("Player");
        playerControl = player.getControl(PlayerControl.class);

        getMasterTimer().runAtInterval(() -> getGameWorld().spawn("Wanderer"), Duration.seconds(1));
        getMasterTimer().runAtInterval(() -> getGameWorld().spawn("Seeker"), Duration.seconds(2));
        getMasterTimer().runAtInterval(() -> getGameWorld().spawn("Crystal", getRandomPoint()), Duration.seconds(4));
        getMasterTimer().runAtInterval(() -> getGameState().increment("time", -1), Duration.seconds(1));

        music = getAssetLoader().loadMusic("bgm.mp3");

        getGameState().<Double>addListener("timeRatio", (prev, now) -> {
            if (now != 1.0) {
                now = 0.85;
            }
            music.setRate(now);
        });

        getAudioPlayer().playMusic(music);
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = getPhysicsWorld();

        CollisionHandler bulletEnemy = new CollisionHandler(GeoWarsType.BULLET, GeoWarsType.WANDERER) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                bullet.removeFromWorld();

                HPComponent hp = enemy.getComponent(HPComponent.class);
                hp.setValue(hp.getValue() - 1);

                if (hp.getValue() == 0) {
                    getGameWorld().spawn("Explosion", enemy.getCenter());
                    enemy.removeFromWorld();
                    addScoreKill();
                }
            }
        };

        physics.addCollisionHandler(bulletEnemy);
        physics.addCollisionHandler(bulletEnemy.copyFor(GeoWarsType.BULLET, GeoWarsType.SEEKER));

        CollisionHandler playerEnemy = new CollisionHandler(GeoWarsType.PLAYER, GeoWarsType.WANDERER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                a.setPosition(getRandomPoint());
                b.removeFromWorld();
                deductScoreDeath();
            }
        };

        physics.addCollisionHandler(playerEnemy);
        physics.addCollisionHandler(playerEnemy.copyFor(GeoWarsType.PLAYER, GeoWarsType.SEEKER));

        // TODO: add shockwave skill
//        CollisionHandler shockEnemy = new CollisionHandler(GeoWarsType.SHOCKWAVE, GeoWarsType.SEEKER) {
//            @Override
//            protected void onCollisionBegin(Entity a, Entity b) {
//                PositionComponent pos = Entities.getPosition(b);
//
//                pos.translate(pos.getValue()
//                        .subtract(Entities.getPosition(player).getValue())
//                        .normalize()
//                        .multiply(100));
//            }
//        };
//
//        physics.addCollisionHandler(shockEnemy);
//        physics.addCollisionHandler(shockEnemy.copyFor(GeoWarsType.SHOCKWAVE, GeoWarsType.WANDERER));
    }

    private WheelMenu weaponMenu;

    @Override
    protected void initUI() {
        Text scoreText = getUIFactory().newText("", Color.WHITE, 18);
        scoreText.setTranslateX(1100);
        scoreText.setTranslateY(70);
        scoreText.textProperty().bind(getGameState().intProperty("score").asString());

        Text timerText = getUIFactory().newText("", Color.WHITE, 18);
        timerText.layoutBoundsProperty().addListener((o, old, bounds) -> {
            timerText.setTranslateX(getWidth() / 2 - bounds.getWidth() / 2);
        });

        timerText.setTranslateX(getWidth() / 2);
        timerText.setTranslateY(60);
        timerText.textProperty().bind(getGameState().intProperty("time").asString());

        Circle timerCircle = new Circle(40, 40, 40, null);
        timerCircle.setStrokeWidth(2);
        timerCircle.setStroke(Color.AQUA);
        timerCircle.setTranslateX(getWidth() / 2 - 40);
        timerCircle.setTranslateY(60 - 40 - 5);

        WeaponType[] weaponTypes = WeaponType.values();

        weaponMenu = new WheelMenu(
                weaponTypes[0].toString(),
                weaponTypes[1].toString(),
                weaponTypes[2].toString(),
                weaponTypes[3].toString()
        );

        weaponMenu.setSelectionHandler(typeName -> {
            WeaponType type = WeaponType.valueOf(typeName);
            getGameState().setValue("weaponType", type);
            getAudioPlayer().playSound(typeName.toLowerCase() + ".wav");
        });

        getGameScene().addUINodes(scoreText, timerText, timerCircle, weaponMenu);

        weaponMenu.close();

        Text beware = getUIFactory().newText("Beware! Seekers get smarter every time!", Color.AQUA, 24);
        beware.setTranslateX(getWidth() / 2 - beware.getLayoutBounds().getWidth() / 2);
        beware.setTranslateY(getHeight() / 2);
        beware.setOpacity(0);

        getGameScene().addUINode(beware);

        FadeTransition ft = new FadeTransition(Duration.seconds(2), beware);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setOnFinished(e -> getGameScene().removeUINode(beware));
        ft.play();
    }

    @Override
    protected void onUpdate(double tpf) {
        player.getComponent(OldPositionComponent.class)
                .setValue(player.getPosition());

        grid.update();
    }

    @Override
    protected void onPostUpdate(double tpf) {
        if (getGameState().getInt("time") == 0) {
            getDisplay().showConfirmationBox("Demo Over. Press anything to exit. Your score: " + getGameState().getInt("score"), yes -> {
                exit();
            });
        }
    }

    private void initBackground() {
        EntityView backgroundView = new EntityView(getAssetLoader().loadTexture("background.png", getWidth(), getHeight()));
        getGameScene().addGameView(backgroundView);

        Canvas canvas = new Canvas(getWidth(), getHeight());
        canvas.getGraphicsContext2D().setStroke(new Color(0.118, 0.118, 0.545, 1));

        Entities.builder()
                .viewFromNode(canvas)
                .with(new GraphicsComponent(canvas.getGraphicsContext2D()))
                .with(new GraphicsUpdateControl())
                .buildAndAttach(getGameWorld());

        Rectangle size = new Rectangle(0, 0, getWidth(), getHeight());
        Point2D spacing = new Point2D(38.8, 40);

        grid = new Grid(size, spacing, getGameWorld(), canvas.getGraphicsContext2D());
    }

    private void openWeaponMenu() {
        weaponMenu.setTranslateX(getInput().getMouseXWorld() - 20);
        weaponMenu.setTranslateY(getInput().getMouseYWorld() - 70);
        weaponMenu.open();
    }

    private Point2D getRandomPoint() {
        return new Point2D(Math.random() * getWidth(), Math.random() * getHeight());
    }

    private void addScoreKill() {
        getGameState().increment("kills", 1);
        if (getGameState().getInt("kills") == 15) {
            getGameState().setValue("kills", 0);
            getGameState().increment("multiplier", +1);
        }

        final int multiplier = getGameState().getInt("multiplier");

        Text bonusText = getUIFactory().newText("+100" + (multiplier == 1 ? "" : "x" + multiplier), Color.WHITE, 18);
        bonusText.setTranslateX(1100);
        bonusText.setTranslateY(0);

        getGameScene().addUINode(bonusText);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), bonusText);
        tt.setToY(70);
        tt.setOnFinished(e -> {
            getGameState().increment("score", +100*multiplier);
            getGameScene().removeUINode(bonusText);
        });
        tt.play();
    }

    private void deductScoreDeath() {
        getGameState().increment("score", -1000);
        getGameState().setValue("kills", 0);
        getGameState().setValue("multiplier", 1);

        Text bonusText = getUIFactory().newText("-1000", Color.WHITE, 18);
        bonusText.setTranslateX(1100);
        bonusText.setTranslateY(70);

        getGameScene().addUINode(bonusText);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), bonusText);
        tt.setToY(0);
        tt.setOnFinished(e -> {
            getGameScene().removeUINode(bonusText);
        });
        tt.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
