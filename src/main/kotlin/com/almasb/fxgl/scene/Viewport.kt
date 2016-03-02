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

package com.almasb.fxgl.scene

import com.almasb.ents.Entity
import com.almasb.fxgl.entity.component.PositionComponent
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D

/**
 * Game scene viewport.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Viewport

/**
 * Constructs a viewport with given width and height.
 *
 * @param width viewport width
 * @param height viewport height
 */
(
        /**
         * @return viewport width
         */
        val width: Double,

        /**
         * @return viewport height
         */
        val height: Double) {

    /**
     * @return current visible viewport area
     */
    val visibleArea: Rectangle2D
        get() = Rectangle2D(getX(), getY(), getX() + width, getY() + height)

    /**
     * Origin x.
     */
    private val x = SimpleDoubleProperty()
    fun getX() = x.get()
    fun xProperty() = x
    fun setX(x: Double) = xProperty().set(x)

    /**
     * Origin y.
     */
    private val y = SimpleDoubleProperty()
    fun getY() = y.get()
    fun yProperty() = y
    fun setY(y: Double) = yProperty().set(y)

    /**
     * @return viewport origin (x, y)
     */
    val origin: Point2D
        get() = Point2D(getX(), getY())

    /**
     * Binds the viewport to entity so that it follows the given entity.
     * distX and distY represent bound distance between entity and viewport origin.
     *
     * bindToEntity(player, getWidth() / 2, getHeight() / 2);
     *
     * the code above centers the camera on player.
     *
     * @param entity the entity to follow
     *
     * @param distX distance in X between origin and entity
     *
     * @param distY distance in Y between origin and entity
     */
    fun bindToEntity(entity: Entity, distX: Double, distY: Double) {
        val position = entity.getComponent(PositionComponent::class.java)
                .orElseThrow{ IllegalArgumentException("Cannot bind to entity without PositionComponent") }

        // origin X Y with no bounds
        val bx = position.xProperty().add(-distX)
        val by = position.yProperty().add(-distY)

        // origin X Y with bounds applied
        var boundX = Bindings.`when`(bx.lessThan(minX)).then(minX).otherwise(position.xProperty().add(-distX))
        var boundY = Bindings.`when`(by.lessThan(minY)).then(minY).otherwise(position.yProperty().add(-distY))

        boundX = Bindings.`when`(bx.greaterThan(maxX.subtract(width))).then(maxX.subtract(width)).otherwise(boundX)
        boundY = Bindings.`when`(by.greaterThan(maxY.subtract(height))).then(maxY.subtract(height)).otherwise(boundY)

        x.bind(boundX)
        y.bind(boundY)
    }

    /**
     * Unbind viewport.
     */
    fun unbind() {
        xProperty().unbind()
        yProperty().unbind()
    }

    private val minX = SimpleIntegerProperty(Integer.MIN_VALUE)
    private val minY = SimpleIntegerProperty(Integer.MIN_VALUE)
    private val maxX = SimpleIntegerProperty(Integer.MAX_VALUE)
    private val maxY = SimpleIntegerProperty(Integer.MAX_VALUE)

    /**
     * Set bounds to viewport so that the viewport will not move outside the bounds
     * when following an entity.
     *
     * @param minX min x
     * @param minY min y
     * @param maxX max x
     * @param maxY max y
     */
    fun setBounds(minX: Int, minY: Int, maxX: Int, maxY: Int) {
        this.minX.set(minX)
        this.minY.set(minY)
        this.maxX.set(maxX)
        this.maxY.set(maxY)
    }
}