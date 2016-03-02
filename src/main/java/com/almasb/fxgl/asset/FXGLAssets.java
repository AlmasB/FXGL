/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.asset;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.scene.CSS;
import com.almasb.fxgl.ui.FontFactory;
import javafx.scene.image.Image;

/**
 * Stores internal assets, i.e. provided by FXGL.
 * These can be overridden by "system.properties" file under "assets/properties".
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class FXGLAssets {

    public static final Sound SOUND_NOTIFICATION;
    public static final Sound SOUND_MENU_SELECT;
    public static final Sound SOUND_MENU_BACK;
    public static final Sound SOUND_MENU_PRESS;

    public static final FontFactory UI_FONT;

    public static final CSS UI_CSS;
    public static final String UI_ICON_NAME;
    public static final Image UI_ICON;

    private static String getName(String assetKey) {
        return FXGL.getProperty(assetKey);
    }

    static {
        AssetLoader loader = GameApplication.getService(ServiceType.ASSET_LOADER);

        SOUND_NOTIFICATION = loader.loadSound(getName("sound.notification"));
        SOUND_MENU_SELECT = loader.loadSound(getName("sound.menu.select"));
        SOUND_MENU_BACK = loader.loadSound(getName("sound.menu.back"));
        SOUND_MENU_PRESS = loader.loadSound(getName("sound.menu.press"));

        UI_FONT = loader.loadFont(getName("ui.font"));
        UI_CSS = loader.loadCSS(getName("ui.css"));
        UI_ICON_NAME = getName("ui.icon.name");
        UI_ICON = loader.loadAppIcon(UI_ICON_NAME);
    }
}
