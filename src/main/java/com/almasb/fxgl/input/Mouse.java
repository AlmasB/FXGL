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

package com.almasb.fxgl.input;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Holds mouse state information.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Mouse {
    Mouse() {
    }

    private Point2D gameXY = Point2D.ZERO;

    /**
     * @return mouse x in game coordinate system
     */
    public double getGameX() {
        return getGameXY().getX();
    }

    /**
     * @return mouse y in game coordinate system
     */
    public double getGameY() {
        return getGameXY().getY();
    }

    /**
     * @return cursor point in game coordinate system
     */
    public Point2D getGameXY() {
        return gameXY;
    }

    /**
     * Set game XY.
     *
     * @param gameXY cursor point in game coordinate system
     */
    void setGameXY(Point2D gameXY) {
        this.gameXY = gameXY;
    }

    /**
     * Hold the value of gameX and y coordinate of the mouse cursor
     * in the current frame (tick) within the screen coordinate system
     */
    private double screenX, screenY;

    /**
     *
     * @return mouse x in screen (app) coordinate system
     */
    public double getScreenX() {
        return screenX;
    }

    /**
     *
     * @return mouse y in screen (app) coordinate system
     */
    public double getScreenY() {
        return screenY;
    }

    /**
     *
     * @return cursor point in screen (app) coordinate system
     */
    public Point2D getScreenXY() {
        return new Point2D(screenX, screenY);
    }

    /**
     * @param gamePosition point in game world
     * @return vector from given point to mouse cursor point
     */
    public Point2D getVectorToCursor(Point2D gamePosition) {
        return getGameXY().subtract(gamePosition);
    }

    /**
     * @param gamePosition point in game world
     * @return vector from mouse cursor point to given point
     */
    public Point2D getVectorFromCursor(Point2D gamePosition) {
        return getVectorToCursor(gamePosition).multiply(-1);
    }

    /**
     * Hold the state of left and right
     * mouse buttons in the current frame (tick).
     */
    boolean leftPressed, rightPressed;

    /**
     *
     * @return true iff left mouse button is pressed
     */
    public boolean isLeftPressed() {
        return leftPressed;
    }

    /**
     *
     * @return true iff right mouse button is pressed
     */
    public boolean isRightPressed() {
        return rightPressed;
    }

    /**
     * Update state of mouse with data from JavaFX mouse event.
     */
    void update(MouseEvent event) {
        this.event = event;
        this.screenX = event.getSceneX();
        this.screenY = event.getSceneY();

        if (leftPressed) {
            if (event.getButton() == MouseButton.PRIMARY && isReleased(event)) {
                leftPressed = false;
            }
        } else {
            leftPressed = event.getButton() == MouseButton.PRIMARY && isPressed(event);
        }

        if (rightPressed) {
            if (event.getButton() == MouseButton.SECONDARY && isReleased(event)) {
                rightPressed = false;
            }
        } else {
            rightPressed = event.getButton() == MouseButton.SECONDARY && isPressed(event);
        }
    }

    private boolean isPressed(MouseEvent event) {
        return event.getEventType() == MouseEvent.MOUSE_PRESSED
                || event.getEventType() == MouseEvent.MOUSE_DRAGGED;
    }

    private boolean isReleased(MouseEvent event) {
        return event.getEventType() == MouseEvent.MOUSE_RELEASED
                || event.getEventType() == MouseEvent.MOUSE_MOVED;
    }

    /**
     * The last internal event
     */
    private MouseEvent event;

    /**
     * It's unlikely that you'll need this.
     *
     * @return last JavaFX mouse event
     */
    public MouseEvent getEvent() {
        return event;
    }
}
