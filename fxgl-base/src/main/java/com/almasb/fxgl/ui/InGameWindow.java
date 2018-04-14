/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import com.almasb.fxgl.app.FXGL;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Paint;

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
public class InGameWindow extends MDIWindow {

    private static final int SNAP_SIZE = 10;

    private static final CopyOnWriteArrayList<InGameWindow> windows = new CopyOnWriteArrayList<>();

    private final int appW;
    private final int appH;

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
        super();

        switch (decor) {
            case MINIMIZE:
                setCanMinimize(true);
                setCanClose(false);
                break;
            case CLOSE:
                setCanMinimize(false);
                setCanClose(true);
                break;
            case ALL:
                setCanMinimize(true);
                setCanClose(true);
                break;
            case NONE:  // fallthru
            default:    // do nothing
                break;
        }

        appW = FXGL.getSettings().getWidth();
        appH = FXGL.getSettings().getHeight();

        layoutXProperty().addListener(makeListenerX());
        layoutYProperty().addListener(makeListenerY());

        windows.add(this);

        setTitle(title);
    }

    private ChangeListener<Number> makeListenerX() {
        return (observable, oldValue, newValue) -> {
            int newX = newValue.intValue();
            int width = (int) getWidth();
            int newMaxX = newX + width;

            if (snapToWindows) {
                snapToWindowsX(newX);
            }

            if (snapToScreen) {
                if (newX > 0 && newX <= SNAP_SIZE) {
                    relocateX(0);
                } else if (newMaxX < appW && newMaxX >= appW - SNAP_SIZE) {
                    relocateX(appW - width);
                }
            }

            if (!canGoOffscreen) {
                keepOnScreenX(newX);
            }
        };
    }

    private ChangeListener<Number> makeListenerY() {
        return (observable, oldValue, newValue) -> {
            int newY = newValue.intValue();
            int height = (int) getHeight();
            int newMaxY = newY + height;

            if (snapToWindows) {
                snapToWindowsY(newY);
            }

            if (snapToScreen) {
                if (newY > 0 && newY <= SNAP_SIZE) {
                    relocateY(0);
                } else if (newMaxY < appH && newMaxY >= appH - SNAP_SIZE) {
                    relocateY(appH - (int)getHeight());
                }
            }

            if (!canGoOffscreen) {
                keepOnScreenY(newY);
            }
        };
    }

    private void snapToWindowsX(int newX) {
        int width = (int) getWidth();
        int newMaxX = newX + width;

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

    private void snapToWindowsY(int newY) {
        int height = (int) getHeight();
        int newMaxY = newY + height;

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

    private void keepOnScreenX(int newX) {
        int width = (int) getWidth();
        int newMaxX = newX + width;

        if (newX < 0)
            relocateX(0);
        else if (newMaxX > appW)
            relocateX(appW - width);
    }

    private void keepOnScreenY(int newY) {
        int height = (int) getHeight();
        int newMaxY = newY + height;

        if (newY < 0)
            relocateY(0);
        else if (newMaxY > appH)
            relocateY(appH - height);
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

    private static boolean between(int value, int min, int max) {
        return value > min && value < max;
    }

    //@Override
    public void close() {
        //super.close();
        windows.remove(this);
    }

    public enum WindowDecor {
        NONE,
        MINIMIZE,
        CLOSE,
        ALL
    }
}
