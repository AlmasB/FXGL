/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.math

import java.io.Serializable

/**
 * A 3D vector with float precision.
 * Can be used to represent a point in 3D space.
 * Can be used instead of JavaFX Point3D to avoid object allocations.
 * This is also preferred for private or scoped fields.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Vec3
@JvmOverloads constructor(
        @JvmField var x: Float = 0f,
        @JvmField var y: Float = 0f,
        @JvmField var z: Float = 0f
) : Serializable {

    companion object {
        @JvmStatic private val serialVersionUID = 1L

        @JvmStatic fun dot(a: Vec3, b: Vec3): Float {
            return a.x * b.x + a.y * b.y + a.z * b.z
        }

        @JvmStatic fun cross(a: Vec3, b: Vec3): Vec3 {
            return Vec3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x)
        }
    }

    constructor(copy: Vec3) : this(copy.x, copy.y, copy.z)

    constructor(x: Double, y: Double, z: Double) : this(x.toFloat(), y.toFloat(), z.toFloat())

    fun set(other: Vec3): Vec3 {
        x = other.x
        y = other.y
        z = other.z
        return this
    }

    fun set(otherX: Float, otherY: Float, otherZ: Float): Vec3 {
        x = otherX
        y = otherY
        z = otherZ
        return this
    }

    fun addLocal(other: Vec3): Vec3 {
        x += other.x
        y += other.y
        z += other.z
        return this
    }

    fun subLocal(other: Vec3): Vec3 {
        x -= other.x
        y -= other.y
        z -= other.z
        return this
    }

    fun mulLocal(scalar: Float): Vec3 {
        x *= scalar
        y *= scalar
        z *= scalar
        return this
    }

    fun negateLocal(): Vec3 {
        x = -x
        y = -y
        z = -z
        return this
    }

    fun setZero() {
        x = 0f
        y = 0f
        z = 0f
    }

    fun copy(): Vec3 {
        return Vec3(this)
    }

    override fun toString(): String {
        return "($x,$y,$z)"
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + java.lang.Float.floatToIntBits(x)
        result = prime * result + java.lang.Float.floatToIntBits(y)
        result = prime * result + java.lang.Float.floatToIntBits(z)
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other === this)
            return true

        if (other is Vec3) {
            return java.lang.Float.floatToIntBits(x) == java.lang.Float.floatToIntBits(other.x)
                    && java.lang.Float.floatToIntBits(y) == java.lang.Float.floatToIntBits(other.y)
                    && java.lang.Float.floatToIntBits(z) == java.lang.Float.floatToIntBits(other.z)
        }

        return false
    }
}