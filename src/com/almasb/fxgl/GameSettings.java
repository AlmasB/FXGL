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
package com.almasb.fxgl;

/**
 * Data structure for variables that are
 * initialised before the application (game) starts.
 *
 * Modifying any data after the start of the game has no effect.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class GameSettings {

    private String title = "Untitled FXGL Game Application";
    private String version = "0.0";
    private int width = 800;
    private int height = 600;
    private boolean fullScreen = false;
    private boolean introEnabled = true;
    private boolean menuEnabled = true;
    private String iconFileName = "";

    /**
     * Constructs game settings with default parameters
     */
    public GameSettings() {}

    /**
     * Constructs new game settings with parameters
     * copied from given.
     *
     * @param copy
     */
    public GameSettings(GameSettings copy) {
        this.title = copy.title;
        this.version = copy.version;
        this.width = copy.width;
        this.height = copy.height;
        this.fullScreen = copy.fullScreen;
        this.introEnabled = copy.introEnabled;
        this.menuEnabled = copy.menuEnabled;
        this.iconFileName = copy.iconFileName;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Set title of the game. This will be shown as the
     * window header if the game isn't fullscreen.
     *
     * @param title
     * @defaultValue "Untitled FXGL Game Application"
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public int getWidth() {
        return width;
    }

    /**
     * Set target width. If the screen width is smaller,
     * the game will automatically scale down the image
     * while maintaining the aspect ratio.
     *
     * All the game logic must use target width and height.
     *
     * @param width
     * @defaultValue 800
     */
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Set target height. If the height width is smaller,
     * the game will automatically scale down the image
     * while maintaining the aspect ratio.
     *
     * All the game logic must use target width and height.
     *
     * @param height
     * @defaultValue 600
     */
    public void setHeight(int height) {
        this.height = height;
    }

    public String getVersion() {
        return version;
    }

    /**
     * Set version of the game.
     *
     * @param version
     * @defaultValue 0.0
     */
    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isIntroEnabled() {
        return introEnabled;
    }

    /**
     * If set to true, the intro video/animation will
     * be played before the start of the game
     *
     * @param b
     * @defaultValue true
     */
    public void setIntroEnabled(boolean b) {
        introEnabled = b;
    }

    public boolean isMenuEnabled() {
        return menuEnabled;
    }

    /**
     * Setting to true enables main and game menu.
     *
     * @param b
     * @defaultValue true
     */
    public void setMenuEnabled(boolean b) {
        menuEnabled = b;
    }

    public String getIconFileName() {
        return iconFileName;
    }

    /**
     * Set file name of the icon to be used
     * as the application icon.
     *
     * The file must be placed under /assets/ui/icons/ .
     * The name must be given in the form relative to that
     * path. E.g. "icon.png"
     *
     * @param iconFileName
     */
    public void setIconFileName(String iconFileName) {
        this.iconFileName = iconFileName;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    /**
     * Setting to true will start the game in fullscreen mode.
     *
     * @param b
     * @defaultValue false
     */
    public void setFullScreen(boolean b) {
        fullScreen = b;
    }
}
