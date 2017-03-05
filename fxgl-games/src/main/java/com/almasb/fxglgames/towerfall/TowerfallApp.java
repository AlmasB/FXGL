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

package com.almasb.fxglgames.towerfall;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.gameplay.rpg.quest.Quest;
import com.almasb.fxgl.gameplay.rpg.quest.QuestObjective;
import com.almasb.fxgl.gameplay.rpg.quest.QuestPane;
import com.almasb.fxgl.gameplay.rpg.quest.QuestWindow;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.text.TextLevelParser;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TowerfallApp extends GameApplication {

    private GameEntity player;
    private CharacterControl playerControl;

    public GameEntity getPlayer() {
        return player;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Towerfall");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                playerControl.jump();
                getGameState().increment("jumps", +1);
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Break") {
            @Override
            protected void onActionBegin() {
                playerControl.stop();
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                playerControl.shoot(input.getMousePositionWorld());
                getGameState().increment("shotArrows", +1);
            }
        }, KeyCode.F);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("shotArrows", 0);
        vars.put("jumps", 0);
        vars.put("enemiesKilled", 0);
    }

    @Override
    protected void initGame() {
        TextLevelParser parser = new TextLevelParser(getGameWorld().getEntityFactory());
        Level level = parser.parse("towerfall/levels/level1.txt");

        getGameWorld().setLevel(level);

        player = (GameEntity) getGameWorld().getEntitiesByType(EntityType.PLAYER).get(0);
        playerControl = player.getControlUnsafe(CharacterControl.class);
    }

    @Override
    protected void initUI() {
        QuestPane questPane = new QuestPane(350, 450);
        QuestWindow window = new QuestWindow(questPane);

        //getGameScene().addUINode(window);

        List<Quest> quests = Arrays.asList(
                new Quest("Test Quest", Arrays.asList(
                        new QuestObjective("Shoot Arrows", getGameState().intProperty("shotArrows"), 15),
                        new QuestObjective("Jump", getGameState().intProperty("jumps"))
                )),

                new Quest("Test Quest 2", Arrays.asList(
                        new QuestObjective("Shoot Arrows", getGameState().intProperty("shotArrows"), 25, Duration.seconds(3))
                )),

                new Quest("Test Quest 2", Arrays.asList(
                        new QuestObjective("Kill an enemy", getGameState().intProperty("enemiesKilled"))
                )),

                new Quest("Test Quest 2", Arrays.asList(
                        new QuestObjective("Shoot Arrows", getGameState().intProperty("shotArrows"), 25)
                )),

                new Quest("Test Quest 2", Arrays.asList(
                        new QuestObjective("Shoot Arrows", getGameState().intProperty("shotArrows"), 25)
                ))
        );

        quests.forEach(getQuestService()::addQuest);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
