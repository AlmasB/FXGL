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
package com.almasb.fxgl.scene.menu;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.scene.FXGLMenu;
import com.almasb.fxgl.scene.MenuFactory;

/**
 * FXGL built-in menu styles. NOT COMPLETED YET.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public enum MenuStyle {
    FXGL_DEFAULT(new MenuFactory() {
        @Override
        public FXGLMenu newMainMenu(GameApplication app) {
            return new FXGLMainMenu(app);
        }

        @Override
        public FXGLMenu newGameMenu(GameApplication app) {
            return new FXGLGameMenu(app);
        }
    }, "fxgl_dark.css"),

    GTA5(new MenuFactory() {
        @Override
        public FXGLMenu newMainMenu(GameApplication app) {
            return new GTAVMainMenu(app);
        }

        @Override
        public FXGLMenu newGameMenu(GameApplication app) {
            return new GTAVGameMenu(app);
        }
    }, "fxgl_gta5.css"),

    CCTR(new MenuFactory() {
        @Override
        public FXGLMenu newMainMenu(GameApplication app) {
            return new CCTRMainMenu(app);
        }

        @Override
        public FXGLMenu newGameMenu(GameApplication app) {
            return new CCTRGameMenu(app);
        }
    }, "fxgl_cctr.css");

    private MenuFactory factory;
    private String css;

    public String getCSSFileName() {
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
