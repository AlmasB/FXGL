/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.settings;

import com.almasb.fxgl.util.ApplicationMode;

/**
 * A copy of GameSettings with public getters only.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ReadOnlyGameSettings {

    protected String title = "Untitled FXGL Game Application";
    protected String version = "0.0";
    protected int width = 800;
    protected int height = 600;
    protected boolean fullScreen = false;
    protected boolean introEnabled = true;
    protected boolean menuEnabled = true;
    protected String iconFileName = "fxgl_icon.png";
    protected boolean showFPS = true;
    protected ApplicationMode appMode = ApplicationMode.DEVELOPER;
    protected String defaultFontName = "Copperplate_Gothic_Light_Regular.ttf";

    /**
     * Constructs game settings with default parameters
     */
    ReadOnlyGameSettings() {
    }

    /**
     * Constructs new game settings with parameters
     * copied from given.
     *
     * @param copy game settings to copy from
     */
    ReadOnlyGameSettings(ReadOnlyGameSettings copy) {
        this.title = copy.title;
        this.version = copy.version;
        this.width = copy.width;
        this.height = copy.height;
        this.fullScreen = copy.fullScreen;
        this.introEnabled = copy.introEnabled;
        this.menuEnabled = copy.menuEnabled;
        this.iconFileName = copy.iconFileName;
        this.showFPS = copy.showFPS;
        this.appMode = copy.appMode;
        this.defaultFontName = copy.defaultFontName;
    }

    public final String getTitle() {
        return title;
    }

    public final String getVersion() {
        return version;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    public final boolean isFullScreen() {
        return fullScreen;
    }

    public final boolean isIntroEnabled() {
        return introEnabled;
    }

    public final boolean isMenuEnabled() {
        return menuEnabled;
    }

    public final String getIconFileName() {
        return iconFileName;
    }

    public final boolean isFPSShown() {
        return showFPS;
    }

    public final ApplicationMode getApplicationMode() {
        return appMode;
    }

    public final String getDefaultFontName() {
        return defaultFontName;
    }
}
