/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames.lockpicking

import com.almasb.fxgl.minigames.MiniGame
import com.almasb.fxgl.minigames.MiniGameResult
import com.almasb.fxgl.minigames.MiniGameView

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class LockPickResult : MiniGameResult {
    override val isSuccess: Boolean
        get() = false
}

class LockPickMiniGame : MiniGame<LockPickResult>() {

    override fun onUpdate(tpf: Double) {
    }
}

class LockPickView : MiniGameView<LockPickMiniGame>(LockPickMiniGame()) {

}