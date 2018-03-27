/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.rpg.quest

import com.almasb.fxgl.saving.UserProfileSavable
import javafx.collections.ObservableList

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface QuestManager : UserProfileSavable {

    /**
     * @return unmodifiable list of currently managed quests
     */
    fun questsProperty(): ObservableList<Quest>

    /**
     * Add given quest to service.
     *
     * @param quest the quest to add
     */
    fun addQuest(quest: Quest)

    /**
     * Remove the quest from service.
     *
     * @param quest the quest to remove
     */
    fun removeQuest(quest: Quest)
}