/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.quest.QuestService;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use QuestService.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class QuestSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.addEngineService(QuestService.class);
    }

    @Override
    protected void initInput() {
        onBtnDownPrimary(() -> inc("clicks", +1));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("clicks", 0);
    }

    @Override
    protected void initGame() {
        var quest = getQuestService().newQuest("First Quest");

        // returns a ref to objective for refined control of each objective
        var objective = quest.addIntObjective("Click 5 times", "clicks", 5);

        quest.stateProperty().subscribe((old, newState) -> {
            System.out.println("Quest state: " + old + " -> " + newState);
        });

        getQuestService().startQuest(quest);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
