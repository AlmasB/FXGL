/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.asset

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGL.Companion.getString
import com.almasb.fxgl.audio.Sound
import com.almasb.fxgl.scene.CSS
import com.almasb.fxgl.ui.FontFactory
import javafx.scene.image.Image

/**
 * Stores internal assets, i.e. provided by FXGL.
 * These can be overridden by "system.properties" file under "/assets/properties/".
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLAssets {

    companion object {
        @JvmField val SOUND_NOTIFICATION: Sound
        @JvmField val SOUND_MENU_SELECT: Sound
        @JvmField val SOUND_MENU_BACK: Sound
        @JvmField val SOUND_MENU_PRESS: Sound

        @JvmField val UI_FONT: FontFactory
        @JvmField val UI_MONO_FONT: FontFactory

        @JvmField val UI_CSS: CSS
        @JvmField val UI_ICON_NAME: String
        @JvmField val UI_ICON: Image

        init {
            val loader = FXGL.getAssetLoader()

            SOUND_NOTIFICATION = loader.loadSound(getString("sound.notification"))
            SOUND_MENU_SELECT = loader.loadSound(getString("sound.menu.select"))
            SOUND_MENU_BACK = loader.loadSound(getString("sound.menu.back"))
            SOUND_MENU_PRESS = loader.loadSound(getString("sound.menu.press"))

            UI_FONT = loader.loadFont(getString("ui.font"))
            UI_MONO_FONT = loader.loadFont(getString("ui.mono.font"))
            UI_CSS = loadCSS()
            UI_ICON_NAME = getString("ui.icon.name")
            UI_ICON = loader.loadAppIcon(UI_ICON_NAME)
        }

        private fun loadCSS(): CSS {
            val cssExternalForm = getString("ui.css")

            return FXGL.getAssetLoader().loadCSS(

                    // if default css, then use menu style css
                    if (cssExternalForm.endsWith("fxgl.css"))
                        FXGL.getSettings().menuStyle.cssFileName
                    else
                        cssExternalForm
            )
        }
    }
}