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
package com.almasb.fxgl.ui;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.asset.SaveLoadManager;
import com.almasb.fxgl.event.MenuEvent;
import com.almasb.fxgl.settings.SceneSettings;

/**
 * This is the default FXGL menu used if the users
 * don't provide their own
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class FXGLMainMenu extends FXGLMenu {

    public FXGLMainMenu(GameApplication app, SceneSettings settings) {
        super(app, settings);
    }

    @Override
    protected MenuBox createMenuBody() {
        MenuItem itemContinue = new MenuItem("CONTINUE");
        itemContinue.setEnabled(SaveLoadManager.INSTANCE.loadLastModifiedFile().isPresent());
        itemContinue.setOnAction(e -> {
            itemContinue.fireEvent(new MenuEvent(MenuEvent.LOAD));
        });

        MenuItem itemNewGame = new MenuItem("NEW GAME");
        itemNewGame.setOnAction(e -> {
            itemNewGame.fireEvent(new MenuEvent(e.getSource(), e.getTarget(), MenuEvent.NEW_GAME));
        });

        MenuItem itemLoad = new MenuItem("LOAD");
        itemLoad.setMenuContent(createContentLoad());

        MenuItem itemOptions = new MenuItem("OPTIONS");
        itemOptions.setChild(createOptionsMenu());

        MenuItem itemExtra = new MenuItem("EXTRA");
        itemExtra.setChild(createExtraMenu());

        MenuItem itemExit = new MenuItem("EXIT");
        itemExit.setOnAction(e -> {
            UIFactory.getDialogBox().showConfirmationBox("Exit the game?", yes -> {
                if (yes)
                    itemExit.fireEvent(new MenuEvent(e.getSource(), e.getTarget(), MenuEvent.EXIT));
            });
        });

        MenuBox menu = new MenuBox(200, itemContinue, itemNewGame, itemLoad, itemOptions, itemExtra, itemExit);
        menu.setTranslateX(50);
        menu.setTranslateY(app.getHeight() / 2 - menu.getLayoutHeight() / 2);
        return menu;
    }
}
