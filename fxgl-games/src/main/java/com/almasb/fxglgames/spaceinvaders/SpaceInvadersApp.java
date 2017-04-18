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

package com.almasb.fxglgames.spaceinvaders;

import com.almasb.fxgl.annotation.Handles;
import com.almasb.fxgl.annotation.OnUserAction;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.gameplay.Achievement;
import com.almasb.fxgl.gameplay.AchievementManager;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.io.FS;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.UI;
import com.almasb.fxglgames.spaceinvaders.control.PlayerControl;
import com.almasb.fxglgames.spaceinvaders.event.BonusPickupEvent;
import com.almasb.fxglgames.spaceinvaders.event.GameEvent;
import com.almasb.fxglgames.spaceinvaders.tutorial.Tutorial;
import com.almasb.fxglgames.spaceinvaders.tutorial.TutorialStep;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;
import java.util.stream.IntStream;

import static com.almasb.fxglgames.spaceinvaders.Config.*;

/**
 * A simple clone of Space Invaders. Demonstrates basic FXGL features.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SpaceInvadersApp extends GameApplication {

    private Logger log;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Space Invaders");
        settings.setVersion("0.8");
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initAchievements() {
        AchievementManager am = getAchievementManager();

        am.registerAchievement(new Achievement("Hitman", "Destroy " + ACHIEVEMENT_ENEMIES_KILLED + " enemies"));
        am.registerAchievement(new Achievement("Master Scorer", "Score " + ACHIEVEMENT_MASTER_SCORER + " points"));
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addInputMapping(new InputMapping("Move Left", KeyCode.A));
        input.addInputMapping(new InputMapping("Move Right", KeyCode.D));
        input.addInputMapping(new InputMapping("Shoot", MouseButton.PRIMARY));
    }

    @OnUserAction(name = "Move Left")
    public void moveLeft() {
        playerControl.left();
    }

    @OnUserAction(name = "Move Right")
    public void moveRight() {
        playerControl.right();
    }

    @OnUserAction(name = "Shoot")
    public void shoot() {
        playerControl.shoot();
    }

    private GameEntity player;
    private PlayerControl playerControl;

    private int highScore;
    private String highScoreName;

    private GameController uiController;

    @Override
    protected void preInit() {
        getAudioPlayer().setGlobalSoundVolume(0.2);
        getAudioPlayer().setGlobalMusicVolume(0.2);
    }

    private SaveData savedData = null;

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("level", 0);
        vars.put("lives", START_LIVES);
        vars.put("enemiesKilled", 0);
    }

    @Override
    protected void initGame() {
        log = FXGL.getLogger("SpaceInvaders");

        // we have to use file system directly, since we are running without menus
        FS.<SaveData>readDataTask(SAVE_DATA_NAME)
                .onSuccess(data -> savedData = data)
                .onFailure(ignore -> {})
                .execute();

        initGame(savedData == null
                ? new SaveData("CPU", ACHIEVEMENT_MASTER_SCORER)
                : savedData);
    }

    private void initGame(SaveData data) {
        highScoreName = data.getName();
        highScore = data.getHighScore();

        getAchievementManager().getAchievementByName("Hitman")
                .bind(getGameState().intProperty("enemiesKilled").greaterThanOrEqualTo(ACHIEVEMENT_ENEMIES_KILLED));
        getAchievementManager().getAchievementByName("Master Scorer")
                .bind(getGameState().intProperty("score").greaterThanOrEqualTo(ACHIEVEMENT_MASTER_SCORER));

        spawnBackground();
        spawnPlayer();

        if (!runningFirstTime)
            nextLevel();
    }

    private void spawnBackground() {
        getGameWorld().spawn("Background");

        getMasterTimer().runAtInterval(() -> {
            getGameWorld().spawn("Meteor");
        }, Duration.seconds(3));
    }

    private void spawnPlayer() {
        player = (GameEntity) getGameWorld().spawn("Player", getWidth() / 2 - 20, getHeight() - 40);
        playerControl = player.getControlUnsafe(PlayerControl.class);
    }

    private void spawnWall(double x, double y) {
        getGameWorld().spawn("Wall", x, y);
    }

    private void spawnBonus(double x, double y, BonusType type) {
        getGameWorld().spawn("Bonus", new SpawnData(x, y).put("type", type));
    }

    private void initLevel() {
        log.debug("initLevel()");

        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {
                getGameWorld().spawn("Enemy", x*60, 150 + 50 * getGameState().getInt("level") + y*60);
            }
        }

        spawnWall(40, getHeight() - 100);
        spawnWall(120, getHeight() - 100);

        spawnWall(getWidth() - 160, getHeight() - 100);
        spawnWall(getWidth() - 80, getHeight() - 100);

        getInput().setProcessInput(true);
    }

    private void cleanupLevel() {
        log.debug("cleanupLevel()");

        getGameWorld().getEntitiesByType(
                SpaceInvadersType.BONUS,
                SpaceInvadersType.WALL,
                SpaceInvadersType.BULLET)
                .forEach(Entity::removeFromWorld);
    }

    private void nextLevel() {
        log.debug("nextLevel()");

        getInput().setProcessInput(false);

        cleanupLevel();

        getGameState().setValue("enemiesKilled", 0);
        getGameState().increment("level", +1);

        if (getGameState().getInt("level") == 3) {
            showGameOver();
            return;
        }

        getGameWorld().spawn("LevelInfo");

        getMasterTimer().runOnceAfter(this::initLevel, Duration.seconds(LEVEL_START_DELAY));

        getAudioPlayer().playSound(Asset.SOUND_NEW_LEVEL);
    }

    @Override
    protected void initUI() {
        uiController = new GameController(getGameScene());

        UI ui = getAssetLoader().loadUI(Asset.FXML_MAIN_UI, uiController);

        uiController.getLabelScore().textProperty().bind(getGameState().intProperty("score").asString("Score: %d"));
        uiController.getLabelHighScore().setText("HiScore: " + highScore + " " + highScoreName + "");

        IntStream.range(0, getGameState().getInt("lives"))
                .forEach(i -> uiController.addLife());

        getGameScene().addUI(ui);
    }

    private boolean runningFirstTime = true;

    @Override
    protected void onUpdate(double tpf) {
        if (runningFirstTime) {
            getDisplay().showConfirmationBox("Play Tutorial?", yes -> {
                if (yes)
                    playTutorial();
                else
                    nextLevel();
            });

            runningFirstTime = false;
        }
    }

    private void playTutorial() {
        getInput().setRegisterInput(false);

        // TODO: ideally we must obtain dynamic key codes because the keys
        // may have been reassigned
        TutorialStep step1 = new TutorialStep("Press A to move left", Asset.DIALOG_MOVE_LEFT, () -> {
            getInput().mockKeyPress(KeyCode.A);
        });

        TutorialStep step2 = new TutorialStep("Press D to move right", Asset.DIALOG_MOVE_RIGHT, () -> {
            getInput().mockKeyRelease(KeyCode.A);
            getInput().mockKeyPress(KeyCode.D);
        });

        TutorialStep step3 = new TutorialStep("Press F to shoot", Asset.DIALOG_SHOOT, () -> {
            getInput().mockKeyRelease(KeyCode.D);

            getInput().mockButtonPress(MouseButton.PRIMARY, 0, 0);
            getInput().mockButtonRelease(MouseButton.PRIMARY);
        });

        Text tutorialText = getUIFactory().newText("", Color.AQUA, 24);
        tutorialText.textProperty().addListener((o, old, newText) -> {
            tutorialText.setTranslateX(getWidth() / 2 - tutorialText.getLayoutBounds().getWidth() / 2);
        });

        tutorialText.setTranslateY(getHeight() / 2 - 50);
        getGameScene().addUINode(tutorialText);

        Tutorial tutorial = new Tutorial(tutorialText, () -> {
            player.getPositionComponent().setValue(getWidth() / 2 - 20, getHeight() - 40);

            getGameScene().removeUINode(tutorialText);
            nextLevel();

            getInput().setRegisterInput(true);
        }, step1, step2, step3);

        tutorial.play();
    }

    @Handles(eventType = "PLAYER_GOT_HIT")
    public void onPlayerGotHit(GameEvent event) {
        getGameState().increment("lives", -1);
        uiController.loseLife();

        playerControl.enableInvincibility();

        getMasterTimer().runOnceAfter(() -> {
            playerControl.disableInvincibility();
        }, Duration.seconds(INVINCIBILITY_TIME));

        getAudioPlayer().playSound(Asset.SOUND_LOSE_LIFE);

        if (getGameState().getInt("lives") == 0)
            showGameOver();
    }

    private int scoreForKill() {
        return SCORE_ENEMY_KILL * (getGameState().getGameDifficulty().ordinal() + SCORE_DIFFICULTY_MODIFIER);
    }

    @Handles(eventType = "ENEMY_KILLED")
    public void onEnemyKilled(GameEvent event) {
        getGameState().increment("enemiesKilled", +1);
        getGameState().increment("score", scoreForKill());

        if (getGameState().getInt("enemiesKilled") == ENEMIES_PER_LEVEL)
            nextLevel();

        if (Math.random() < BONUS_SPAWN_CHANCE) {
            int bonusSize = BonusType.values().length;

            spawnBonus(Math.random() * (getWidth() - 50), Math.random() * getHeight() / 3,
                    BonusType.values()[(int)(Math.random()*bonusSize)]);
        }
    }

    @Handles(eventType = "ENEMY_REACHED_END")
    public void onEnemyReachedEnd(GameEvent event) {
        getGameState().increment("enemiesKilled", +1);

        getGameState().increment("lives", -1);
        uiController.loseLife();

        if (getGameState().getInt("lives") == 0)
            showGameOver();

        if (getGameState().getInt("enemiesKilled") == ENEMIES_PER_LEVEL)
            nextLevel();
    }

    @Handles(eventType = "ANY")
    public void onBonusPickup(BonusPickupEvent event) {
        switch (event.getType()) {
            case ATTACK_RATE:
                playerControl.increaseAttackSpeed(PLAYER_BONUS_ATTACK_SPEED);
                break;
            case LIFE:
                getGameState().increment("lives", +1);
                uiController.addLife();
                break;
        }
    }

    private void showGameOver() {
        getDisplay().showConfirmationBox("Demo Over. Play Again?", yes -> {
            if (yes) {
                startNewGame();
            } else {

                int score = getGameState().getInt("score");

                if (score > highScore) {
                    getDisplay().showInputBox("High Score! Enter your name", playerName -> {

                        // we have to use file system directly, since we are running without menus
                        FS.writeDataTask(new SaveData(playerName, score), SAVE_DATA_NAME).execute();

                        exit();
                    });
                } else {
                    exit();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
