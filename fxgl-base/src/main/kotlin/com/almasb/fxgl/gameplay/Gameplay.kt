/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.gameplay.achievement.AchievementManager
import com.almasb.fxgl.gameplay.cutscene.CutsceneManager
import com.almasb.fxgl.gameplay.qte.QTE
import com.almasb.fxgl.gameplay.qte.QTEProvider
import com.almasb.fxgl.gameplay.rpg.InGameClock
import com.almasb.fxgl.gameplay.rpg.quest.QuestManager
import com.almasb.fxgl.gameplay.rpg.quest.QuestManagerProvider
import com.almasb.fxgl.saving.UserProfile
import com.almasb.fxgl.saving.UserProfileSavable

/**
 * Contains access to various gameplay related managers / services
 * and data.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Gameplay : UserProfileSavable {

    val stats = GameplayStats()

    val clock = InGameClock(FXGL.getProperties().getInt("gameplay.clock.secondsIn24h"))

    val QTE: QTE by lazy { QTEProvider() }

    val questManager: QuestManager by lazy { QuestManagerProvider() }

    val achievementManager: AchievementManager by lazy { AchievementManager() }

    val cutsceneManager: CutsceneManager by lazy { CutsceneManager() }

    val leaderboard: Leaderboard by lazy { Leaderboard() }

    override fun save(profile: UserProfile) {
        stats.save(profile)
        achievementManager.save(profile)
        questManager.save(profile)
    }

    override fun load(profile: UserProfile) {
        stats.load(profile)
        achievementManager.load(profile)
        questManager.load(profile)
    }
}