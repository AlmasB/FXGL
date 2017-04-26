/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxglgames.flappy;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FlappyBirdApp extends GameApplication {

    private PlayerControl playerControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Flappy Bird Clone");
        settings.setVersion("0.2");
        settings.setProfilingEnabled(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setFullScreen(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                playerControl.jump();
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("stageColor", Color.BLACK);
    }

    @Override
    protected void initGame() {
        initBackground();
        initPlayer();

        initBackgroundMusic();
    }

    private boolean requestNewGame = false;

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.WALL) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                requestNewGame = true;
            }
        });
    }

    @Override
    protected void initUI() {
        Text uiScore = getUIFactory().newText("", 72);
        uiScore.setTranslateX(getWidth() - 200);
        uiScore.setTranslateY(50);
        uiScore.fillProperty().bind(getGameState().objectProperty("stageColor"));
        //uiScore.textProperty().bind(getMasterTimer().tickProperty().asString());

        getGameScene().addUINode(uiScore);
    }

    @Override
    protected void onPostUpdate(double tpf) {
        if (getTick() == 3000) {
            showGameOver();
        }

        if (requestNewGame) {
            requestNewGame = false;
            startNewGame();
        }
    }

    private void initBackground() {
        GameEntity bg = Entities.builder()
                .type(EntityType.BACKGROUND)
                .viewFromNode(new Rectangle(getWidth(), getHeight(), Color.WHITE))
                .with(new ColorChangingControl())
                .buildAndAttach(getGameWorld());

        bg.getPositionComponent().xProperty().bind(getGameScene().getViewport().xProperty());
        bg.getPositionComponent().yProperty().bind(getGameScene().getViewport().yProperty());
    }

    private void initPlayer() {
        playerControl = new PlayerControl();

        Texture view = getAssetLoader().loadTexture("bird.png")
                .toAnimatedTexture(2, Duration.seconds(0.5));

        GameEntity player = Entities.builder()
                .at(100, 100)
                .type(EntityType.PLAYER)
                .bbox(new HitBox("BODY", BoundingShape.box(70, 60)))
                .viewFromNode(view)
                .with(new CollidableComponent(true))
                .with(playerControl, new WallBuildingControl())
                .buildAndAttach(getGameWorld());

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, getHeight());
        getGameScene().getViewport().bindToEntity(player, getWidth() / 3, getHeight() / 2);
    }

    private Music bgm = null;

    private void initBackgroundMusic() {
        // already initialized
        if (bgm != null)
            return;

        bgm = getAssetLoader().loadMusic("bgm.mp3");
        bgm.setCycleCount(Integer.MAX_VALUE);

        getAudioPlayer().playMusic(bgm);

//        addFXGLListener(new FXGLListener() {
//            @Override
//            public void onPause() {}
//
//            @Override
//            public void onResume() {}
//
//            @Override
//            public void onReset() {}
//
//            @Override
//            public void onExit() {
//                getAudioPlayer().stopMusic(bgm);
//                bgm.dispose();
//            }
//        });
    }

    private void showGameOver() {
        getDisplay().showMessageBox("Demo Over. Thanks for playing!", this::exit);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
