/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.components.BoundingBoxComponent
import com.almasb.fxgl.entity.components.PositionComponent
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D

/**
 * Scene viewport.
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
        get() = Rectangle2D(getX(), getY(), width, height)

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

    private val zoom = SimpleDoubleProperty(1.0)
    fun getZoom() = zoom.get()
    fun zoomProperty() = zoom
    fun setZoom(value: Double) = zoomProperty().set(value)

    /**
     * @return viewport origin (x, y)
     */
    val origin: Point2D
        get() = Point2D(getX(), getY())

    private val angle = SimpleDoubleProperty()
    fun getAngle() = angle.value
    fun angleProperty() = angle
    fun setAngle(value: Double) = angleProperty().set(value)

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
        val position = entity.getComponentOptional(PositionComponent::class.java)
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

    fun bindToFit(xMargin: Double, yMargin: Double, vararg entities: Entity) {
        val minBindingX = entities.filter { it.hasComponent(BoundingBoxComponent::class.java) }
                .map { it.getComponent(BoundingBoxComponent::class.java) }
                .map { it.minXWorldProperty() }
                .fold(Bindings.min(SimpleIntegerProperty(Int.MAX_VALUE), Integer.MAX_VALUE), { min, x -> Bindings.min(min, x) })
                .subtract(xMargin)

        val minBindingY = entities.filter { it.hasComponent(BoundingBoxComponent::class.java) }
                .map { it.getComponent(BoundingBoxComponent::class.java) }
                .map { it.minYWorldProperty() }
                .fold(Bindings.min(SimpleIntegerProperty(Int.MAX_VALUE), Integer.MAX_VALUE), { min, y -> Bindings.min(min, y) })
                .subtract(yMargin)

        val maxBindingX = entities.filter { it.hasComponent(BoundingBoxComponent::class.java) }
                .map { it.getComponent(BoundingBoxComponent::class.java) }
                .map { it.maxXWorldProperty() }
                .fold(Bindings.max(SimpleIntegerProperty(Int.MIN_VALUE), Integer.MIN_VALUE), { max, x -> Bindings.max(max, x) })
                .add(xMargin)

        val maxBindingY = entities.filter { it.hasComponent(BoundingBoxComponent::class.java) }
                .map { it.getComponent(BoundingBoxComponent::class.java) }
                .map { it.maxYWorldProperty() }
                .fold(Bindings.max(SimpleIntegerProperty(Int.MIN_VALUE), Integer.MIN_VALUE), { max, y -> Bindings.max(max, y) })
                .add(yMargin)

        val widthBinding = maxBindingX.subtract(minBindingX)
        val heightBinding = maxBindingY.subtract(minBindingY)

        val ratio = Bindings.min(Bindings.divide(width, widthBinding), Bindings.divide(height, heightBinding))

        x.bind(minBindingX)
        y.bind(minBindingY)

        zoom.bind(ratio)
    }

    /**
     * Unbind viewport.
     */
    fun unbind() {
        xProperty().unbind()
        yProperty().unbind()
        zoomProperty().unbind()
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

    fun focusOn(entity: Entity) {
        val newOrigin = entity.center.subtract(FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0)

        setX(newOrigin.x)
        setY(newOrigin.y)
    }

    // adapted from https://gamedev.stackexchange.com/questions/1828/realistic-camera-screen-shake-from-explosion
    // modified with https://www.youtube.com/watch?v=tu-Qe66AvtY
    // getPerlinNoise(seed, time, ...);
    // getPerlinNoise(seed + 1, time, ...);
    // etc. for each random call (instead of getRandomFloatNegativeOnetoOne())

    /*
    angle = maxAngle * shake * getRandomFloatNegativeOnetoOne();
    offsetX = maxOffset * shake * getRandomFloatNegativeOnetoOne();
    offsetY = maxOffset * shake * getRandomFloatNegativeOnetoOne();
    ```
    Alternatively,

    ```
    getPerlinNoise(seed, time, ...);
    getPerlinNoise(seed + 1, time, ...);
    // etc. for each random call (instead of getRandomFloatNegativeOnetoOne())
    ```

    Then (note this will need to be adapted to FXGL viewport)
    ```
    newCamera.angle = camera.angle + angle;
    newCamera.center = camera.center + (offsetX, offsetY);
     */
    
    private var shakePowerTranslate = 0.0
    private var shakePowerRotate = 0.0
    private var shakeAngle = 0.0

    private val originBeforeShake = Vec2()
    private var angleBeforeShake = 0.0

    private val offset = Vec2()

    //private var shake = false
    private var shakingTranslate = false
    private var shakingRotate = false

    fun shake(powerTranslate: Double, powerRotate: Double) {
        shakeTranslational(powerTranslate)
        shakeRotational(powerRotate)
    }

    @Deprecated("use shake(double, double)")
    fun shake(power: Double) {
        shakeTranslational(power)
    }

    fun shakeTranslational(power: Double) {
        shakePowerTranslate = power
        shakeAngle = FXGLMath.random() * FXGLMath.PI2

        // only record origin if not shaking, so that we don't record 'false' origin
        if (!shakingTranslate)
            originBeforeShake.set(x.floatValue(), y.floatValue())

        shakingTranslate = true
    }

    fun shakeRotational(power: Double) {
        shakePowerRotate = power

        // only record origin if not shaking, so that we don't record 'false' origin
        if (!shakingRotate)
            angleBeforeShake = angle.value

        shakingRotate = true
    }

    private var time = 0.0

    fun onUpdate(tpf: Double) {
        time += tpf

        if (!shakingRotate && !shakingTranslate)
            return

        if (shakingTranslate) {
            shakePowerTranslate *= 0.9
            shakeAngle += 180 + FXGLMath.random() * FXGLMath.PI2 / 6
            offset.set((shakePowerTranslate * FXGLMath.cos(shakeAngle)).toFloat(),
                    (shakePowerTranslate * FXGLMath.sin(shakeAngle)).toFloat())

            setX(offset.x + originBeforeShake.x.toDouble())
            setY(offset.y + originBeforeShake.y.toDouble())

            if (FXGLMath.abs(offset.x) < 0.5 && FXGLMath.abs(offset.y) < 0.5) {
                setX(originBeforeShake.x.toDouble())
                setY(originBeforeShake.y.toDouble())

                shakingTranslate = false
            }
        }

        if (shakingRotate) {
            val maxAngle = 10.0

            shakePowerRotate *= 0.9

            // we can't use (FXGLMath.noise1D(time) - 0.5) yet
            // as it will just "shake" once and get to initial position
            setAngle(maxAngle * shakePowerRotate * FXGLMath.random(-1.0, 1.0))

            if (FXGLMath.abs(angle.value - angleBeforeShake) < 0.5) {
                setAngle(0.0)

                shakingRotate = false
            }
        }
    }
}