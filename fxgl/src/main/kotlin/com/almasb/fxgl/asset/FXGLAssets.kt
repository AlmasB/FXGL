/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
            UI_CSS = loader.loadCSS(getString("ui.css"))
            UI_ICON_NAME = getString("ui.icon.name")
            UI_ICON = loader.loadAppIcon(UI_ICON_NAME)
        }
    }
}