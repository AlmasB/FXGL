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
package com.almasb.fxgl.ui.menu;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.settings.SceneSettings;
import com.almasb.fxgl.ui.FXGLMenu;
import com.almasb.fxgl.ui.MenuFactory;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public enum MenuStyle {
    FXGL_DEFAULT(new MenuFactory() {
        @Override
        public FXGLMenu newMainMenu(GameApplication app, SceneSettings settings) {
            return new FXGLMainMenu(app, settings);
        }

        @Override
        public FXGLMenu newGameMenu(GameApplication app, SceneSettings settings) {
            return new FXGLGameMenu(app, settings);
        }
    }, "fxgl_dark.css"),

    GTA5(new MenuFactory() {
        @Override
        public FXGLMenu newMainMenu(GameApplication app, SceneSettings settings) {
            return new GTAVMainMenu(app, settings);
        }

        @Override
        public FXGLMenu newGameMenu(GameApplication app, SceneSettings settings) {
            return new GTAVGameMenu(app, settings);
        }
    }, "fxgl_gta5.css"),

    CCTR(new MenuFactory() {
        @Override
        public FXGLMenu newMainMenu(GameApplication app, SceneSettings settings) {
            return new CCTRMainMenu(app, settings);
        }

        @Override
        public FXGLMenu newGameMenu(GameApplication app, SceneSettings settings) {
            return new CCTRGameMenu(app, settings);
        }
    }, "fxgl_cctr.css");

    private MenuFactory factory;
    private String css;

    public String getCSS() {
        return css;
    }

    public MenuFactory getFactory() {
        return factory;
    }

    MenuStyle(MenuFactory factory, String css) {
        this.factory = factory;
        this.css = css;
    }
}
