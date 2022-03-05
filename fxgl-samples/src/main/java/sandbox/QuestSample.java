/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.quest.Quest;
import com.almasb.fxgl.quest.QuestService;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class QuestSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.addEngineService(QuestService.class);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            var quest = new Quest("Your first quest");

            quest.addIntObjective("Click 5 times", "clicks", 5);

            quest.stateProperty().addListener((o, old, newState) -> {
                System.out.println("Quest state: " + old + " -> " + newState);
            });

            getQuestService().startQuest(quest);
        });

        onBtnDown(MouseButton.PRIMARY, () -> {
            inc("clicks", +1);
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("clicks", 0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
