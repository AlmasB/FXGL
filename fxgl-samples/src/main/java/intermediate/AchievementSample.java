/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.achievement.Achievement;
import com.almasb.fxgl.achievement.AchievementEvent;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import javafx.scene.input.KeyCode;

import java.util.EnumSet;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to register achievements and listen for achievement events.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AchievementSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        // 1. add achievements
        settings.getAchievements().add(new Achievement("See the world", "Move 50 pixels", "pixelsMoved", 50));
        settings.getAchievements().add(new Achievement("Killer", "Kill 3 enemies", "enemiesKilled", 3));

        settings.setMainMenuEnabled(true);
        settings.setEnabledMenuItems(EnumSet.allOf(MenuItem.class));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        // 2. make sure to add variables tracked by the achievements
        vars.put("pixelsMoved", 0);
        vars.put("enemiesKilled", 0);
    }

    @Override
    protected void initGame() {
        getEventBus().addEventHandler(AchievementEvent.ANY, e -> {
            System.out.println(e);
        });
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.A, () -> {
            inc("enemiesKilled", -1);
        });

        onKeyDown(KeyCode.D, () -> {
            inc("enemiesKilled", +1);
        });

        onKeyDown(KeyCode.Q, () -> {
            inc("pixelsMoved", -10);
        });

        onKeyDown(KeyCode.E, () -> {
            inc("pixelsMoved", +10);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
