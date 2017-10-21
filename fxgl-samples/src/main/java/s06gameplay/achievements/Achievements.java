/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s06gameplay.achievements;

import com.almasb.fxgl.gameplay.Achievement;
import com.almasb.fxgl.gameplay.AchievementManager;
import com.almasb.fxgl.gameplay.AchievementStore;
import com.almasb.fxgl.gameplay.SetAchievementStore;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetAchievementStore
public class Achievements implements AchievementStore {

    @Override
    public void initAchievements(AchievementManager manager) {
        manager.registerAchievement(new Achievement("Likes moving", "Move 500 pixels", "moved", 400));
        manager.registerAchievement(new Achievement("World Traveller", "Get to the other side of the screen.", "playerX", 600));
    }
}
