/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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
package s31pacman;

import com.almasb.astar.AStarGrid;
import com.almasb.astar.NodeState;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.TypeComponent;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.TextLevelParser;
import com.almasb.fxgl.settings.GameSettings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import s31pacman.collision.PlayerCoinHandler;
import s31pacman.collision.PlayerEnemyHandler;
import s31pacman.control.PlayerControl;

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
        settings.setShowFPS(true);
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

                if (teleports.get() > 0) {
                    teleports.set(teleports.get() - 1);
                    playerControl.teleport();
                }
            }
        }, KeyCode.F);
    }

    @Override
    protected void initAssets() {}

    private IntegerProperty score;
    private IntegerProperty teleports;

    @Override
    protected void initGame() {

        TextLevelParser parser = new TextLevelParser();
        parser.addEntityProducer('0', EntityFactory::newCoin);
        parser.addEntityProducer('1', EntityFactory::newBlock);

        parser.addEntityProducer('E', EntityFactory::newEnemy);
        parser.addEntityProducer('P', EntityFactory::newPlayer);

        Level level = parser.parse("pacman_level0.txt");

        player = (GameEntity) level.getEntities()
                .stream()
                .filter(p -> p.getComponentUnsafe(TypeComponent.class).isType(EntityType.PLAYER))
                .findAny()
                .get();

        playerControl = player.getControlUnsafe(PlayerControl.class);

        long numCoins = level.getEntities()
                .stream()
                .filter(e -> Entities.getType(e).isType(EntityType.COIN))
                .count();

        coins = new SimpleIntegerProperty();
        coins.setValue(numCoins);

        grid = new AStarGrid(MAP_SIZE, MAP_SIZE);
        level.getEntities()
                .stream()
                .filter(e -> Entities.getType(e).isType(EntityType.BLOCK))
                .map(e -> Entities.getPosition(e).getValue())
                .forEach(point -> {
                    int x = (int) point.getX() / BLOCK_SIZE;
                    int y = (int) point.getY() / BLOCK_SIZE;

                    grid.setNodeState(x, y, NodeState.NOT_WALKABLE);
                });

        score = new SimpleIntegerProperty();
        teleports = new SimpleIntegerProperty();



        getGameWorld().setLevel(level);
        level.getEntities().clear();
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new PlayerCoinHandler());
        getPhysicsWorld().addCollisionHandler(new PlayerEnemyHandler());
    }

    private PacmanUIController uiController;

    @Override
    protected void initUI() {
        uiController = new PacmanUIController();
        getMasterTimer().addUpdateListener(uiController);

        Parent fxmlUI = getAssetLoader().loadFXML("pacman_ui.fxml", uiController);
        fxmlUI.setTranslateX(MAP_SIZE * BLOCK_SIZE);

        uiController.getLabelScore().textProperty().bind(score.asString("Score:\n[%d]"));
        uiController.getLabelTeleport().textProperty().bind(teleports.asString("Teleports:\n[%d]"));

        getGameScene().addUINode(fxmlUI);

        //System.out.println((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576.0);
    }

    @Override
    protected void onUpdate(double tpf) {
    }

    @Override
    protected void onPostUpdate(double tpf) {
        if (requestNewGame) {
            requestNewGame = false;
            getMasterTimer().removeUpdateListener(uiController);
            startNewGame();
        }
    }

    private IntegerProperty coins;

    public void onCoinPickup() {
        coins.set(coins.get() - 1);
        score.set(score.get() + 50);

        if (score.get() % 2000 == 0) {
            teleports.set(teleports.get() + 1);
        }

        if (coins.get() == 0) {
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
