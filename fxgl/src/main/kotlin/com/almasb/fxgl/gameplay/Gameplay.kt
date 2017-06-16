/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.gameplay.qte.QTE
import com.almasb.fxgl.gameplay.qte.QTEProvider
import com.almasb.fxgl.gameplay.rpg.InGameClock
import com.almasb.fxgl.gameplay.rpg.quest.QuestManager
import com.almasb.fxgl.gameplay.rpg.quest.QuestManagerProvider
import com.google.inject.Inject

/**
 * Contains access to various gameplay related managers / services
 * and data.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Gameplay
@Inject
private constructor() {

    val stats = GameplayStats()

    val clock = FXGL.getInstance(InGameClock::class.java)

    val QTE: QTE by lazy { QTEProvider() }

    val questManager: QuestManager by lazy { QuestManagerProvider() }

    val achievementManager: AchievementManager by lazy { AchievementManager() }
}