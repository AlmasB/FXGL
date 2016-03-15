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

import com.almasb.fxgl.app.FXGL;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;
import jfxtras.scene.control.window.CloseIcon;
import jfxtras.scene.control.window.MinimizeIcon;
import jfxtras.scene.control.window.Window;

import java.util.concurrent.CopyOnWriteArrayList;

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

    private static final int SNAP_SIZE = 10;

    private static final CopyOnWriteArrayList<InGameWindow> windows = new CopyOnWriteArrayList<>();

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

        initXYListeners();

        windows.add(this);
    }

    private void initXYListeners() {
        double appW = FXGL.getSettings().getWidth();
        double appH = FXGL.getSettings().getHeight();

        layoutXProperty().addListener((observable, oldValue, newValue) -> {
            int newX = newValue.intValue();
            int width = (int) getWidth();
            int newMaxX = newX + width;

            if (snapToWindows) {
                for (InGameWindow window : windows) {
                    if (window == this)
                        continue;

                    int nodeMinX = (int) window.getLayoutX();
                    int nodeMaxX = (int) (window.getLayoutX() + window.getWidth());

                    if (between(newX, nodeMaxX - SNAP_SIZE, nodeMaxX + SNAP_SIZE)) {
                        relocateX(nodeMaxX);
                        break;
                    }

                    if (between(newMaxX, nodeMinX - SNAP_SIZE, nodeMinX + SNAP_SIZE)) {
                        relocateX(nodeMinX - width);
                        break;
                    }
                }
            }

            if (snapToScreen) {
                if (newX > 0 && newX <= SNAP_SIZE) {
                    relocateX(0);
                } else if (newMaxX < appW && newMaxX >= appW - SNAP_SIZE) {
                    relocateX(appW - width);
                }
            }

            if (canGoOffscreen)
                return;

            if (newX < 0)
                relocateX(0);
            else if (newMaxX > appW)
                relocateX(appW - width);
        });

        layoutYProperty().addListener((observable, oldValue, newValue) -> {
            int newY = newValue.intValue();
            int height = (int) getHeight();
            int newMaxY = newY + height;

            if (snapToWindows) {
                for (InGameWindow window : windows) {
                    if (window == this)
                        continue;

                    int nodeMinY = (int) window.getLayoutY();
                    int nodeMaxY = (int) (window.getLayoutY() + window.getHeight());

                    if (between(newY, nodeMaxY - SNAP_SIZE, nodeMaxY + SNAP_SIZE)) {
                        relocateY(nodeMaxY);
                        break;
                    }

                    if (between(newMaxY, nodeMinY - SNAP_SIZE, nodeMinY + SNAP_SIZE)) {
                        relocateY(nodeMinY - height);
                        break;
                    }
                }
            }

            if (snapToScreen) {
                if (newY > 0 && newY <= SNAP_SIZE) {
                    relocateY(0);
                } else if (newMaxY < appH && newMaxY >= appH - SNAP_SIZE) {
                    relocateY(appH - (int)getHeight());
                }
            }

            if (canGoOffscreen)
                return;

            if (newY < 0)
                relocateY(0);
            else if (newMaxY > appH)
                relocateY(appH - (int)getHeight());
        });
    }

    /**
     * Set background color of this window.
     *
     * @param color background color
     */
    public final void setBackgroundColor(Paint color) {
        setBackground(new Background(new BackgroundFill(color, null, null)));
    }

    private boolean canGoOffscreen = false;

    public final void setCanGoOffscreen(boolean value) {
        canGoOffscreen = value;
    }

    public final boolean canGoOffscreen() {
        return canGoOffscreen;
    }

    private boolean snapToScreen = true;

    public final void setSnapToScreen(boolean value) {
        snapToScreen = value;
    }

    public final boolean isSnapToScreen() {
        return snapToScreen;
    }

    private boolean snapToWindows = true;

    public final void setSnapToWindows(boolean value) {
        snapToWindows = value;
    }

    public final boolean isSnapToWindows() {
        return snapToWindows;
    }

    public final void relocateX(double x) {
        setLayoutX(x - getLayoutBounds().getMinX());
    }

    public final void relocateY(double y) {
        setLayoutY(y - getLayoutBounds().getMinY());
    }

    /**
     * Set top-left position of the window.
     *
     * @param x left x
     * @param y top y
     */
    public final void setPosition(double x, double y) {
        relocate(x, y);
    }

    private static boolean between(double value, double min, double max) {
        return value > min && value < max;
    }

    @Override
    public void close() {
        super.close();
        windows.remove(this);
    }

    //    private static class WindowLayout {
//        final int id;
//        DoubleProperty layoutX, layoutY;
//        ReadOnlyObjectProperty<Bounds> layoutBounds;
//
//        public WindowLayout(int id, DoubleProperty layoutX, DoubleProperty layoutY,
//                            ReadOnlyObjectProperty<Bounds> layoutBounds) {
//            this.id = id;
//            this.layoutX = layoutX;
//            this.layoutY = layoutY;
//            this.layoutBounds = layoutBounds;
//        }
//    }

    public enum WindowDecor {
        NONE,
        MINIMIZE,
        CLOSE,
        ALL
    }
}
