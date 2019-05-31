/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.scene.SubScene

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class PauseMenu : SubScene() {

    private var canSwitchGameMenu = true

    init {
        input.addAction(object : UserAction("Resume") {
            override fun onActionBegin() {
                requestHide()
            }

            override fun onActionEnd() {
                unlockSwitch()
            }
        }, FXGL.getSettings().menuKey)
    }

    internal fun requestShow(onShow: () -> Unit) {
        if (canSwitchGameMenu) {
            canSwitchGameMenu = false
            onShow()
        }
    }

    protected fun requestHide() {
        if (canSwitchGameMenu) {
            canSwitchGameMenu = false
            onHide()
            unlockSwitch()
        }
    }

    internal fun unlockSwitch() {
        canSwitchGameMenu = true
    }

    protected open fun onHide() {
        FXGL.getGameController().popSubScene()
    }
}