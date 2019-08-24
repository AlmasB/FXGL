/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.achievements;

import com.almasb.fxgl.achievement.Achievement;
import com.almasb.fxgl.achievement.AchievementEvent;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.saving.DataFile;
import javafx.scene.input.KeyCode;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AchievementApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.getAchievements().add(new Achievement("See the world", "Move 600 pixels", "pixelsMoved", 600));
        settings.getAchievements().add(new Achievement("Killer", "Kill 3 enemies", "enemiesKilled", 3));

        settings.setMenuEnabled(true);
        settings.setEnabledMenuItems(EnumSet.allOf(MenuItem.class));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
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
        onKeyDown(KeyCode.Q, "save", () -> {
            var data = saveState();

            getFS().writeDataTask(data, "data.dat").run();
        });

        onKeyDown(KeyCode.E, "Load", () -> {
            getFS().<DataFile>readDataTask("data.dat")
                    .onSuccess(data -> loadState(data))
                    .run();
        });

        onKeyDown(KeyCode.A, "Dec", () -> {
            inc("enemiesKilled", -1);
        });

        onKeyDown(KeyCode.D, "Inc", () -> {
            inc("enemiesKilled", +1);
        });
    }

    @Override
    protected DataFile saveState() {
        var map = new HashMap<String, Integer>();

        map.put("pixelsMoved", geti("pixelsMoved"));
        map.put("enemiesKilled", geti("enemiesKilled"));

        return new DataFile(map);
    }

    @Override
    protected void loadState(DataFile dataFile) {
        Map<String, Integer> map = (Map<String, Integer>) dataFile.getData();

        set("pixelsMoved", map.get("pixelsMoved"));
        set("enemiesKilled", map.get("enemiesKilled"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
