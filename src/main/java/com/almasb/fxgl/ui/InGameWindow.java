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

package com.almasb.fxgl.ui;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import jfxtras.scene.control.window.CloseIcon;
import jfxtras.scene.control.window.MinimizeIcon;
import jfxtras.scene.control.window.Window;

/**
 * Represents an in-game window as part of game UI.
 * This is only intended to be used as a pane for UI controls,
 * i.e. non-game scene view elements.
 * <p>
 *     Can be attached to game scene as follows:
 *     <pre>
 *         InGameWindow window = new InGameWindow("Title");
 *         getGameScene().addUINode(window);
 *     </pre>
 * </p>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class InGameWindow extends Window {

    /**
     * Constructs an in-game window with minimize and close icons.
     *
     * @param title window title
     */
    public InGameWindow(String title) {
        this(title, WindowDecor.ALL);
    }

    /**
     * Constructs an in-game window with given window decor.
     *
     * @param title window title
     * @param decor window decor
     */
    public InGameWindow(String title, WindowDecor decor) {
        super(title);
        switch (decor) {
            case NONE:
                break;
            case MINIMIZE:
                getRightIcons().addAll(new MinimizeIcon(this));
                break;
            case CLOSE:
                getRightIcons().addAll(new CloseIcon(this));
                break;
            case ALL:
                getRightIcons().addAll(new MinimizeIcon(this), new CloseIcon(this));
                break;
        }
    }

    /**
     * Set background color of this window.
     *
     * @param color background color
     */
    public final void setBackgroundColor(Paint color) {
        setBackground(new Background(new BackgroundFill(color, null, null)));
    }

    /**
     * Set top-left position of the window.
     *
     * @param x left x
     * @param y top y
     */
    public final void setPosition(double x, double y) {
        setTranslateX(x);
        setTranslateY(y);
    }

    public enum WindowDecor {
        NONE,
        MINIMIZE,
        CLOSE,
        ALL
    }
}
