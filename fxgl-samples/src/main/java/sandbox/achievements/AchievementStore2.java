/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.achievements;

import com.almasb.fxgl.achievement.Achievement;
import com.almasb.fxgl.achievement.AchievementManager;
import com.almasb.fxgl.achievement.AchievementStore;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AchievementStore2 implements AchievementStore {
    @Override
    public void initAchievements(AchievementManager manager) {
        manager.registerAchievement(new Achievement("Killer", "Kill 3 enemies", "enemiesKilled", 3));

    }
}
