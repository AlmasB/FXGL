/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s06gameplay;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.gameplay.rpg.quest.Quest;
import com.almasb.fxgl.gameplay.rpg.quest.QuestObjective;
import com.almasb.fxgl.gameplay.rpg.quest.QuestPane;
import com.almasb.fxgl.settings.GameSettings;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Arrays;

/**
 * Shows how to add a quest and its view.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class QuestSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("QuestSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGame() {
        getGameplay().getQuestManager().addQuest(new Quest("TestQuest", Arrays.asList(
                new QuestObjective("TestObjective", new SimpleIntegerProperty(0), 3)
        )));
    }

    @Override
    protected void initUI() {
        getGameScene().addUINode(new QuestPane(300, 300));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
