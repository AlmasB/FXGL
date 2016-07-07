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
import com.almasb.fxgl.entity.component.TypeComponent;
import com.almasb.fxgl.event.DisplayEvent;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.TextLevelParser;
import com.almasb.fxgl.settings.GameSettings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.KeyCode;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PacmanApp extends GameApplication {

    public static final int BLOCK_SIZE = 40;

    public static final int MAP_SIZE = 21;

    private PlayerControl playerControl;

    public PlayerControl getPlayerControl() {
        return playerControl;
    }

    private AStarGrid grid;

    public AStarGrid getGrid() {
        return grid;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(MAP_SIZE * BLOCK_SIZE);
        settings.setHeight(MAP_SIZE * BLOCK_SIZE);
        settings.setTitle("Pacman");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(false);
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
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {

        TextLevelParser parser = new TextLevelParser();
        parser.addEntityProducer('0', EntityFactory::newCoin);
        parser.addEntityProducer('1', EntityFactory::newBlock);

        parser.addEntityProducer('E', EntityFactory::newEnemy);
        parser.addEntityProducer('P', EntityFactory::newPlayer);

        Level level = parser.parse("pacman_level0.txt");

        playerControl = level.getEntities()
                .stream()
                .filter(p -> p.getComponentUnsafe(TypeComponent.class).isType(EntityType.PLAYER))
                .findAny()
                .get()
                .getControlUnsafe(PlayerControl.class);

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

//        for (int y = 0; y < grid.getHeight(); y++) {
//            for (int x = 0; x < grid.getWidth(); x++) {
//
//                System.out.print(grid.getNodeState(x, y) == NodeState.WALKABLE ? 0 : 1);
//            }
//
//            System.out.println();
//        }

        getGameWorld().setLevel(level);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new PlayerCoinHandler());
        getPhysicsWorld().addCollisionHandler(new PlayerEnemyHandler());
    }

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    private IntegerProperty coins;

    public void onCoinPickup() {
        coins.set(coins.get() - 1);

        if (coins.get() == 0) {
            gameOver();
        }
    }

    public void onPlayerKilled() {
        startNewGame();
    }

    private void gameOver() {
        getDisplay().showConfirmationBox("Demo Over. Press Something", yes -> {
            // workaround until we can issue requests
            getEventBus().fireEvent(new DisplayEvent(DisplayEvent.CLOSE_REQUEST));
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
