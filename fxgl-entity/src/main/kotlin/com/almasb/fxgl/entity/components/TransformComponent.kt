/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.math.FXGLMath.cosDeg
import com.almasb.fxgl.core.math.FXGLMath.sinDeg
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.CoreComponent
import com.almasb.fxgl.entity.component.SerializableComponent
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Point2D
import javafx.geometry.Point3D
import java.lang.Math.abs
import java.lang.Math.asin

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@CoreComponent
class TransformComponent(x: Double, y: Double, angle: Double, scaleX: Double, scaleY: Double) :
        Component(),
        SerializableComponent {

    constructor(p: Point2D) : this(p.x, p.y, 0.0, 1.0, 1.0)
    constructor() : this(0.0, 0.0, 0.0, 1.0, 1.0)

    private val propX: DoubleProperty = SimpleDoubleProperty(x)
    private val propY: DoubleProperty = SimpleDoubleProperty(y)
    private val propZ: DoubleProperty = SimpleDoubleProperty(0.0)

    private val propScaleX: DoubleProperty = SimpleDoubleProperty(scaleX)
    private val propScaleY: DoubleProperty = SimpleDoubleProperty(scaleY)
    private val propScaleZ: DoubleProperty = SimpleDoubleProperty(1.0)

    private val propPositionOriginX = SimpleDoubleProperty(0.0)
    private val propPositionOriginY = SimpleDoubleProperty(0.0)
    private val propPositionOriginZ = SimpleDoubleProperty(0.0)

    private val propScaleOriginX = SimpleDoubleProperty(0.0)
    private val propScaleOriginY = SimpleDoubleProperty(0.0)
    private val propScaleOriginZ = SimpleDoubleProperty(0.0)

    private val propRotationOriginX = SimpleDoubleProperty(0.0)
    private val propRotationOriginY = SimpleDoubleProperty(0.0)
    private val propRotationOriginZ = SimpleDoubleProperty(0.0)

    private val propRotationX = SimpleDoubleProperty(0.0)
    private val propRotationY = SimpleDoubleProperty(0.0)
    private val propRotationZ = SimpleDoubleProperty(angle)

    var x: Double
        get() = propX.value
        set(value) { propX.value = value }

    var y: Double
        get() = propY.value
        set(value) { propY.value = value }

    var z: Double
        get() = propZ.value
        set(value) { propZ.value = value }

    /**
     * Rotation angle in 2D (along the Z axis).
     */
    var angle: Double
        get() = propRotationZ.value
        set(value) { propRotationZ.value = value }

    var scaleX: Double
        get() = propScaleX.value
        set(value) { propScaleX.value = value }

    var scaleY: Double
        get() = propScaleY.value
        set(value) { propScaleY.value = value }

    var scaleZ: Double
        get() = propScaleZ.value
        set(value) { propScaleZ.value = value }

    var position: Point2D
        get() = Point2D(x, y)
        set(value) { setPosition(value.x, value.y) }

    var position3D: Point3D
        get() = Point3D(x, y, z)
        set(value) { setPosition3D(value.x, value.y, value.z) }

    var rotationX: Double
        get() = propRotationX.value
        set(value) { propRotationX.value = value }

    var rotationY: Double
        get() = propRotationY.value
        set(value) { propRotationY.value = value }

    var rotationZ: Double
        get() = propRotationZ.value
        set(value) { propRotationZ.value = value }

    var scaleOrigin: Point2D
        get() = Point2D(propScaleOriginX.value, propScaleOriginY.value)
        set(value) {
            propScaleOriginX.value = value.x
            propScaleOriginY.value = value.y
        }

    var scaleOrigin3D: Point3D
        get() = Point3D(propScaleOriginX.value, propScaleOriginY.value, propScaleOriginZ.value)
        set(value) {
            propScaleOriginX.value = value.x
            propScaleOriginY.value = value.y
            propScaleOriginZ.value = value.z
        }

    var rotationOrigin: Point2D
        get() = Point2D(propRotationOriginX.value, propRotationOriginY.value)
        set(value) {
            propRotationOriginX.value = value.x
            propRotationOriginY.value = value.y
        }

    var rotationOrigin3D: Point3D
        get() = Point3D(propRotationOriginX.value, propRotationOriginY.value, propRotationOriginZ.value)
        set(value) {
            propRotationOriginX.value = value.x
            propRotationOriginY.value = value.y
            propRotationOriginZ.value = value.z
        }

    fun xProperty() = propX
    fun yProperty() = propY
    fun zProperty() = propZ

    fun scaleXProperty() = propScaleX
    fun scaleYProperty() = propScaleY
    fun scaleZProperty() = propScaleZ

    /**
     * @return angle for 2D rotations (along Z axis)
     */
    fun angleProperty() = propRotationZ

    fun positionOriginXProperty() = propPositionOriginX
    fun positionOriginYProperty() = propPositionOriginY
    fun positionOriginZProperty() = propPositionOriginZ

    fun scaleOriginXProperty() = propScaleOriginX
    fun scaleOriginYProperty() = propScaleOriginY
    fun scaleOriginZProperty() = propScaleOriginY

    fun rotationOriginXProperty() = propRotationOriginX
    fun rotationOriginYProperty() = propRotationOriginY
    fun rotationOriginZProperty() = propRotationOriginZ

    fun rotationXProperty() = propRotationX
    fun rotationYProperty() = propRotationY
    fun rotationZProperty() = propRotationZ

    fun setPosition(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    fun setPosition3D(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    /**
     * Translate X by given value.
     *
     * @param x dx
     */
    fun translateX(x: Double) {
        this.x += x
    }

    /**
     * Translate Y by given value.
     *
     * @param y dy
     */
    fun translateY(y: Double) {
        this.y += y
    }

    /**
     * Translate Z by given value.
     *
     * @param z dz
     */
    fun translateZ(z: Double) {
        this.z += z
    }

    /**
     * Translate x and y by given values.
     *
     * @param x dx value
     * @param y dy value
     */
    fun translate(x: Double, y: Double) {
        translateX(x)
        translateY(y)
    }

    /**
     * Translate x, y and z by given values.
     */
    fun translate3D(x: Double, y: Double, z: Double) {
        translateX(x)
        translateY(y)
        translateZ(z)
    }

    /**
     * Translate x and y by given vector.
     *
     * @param vector translate vector
     */
    fun translate(vector: Point2D) {
        translate(vector.x, vector.y)
    }

    /**
     * Translate x, y and z by given vector.
     *
     * @param vector translate vector
     */
    fun translate3D(vector: Point3D) {
        translate3D(vector.x, vector.y, vector.z)
    }

    /**
     * @param position the point to move towards
     * @param distance the distance to move
     */
    fun translateTowards(position: Point2D, distance: Double) {
        translate(position.subtract(x, y).normalize().multiply(distance))
    }

    /**
     * @param other the other component
     * @return distance in pixels from this position to the other
     */
    fun distance(other: TransformComponent): Double {
        return position.distance(other.position)
    }

    fun distance3D(other: TransformComponent): Double {
        return position3D.distance(other.position3D)
    }

    /**
     * Rotate entity view by given angle.
     * Note: this doesn't affect hit boxes. For more accurate
     * collisions use [com.almasb.fxgl.physics.PhysicsComponent].
     *
     * @param byAngle rotation angle in degrees
     */
    fun rotateBy(byAngle: Double) {
        propRotationZ.value += byAngle
    }

    /**
     * Set absolute rotation of the entity view to angle
     * between vector and positive X axis.
     * This is useful for projectiles (bullets, arrows, etc)
     * which rotate depending on their current velocity.
     * Note, this assumes that at 0 angle rotation the scene view is
     * facing right.
     *
     * @param vector the rotation vector / velocity vector
     */
    fun rotateToVector(vector: Point2D) {
        propRotationZ.value = Math.toDegrees(Math.atan2(vector.y, vector.x))
    }

    /**
     * Point in pixels in local coordinates relative to (0, 0) of this transform component.
     */
    var localAnchor: Point2D = Point2D.ZERO

    /**
     * Position with local anchor offset, i.e.
     * set top left point of this entity in a way that given entity's local anchor
     * will be located at given (x, y) in world coordinates.
     */
    var anchoredPosition: Point2D
        get() = Point2D(x + localAnchor.x, y + localAnchor.y)
        set(value) {
            setPosition(value.x - localAnchor.x, value.y - localAnchor.y)
        }

    // 3D transformations
    // Note: these are super simplified XZ-only alternatives of "proper" 3D transformations

    /**
     * Unit vector that always points to where this transform is "looking at".
     */
    var direction3D: Point3D = Point3D(0.0, 0.0, 1.0)
        private set

    var up3D = Point3D(0.0, -1.0, 0.0)
        private set

    fun lookUpBy(angle: Double) {
        propRotationX.value += angle

        updateDirection()
    }

    fun lookDownBy(angle: Double) {
        propRotationX.value -= angle

        updateDirection()
    }

    fun lookLeftBy(angle: Double) {
        propRotationY.value -= angle

        updateDirection()
    }

    fun lookRightBy(angle: Double) {
        propRotationY.value += angle

        updateDirection()
    }

    fun lookAt(point: Point3D) {
        val directionToLook = point.subtract(x, y, z)

        // ignore the Y axis and use XZ as 2D plane
        rotationY = 90 - Math.toDegrees(Math.atan2(directionToLook.z, directionToLook.x))

        // theta = asin ( opposite side / hypotenuse )
        val theta = Math.toDegrees(asin(abs(directionToLook.y) / directionToLook.magnitude()))

        if (directionToLook.y > 0) {
            // looking down, range -90..0
            rotationX = -theta

        } else {
            // looking up, range 0..90
            rotationX = theta
        }

        updateDirection()
    }

    /**
     * Move forward on the XZ plane.
     * No Y movement.
     */
    fun moveForwardXZ(distance: Double) {
        val vector = direction3D.multiply(distance)

        translate3D(vector.x, 0.0, vector.z)
    }

    /**
     * Move back on the XZ plane.
     * No Y movement.
     */
    fun moveBackXZ(distance: Double) {
        val vector = direction3D.multiply(distance)

        translate3D(-vector.x, 0.0, -vector.z)
    }

    /**
     * Move forward along [direction3D] (the way this transform is facing).
     */
    fun moveForward(distance: Double) {
        val vector = direction3D.multiply(distance)

        translate3D(vector)
    }

    /**
     * Move back along [direction3D] (the opposite of the way this transform is facing).
     */
    fun moveBack(distance: Double) {
        val vector = direction3D.multiply(-distance)

        translate3D(vector)
    }

    /**
     * Move left on the XZ plane.
     * No Y movement.
     */
    fun moveLeft(distance: Double) {
        val left = up3D.crossProduct(direction3D)
                .normalize()
                .multiply(distance)

        translateX(left.x)
        translateZ(left.z)
    }

    /**
     * Move right on the XZ plane.
     * No Y movement.
     */
    fun moveRight(distance: Double) {
        val right = direction3D.crossProduct(up3D)
                .normalize()
                .multiply(distance)

        translateX(right.x)
        translateZ(right.z)
    }

    private fun updateDirection() {
        // 1. handle rotation Y since it is added first
        // we adjust it since 0 deg is not Point3D(0.0, 0.0, 1.0) (which is what we need) but Point3D(1.0, 0.0, 0.0)
        val adjustedRotationY = 90 - rotationY

        direction3D = Point3D(cosDeg(adjustedRotationY), 0.0, sinDeg(adjustedRotationY)).normalize()

        // 2. handle rotation X
        direction3D = Point3D(direction3D.x, -sinDeg(rotationX), direction3D.z * cosDeg(rotationX)).normalize()
    }

    override fun toString(): String {
        return "Transform($x, $y, $angle, $scaleX, $scaleY)"
    }

    override fun write(bundle: Bundle) {
        bundle.put("propX", x)
        bundle.put("propY", y)
        bundle.put("angle", angle)
        bundle.put("scaleX", scaleX)
        bundle.put("scaleY", scaleY)
        bundle.put("scaleOriginX", scaleOrigin.x)
        bundle.put("scaleOriginY", scaleOrigin.y)
        bundle.put("rotationOriginX", rotationOrigin.x)
        bundle.put("rotationOriginY", rotationOrigin.y)
    }

    override fun read(bundle: Bundle) {
        setPosition(bundle.get("propX"), bundle.get("propY"))
        scaleOrigin = Point2D(bundle.get("scaleOriginX"), bundle.get("scaleOriginY"))
        rotationOrigin = Point2D(bundle.get("rotationOriginX"), bundle.get("rotationOriginY"))
        angle = bundle.get("angle")
        scaleX = bundle.get("scaleX")
        scaleY = bundle.get("scaleY")
    }

    override fun isComponentInjectionRequired(): Boolean = false
}
