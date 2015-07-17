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

import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public abstract class GameMenu {

    protected final GameApplication app;
    protected final Pane root = new Pane();

    private boolean canSwitch = false;

    public GameMenu(GameApplication app) {
        this.app = app;
        root.setPrefSize(app.getWidth(), app.getHeight());
        root.setFocusTraversable(true);
    }

    public final void setMenuKey(KeyCode key) {
//        root.setOnKeyPressed(event -> {
//            if (canSwitch && event.getCode() == key) {
//                canSwitch = false;
//                app.closeGameMenu();
//            }
//        });
    }

    public final void open() {
        canSwitch = false;
        Thread t = new Thread(new TimerTask());
        t.setDaemon(true);
        t.start();
    }

    public final Pane getRoot() {
        return root;
    }

    private class TimerTask extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            try {
                Thread.sleep(1000);
            }
            catch (Exception e) {
                canSwitch = true;
            }

            canSwitch = true;
            return null;
        }
    }
}
