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
package com.almasb.fxglgames.pacman;

import com.almasb.fxgl.ai.pathfinding.AStarGrid;
import com.almasb.fxgl.ai.pathfinding.NodeState;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.text.TextLevelParser;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.UI;
import com.almasb.fxglgames.pacman.control.PlayerControl;
import javafx.scene.input.KeyCode;

import java.util.Map;

/**
 * This is a basic demo of Pacman.
 *
 * Assets taken from opengameart.org
 * (Carlos Alface 2014 kalface@gmail.com, http://c-toy.blogspot.pt/).
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PacmanApp extends GameApplication {

    public static final int BLOCK_SIZE = 40;

    public static final int MAP_SIZE = 21;

    private static final int UI_SIZE = 200;

    private GameEntity player;
    private PlayerControl playerControl;

    public GameEntity getPlayer() {
        return player;
    }

    public PlayerControl getPlayerControl() {
        return playerControl;
    }

    private AStarGrid grid;

    public AStarGrid getGrid() {
        return grid;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(MAP_SIZE * BLOCK_SIZE + UI_SIZE);
        settings.setHeight(MAP_SIZE * BLOCK_SIZE);
        settings.setTitle("Reverse Pac-man");
        settings.setVersion("0.3");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setProfilingEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Up") {
            @Override
            protected void onAction() {
                playerControl.up();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Down") {
            @Override
            protected void onAction() {
                playerControl.down();
            }
        }, KeyCode.S);

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

        getInput().addAction(new UserAction("Teleport") {
            @Override
            protected void onActionBegin() {

                if (getGameState().getInt("teleport") > 0) {
                    getGameState().increment("teleport", -1);
                    playerControl.teleport();
                }
            }
        }, KeyCode.F);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("coins", 0);
        vars.put("teleport", 0);
    }

    @Override
    protected void initGame() {
        TextLevelParser parser = new TextLevelParser(getGameWorld().getEntityFactory());

        // parse level from text and set to game world
        Level level = parser.parse("pacman/levels/pacman_level0.txt");
        getGameWorld().setLevel(level);
        level.getEntities().clear();

        // get references to player and his control
        player = (GameEntity) getGameWorld().getEntitiesByType(PacmanType.PLAYER).get(0);
        playerControl = player.getControlUnsafe(PlayerControl.class);

        // init the A* underlying grid and mark nodes where blocks are as not walkable
        grid = new AStarGrid(MAP_SIZE, MAP_SIZE);
        getGameWorld().getEntitiesByType(PacmanType.BLOCK)
                .stream()
                .map(e -> Entities.getPosition(e).getValue())
                .forEach(point -> {
                    int x = (int) point.getX() / BLOCK_SIZE;
                    int y = (int) point.getY() / BLOCK_SIZE;

                    grid.setNodeState(x, y, NodeState.NOT_WALKABLE);
                });

        // find out number of coins
        getGameState().setValue("coins", getGameWorld().getEntitiesByType(PacmanType.COIN).size());
    }

    private PacmanUIController uiController;

    @Override
    protected void initUI() {
        uiController = new PacmanUIController();
        getMasterTimer().addUpdateListener(uiController);

        UI ui = getAssetLoader().loadUI("pacman/pacman_ui.fxml", uiController);
        ui.getRoot().setTranslateX(MAP_SIZE * BLOCK_SIZE);

        uiController.getLabelScore().textProperty().bind(getGameState().intProperty("score").asString("Score:\n[%d]"));
        uiController.getLabelTeleport().textProperty().bind(getGameState().intProperty("teleport").asString("Teleports:\n[%d]"));

        getGameScene().addUI(ui);
    }

    @Override
    protected void onPostUpdate(double tpf) {
        if (requestNewGame) {
            requestNewGame = false;
            getMasterTimer().removeUpdateListener(uiController);
            startNewGame();
        }
    }

    public void onCoinPickup() {
        getGameState().increment("coins", -1);
        getGameState().increment("score", +50);

        if (getGameState().getInt("score") % 2000 == 0) {
            getGameState().increment("teleport", +1);
        }

        if (getGameState().getInt("coins") == 0) {
            gameOver();
        }
    }

    private boolean requestNewGame = false;

    public void onPlayerKilled() {
        requestNewGame = true;
    }

    private void gameOver() {
        getDisplay().showConfirmationBox("Demo Over. Press Something", yes -> {
            exit();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
