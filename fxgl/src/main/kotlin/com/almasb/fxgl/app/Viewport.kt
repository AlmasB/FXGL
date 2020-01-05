/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.animation.AnimatedValue
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.math.FXGLMath.*
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.Entity
import javafx.beans.binding.Bindings
import javafx.beans.binding.NumberBinding
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.ReadOnlyDoubleWrapper
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

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

    /*
    Moving origin X and Y moves the viewport.
    Moving the camera does nothing.
    Explanation given below for camera's purpose.
     */

    /**
     * Origin x.
     */
    private val xProp = ReadOnlyDoubleWrapper()

    /**
     * Origin y.
     */
    private val yProp = ReadOnlyDoubleWrapper()

    fun xProperty(): ReadOnlyDoubleProperty = xProp.readOnlyProperty
    fun yProperty(): ReadOnlyDoubleProperty = yProp.readOnlyProperty

    var x: Double
        get() = xProp.value
        set(value) { xProp.value = value }

    var y: Double
        get() = yProp.value
        set(value) { yProp.value = value }

    /**
     * @return viewport origin (x, y)
     */
    val origin: Point2D
        get() = Point2D(x, y)

    /**
     * @return current visible viewport area
     */
    val visibleArea: Rectangle2D
        get() = Rectangle2D(x, y, width, height)

    private val zoom = SimpleDoubleProperty(1.0)
    fun getZoom() = zoom.get()
    fun zoomProperty() = zoom
    fun setZoom(value: Double) = zoomProperty().set(value)

    private val angle = SimpleDoubleProperty()
    fun getAngle() = angle.value
    fun angleProperty() = angle
    fun setAngle(value: Double) = angleProperty().set(value)

    private var boundX: NumberBinding? = null
    private var boundY: NumberBinding? = null

    private val minX = SimpleIntegerProperty(Integer.MIN_VALUE)
    private val minY = SimpleIntegerProperty(Integer.MIN_VALUE)
    private val maxX = SimpleIntegerProperty(Integer.MAX_VALUE)
    private val maxY = SimpleIntegerProperty(Integer.MAX_VALUE)

    var isLazy = false

    /**
     * Currently only supported for non-lazy and entity bound viewports.
     */
    var isFloating = false

    /**
     * This is only used for visual effects and acts like a viewport overlay.
     * Its x and y follow the actual x and y of viewport.
     */
    internal val camera = Entity()

    /**
     * Binds the viewport to entity so that it follows the given entity.
     * distX and distY represent bound distance between entity and viewport origin.
     *
     * bindToEntity(player, getWidth() / 2, getHeight() / 2);
     *
     * the code above centers the camera on player.
     *
     * @param entity the entity to follow
     * @param distX distance in X between origin and entity
     * @param distY distance in Y between origin and entity
     */
    fun bindToEntity(entity: Entity, distX: Double, distY: Double) {
        val position = entity.transformComponent

        // origin X Y with no bounds
        val bx = position.xProperty().add(-distX)
        val by = position.yProperty().add(-distY)

        // origin X Y with bounds applied
        boundX = Bindings.`when`(bx.lessThan(minX)).then(minX).otherwise(position.xProperty().add(-distX))
        boundY = Bindings.`when`(by.lessThan(minY)).then(minY).otherwise(position.yProperty().add(-distY))

        boundX = Bindings.`when`(bx.greaterThan(maxX.subtract(width))).then(maxX.subtract(width)).otherwise(boundX)
        boundY = Bindings.`when`(by.greaterThan(maxY.subtract(height))).then(maxY.subtract(height)).otherwise(boundY)
    }

//    fun bindToFit(xMargin: Double, yMargin: Double, vararg entities: Entity) {
//        val minBindingX = entities.filter { it.hasComponent(BoundingBoxComponent::class.java) }
//                .map { it.getComponent(BoundingBoxComponent::class.java) }
//                .map { it.minXWorldProperty() }
//                .fold(Bindings.min(SimpleIntegerProperty(Int.MAX_VALUE), Integer.MAX_VALUE), { min, x -> Bindings.min(min, x) })
//                .subtract(xMargin)
//
//        val minBindingY = entities.filter { it.hasComponent(BoundingBoxComponent::class.java) }
//                .map { it.getComponent(BoundingBoxComponent::class.java) }
//                .map { it.minYWorldProperty() }
//                .fold(Bindings.min(SimpleIntegerProperty(Int.MAX_VALUE), Integer.MAX_VALUE), { min, y -> Bindings.min(min, y) })
//                .subtract(yMargin)
//
//        val maxBindingX = entities.filter { it.hasComponent(BoundingBoxComponent::class.java) }
//                .map { it.getComponent(BoundingBoxComponent::class.java) }
//                .map { it.maxXWorldProperty() }
//                .fold(Bindings.max(SimpleIntegerProperty(Int.MIN_VALUE), Integer.MIN_VALUE), { max, x -> Bindings.max(max, x) })
//                .add(xMargin)
//
//        val maxBindingY = entities.filter { it.hasComponent(BoundingBoxComponent::class.java) }
//                .map { it.getComponent(BoundingBoxComponent::class.java) }
//                .map { it.maxYWorldProperty() }
//                .fold(Bindings.max(SimpleIntegerProperty(Int.MIN_VALUE), Integer.MIN_VALUE), { max, y -> Bindings.max(max, y) })
//                .add(yMargin)
//
//        val widthBinding = maxBindingX.subtract(minBindingX)
//        val heightBinding = maxBindingY.subtract(minBindingY)
//
//        val ratio = Bindings.min(Bindings.divide(width, widthBinding), Bindings.divide(height, heightBinding))
//
//        x.bind(minBindingX)
//        y.bind(minBindingY)
//
//        zoom.bind(ratio)
//    }

    /**
     * Unbind viewport.
     */
    fun unbind() {
        zoomProperty().unbind()
    }

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
        focusOn(entity.center)
    }

    fun focusOn(point: Point2D) {
        val newOrigin = point.subtract(FXGL.getAppWidth() / 2.0, FXGL.getAppHeight() / 2.0)

        x = newOrigin.x
        y = newOrigin.y
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

    fun shakeTranslational(power: Double) {
        shakePowerTranslate = power
        shakeAngle = randomDouble() * PI2

        // only record origin if not shaking, so that we don't record 'false' origin
        if (!shakingTranslate)
            originBeforeShake.set(xProp.floatValue(), yProp.floatValue())

        shakingTranslate = true
    }

    fun shakeRotational(power: Double) {
        shakePowerRotate = power

        // only record origin if not shaking, so that we don't record 'false' origin
        if (!shakingRotate)
            angleBeforeShake = angle.value

        shakingRotate = true
    }

    private val flashRect by lazy { Rectangle(FXGL.getAppWidth().toDouble(), FXGL.getAppHeight().toDouble(), Color.WHITE) }

    private val flashAnimatedValue = AnimatedValue(1.0, 0.0)

    private var flashTime = 0.0

    private var isFlashing = false
    private var isFading = false

    private var onFadeFlashFinish: Runnable = EmptyRunnable

    private var t = 0.0

    fun flash(onFinished: Runnable) {
        if (isFlashing || isFading)
            return

        onFadeFlashFinish = onFinished
        flashRect.opacity = 1.0
        isFlashing = true

        fadeFlash()
    }

    fun fade(onFinished: Runnable) {
        if (isFlashing || isFading)
            return

        onFadeFlashFinish = onFinished
        flashRect.opacity = 0.0
        isFading = true

        fadeFlash()
    }

    private fun fadeFlash() {
        camera.viewComponent.addChild(flashRect)

        flashTime = 0.0
    }

    fun onUpdate(tpf: Double) {
        t += tpf * 0.25

        if (isFlashing || isFading) {
            updateFadeFlash(tpf)
        }

        if (boundX != null) {
            setOriginFromBoundEntity()
        }

        if (!shakingRotate && !shakingTranslate)
            return

        if (shakingTranslate) {
            shakeTranslate()
        }

        if (shakingRotate) {
            shakeRotate()
        }

        updateCameraPosition()
    }

    private fun shakeTranslate() {
        shakePowerTranslate *= 0.9
        shakeAngle += 180 + randomDouble() * PI2 / 6
        offset.set(
                (shakePowerTranslate * cos(shakeAngle)).toFloat(),
                (shakePowerTranslate * sin(shakeAngle)).toFloat()
        )

        if (boundX != null) {
            setOriginFromBoundEntity(offset.x, offset.y)
        } else {
            x = offset.x + originBeforeShake.x.toDouble()
            y = offset.y + originBeforeShake.y.toDouble()
        }

        if (abs(offset.x) < 0.5 && abs(offset.y) < 0.5) {
            if (boundX != null) {
                setOriginFromBoundEntity()
            } else {
                x = originBeforeShake.x.toDouble()
                y = originBeforeShake.y.toDouble()
            }

            shakingTranslate = false
        }
    }

    private fun setOriginFromBoundEntity(offsetX: Float = 0.0f, offsetY: Float = 0.0f) {
        if (!isLazy) {
            x = offsetX + boundX!!.doubleValue()
            y = offsetY + boundY!!.doubleValue()

            // TODO: this should be easier to implement if we keep origin (x, y) data
            // without any transforms, so at any time we know where the origin is
            if (isFloating) {
                x += (noise1D(t) - 0.5) * 50
                y += (noise1D(t + 1500) - 0.5) * 50
            }

            return
        }

        val sourceX = offsetX + x
        val sourceY = offsetY + y

        /*(Asymptotic average:

            x = 0.9x + 0.1target;
            OR
            x += (target - x) * 0.1 * timeScale;
            Use 0.01 for 60fps

            timeScale 0 for pause, 0.1 for slow)*/

        x = sourceX * 0.9 + boundX!!.doubleValue() * 0.1
        y = sourceY * 0.9 + boundY!!.doubleValue() * 0.1
    }

    private fun updateFadeFlash(tpf: Double) {
        flashTime += tpf

        if (flashTime > 1.0) {
            flashTime = 1.0
            isFlashing = false
            isFading = false
            camera.viewComponent.removeChild(flashRect)

            onFadeFlashFinish.run()
        }

        val ratio = flashTime / 1.0

        val progress = if (isFading) 1 - ratio else ratio
        val opacity = flashAnimatedValue.getValue(progress)

        flashRect.opacity = opacity
    }

    private fun shakeRotate() {
        val maxAngle = 10.0

        shakePowerRotate *= 0.9

        // we can't use (FXGLMath.noise1D(time) - 0.5) yet
        // as it will just "shake" once and get to initial position
        setAngle(maxAngle * shakePowerRotate * random(-1.0, 1.0))

        if (abs(angle.value - angleBeforeShake) < 0.5) {
            setAngle(0.0)

            shakingRotate = false
        }
    }

    private fun updateCameraPosition() {
        camera.x = x
        camera.y = y
    }
}