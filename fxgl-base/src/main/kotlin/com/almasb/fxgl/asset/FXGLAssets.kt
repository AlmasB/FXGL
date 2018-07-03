/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.asset

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.audio.Sound
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.scene.CSS
import com.almasb.fxgl.ui.FontFactory

/**
 * Stores internal assets, i.e. provided by FXGL.
 * These can be overridden by "system.properties" file under "/assets/properties/".
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLAssets {

    companion object {
        private val log = Logger.get<FXGLAssets>()

        @JvmField val SOUND_NOTIFICATION: Sound
        @JvmField val SOUND_MENU_SELECT: Sound
        @JvmField val SOUND_MENU_BACK: Sound
        @JvmField val SOUND_MENU_PRESS: Sound

        @JvmField val UI_FONT: FontFactory
        @JvmField val UI_MONO_FONT: FontFactory
        @JvmField val UI_GAME_FONT: FontFactory
        @JvmField val UI_TEXT_FONT: FontFactory

        @JvmField val UI_CSS: CSS

        init {
            log.debug("Loading FXGLAssets")

            val loader = FXGL.getAssetLoader()
            val settings = FXGL.getSettings()

            SOUND_NOTIFICATION = loader.loadSound(getString("sound.notification"))
            SOUND_MENU_SELECT = loader.loadSound(getString("sound.menu.select"))
            SOUND_MENU_BACK = loader.loadSound(getString("sound.menu.back"))
            SOUND_MENU_PRESS = loader.loadSound(getString("sound.menu.press"))

            UI_FONT = loader.loadFont(settings.fontUI)
            UI_MONO_FONT = loader.loadFont(settings.fontMono)
            UI_TEXT_FONT = loader.loadFont(settings.fontText)
            UI_GAME_FONT = loader.loadFont(settings.fontGame)
            UI_CSS = loader.loadCSS(FXGL.getSettings().css)
        }

        private fun getString(name: String): String {
            return FXGL.getProperties().getString(name)
        }
    }
}