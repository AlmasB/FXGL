/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.achievements;

import com.almasb.fxgl.achievement.Achievement;
import com.almasb.fxgl.achievement.AchievementStore;

import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AchievementStore2 implements AchievementStore {

    @Override
    public void initAchievements(List<Achievement> achievements) {
        achievements.add(new Achievement("Killer", "Kill 3 enemies", "enemiesKilled", 3));
    }
}
