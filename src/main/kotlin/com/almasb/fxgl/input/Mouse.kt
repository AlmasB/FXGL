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

package com.almasb.fxgl.input

import javafx.geometry.Point2D
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent

/**
 * Holds mouse state information.

 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Mouse internal constructor() {

    /**
     * @return cursor point in game coordinate system
     */
    /**
     * Set game XY.

     * @param gameXY cursor point in game coordinate system
     */
    var gameXY = Point2D.ZERO
        internal set

    /**
     * @return mouse x in game coordinate system
     */
    val gameX: Double
        get() = gameXY.x

    /**
     * @return mouse y in game coordinate system
     */
    val gameY: Double
        get() = gameXY.y

    /**
     * Hold the value of gameX and y coordinate of the mouse cursor
     * in the current frame (tick) within the screen coordinate system
     */
    /**

     * @return mouse x in screen (app) coordinate system
     */
    var screenX: Double = 0.0
        private set

    /**
     * @return mouse y in screen (app) coordinate system
     */
    var screenY: Double = 0.0
        private set

    /**
     * @return cursor point in screen (app) coordinate system
     */
    val screenXY: Point2D
        get() = Point2D(screenX, screenY)

    /**
     * @param gamePosition point in game world
     * *
     * @return vector from given point to mouse cursor point
     */
    fun getVectorToCursor(gamePosition: Point2D) = gameXY.subtract(gamePosition)

    /**
     * @param gamePosition point in game world
     * *
     * @return vector from mouse cursor point to given point
     */
    fun getVectorFromCursor(gamePosition: Point2D) = getVectorToCursor(gamePosition).multiply(-1.0)

    /**
     * Hold the state of left and right
     * mouse buttons in the current frame (tick).
     */
    /**

     * @return true iff left mouse button is pressed
     */
    var isLeftPressed: Boolean = false
        internal set
    /**

     * @return true iff right mouse button is pressed
     */
    var isRightPressed: Boolean = false
        internal set

    /**
     * Update state of mouse with data from JavaFX mouse event.
     */
    internal fun update(event: MouseEvent) {
        this.event = event
        this.screenX = event.sceneX
        this.screenY = event.sceneY

        if (isLeftPressed) {
            if (event.button == MouseButton.PRIMARY && isReleased(event)) {
                isLeftPressed = false
            }
        } else {
            isLeftPressed = event.button == MouseButton.PRIMARY && isPressed(event)
        }

        if (isRightPressed) {
            if (event.button == MouseButton.SECONDARY && isReleased(event)) {
                isRightPressed = false
            }
        } else {
            isRightPressed = event.button == MouseButton.SECONDARY && isPressed(event)
        }
    }

    private fun isPressed(event: MouseEvent) =
            event.eventType == MouseEvent.MOUSE_PRESSED || event.eventType == MouseEvent.MOUSE_DRAGGED

    private fun isReleased(event: MouseEvent) =
            event.eventType == MouseEvent.MOUSE_RELEASED || event.eventType == MouseEvent.MOUSE_MOVED

    /**
     * The last internal event
     */
    /**
     * It's unlikely that you'll need this.

     * @return last JavaFX mouse event
     */
    var event: MouseEvent? = null
        private set
}
