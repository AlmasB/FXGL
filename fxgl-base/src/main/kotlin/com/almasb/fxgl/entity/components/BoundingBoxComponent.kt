/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.pool.Pool
import com.almasb.fxgl.core.pool.Pools
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.CopyableComponent
import com.almasb.fxgl.entity.component.CoreComponent
import com.almasb.fxgl.entity.component.SerializableComponent
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.physics.CollisionResult
import com.almasb.fxgl.physics.HitBox
import com.almasb.fxgl.physics.SAT
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.ReadOnlyDoubleWrapper
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import java.util.*

/**
 * Component that adds bounding box information to an entity.
 * The bounding box itself comprises a collection of hit boxes.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@CoreComponent
class BoundingBoxComponent(vararg boxes: HitBox) :
        Component(),
        SerializableComponent,
        CopyableComponent<BoundingBoxComponent> {

    companion object {

        init {
            Pools.set(CollisionResult::class.java, object : Pool<CollisionResult>() {
                override fun newObject(): CollisionResult {
                    return CollisionResult()
                }
            })
        }
    }

    private lateinit var position: PositionComponent

    /**
     * Contains all hit boxes (collision bounding boxes) for this entity.
     */
    private val hitBoxes = FXCollections.observableArrayList<HitBox>()

    private val width = ReadOnlyDoubleWrapper()
    private val height = ReadOnlyDoubleWrapper()

    private val minXLocal = ReadOnlyDoubleWrapper()
    private val minYLocal = ReadOnlyDoubleWrapper()

    private val minXWorld = ReadOnlyDoubleWrapper()
    private val minYWorld = ReadOnlyDoubleWrapper()
    private val maxXWorld = ReadOnlyDoubleWrapper()
    private val maxYWorld = ReadOnlyDoubleWrapper()

    /**
     * Note: same as width, unless specified otherwise.
     *
     * @return max x of bbox in local coordinates
     */
    val maxXLocal: Double
        get() = getWidth()

    /**
     * Note: same as height, unless specified otherwise.
     *
     * @return max y of bbox in local coordinates
     */
    val maxYLocal: Double
        get() = getHeight()

    /**
     * @return center point in local coordinates
     */
    val centerLocal: Point2D
        get() = Point2D(getWidth() / 2, getHeight() / 2)

    /**
     * If entity doesn't have [PositionComponent] then the center point
     * is the same as local center.
     *
     * @return center point in world coordinates
     */
    val centerWorld: Point2D
        get() = centerLocal.add(getMinXWorld(), getMinYWorld())

    /**
     * @return min X poperty in local coordinates
     */
    fun minXLocalProperty(): ReadOnlyDoubleProperty = minXLocal.readOnlyProperty

    /**
     * @return min x of bbox in local coordinate system
     */
    fun getMinXLocal(): Double = minXLocal.value

    /**
     * @return min Y poperty in local coordinates
     */
    fun minYLocalProperty(): ReadOnlyDoubleProperty = minYLocal.readOnlyProperty

    /**
     * @return min y of bbox in local coordinate system
     */
    fun getMinYLocal(): Double = minYLocal.value

    fun minXWorldProperty(): ReadOnlyDoubleProperty = minXWorld.readOnlyProperty

    fun minYWorldProperty(): ReadOnlyDoubleProperty = minYWorld.readOnlyProperty

    fun maxXWorldProperty(): ReadOnlyDoubleProperty = maxXWorld.readOnlyProperty

    fun maxYWorldProperty(): ReadOnlyDoubleProperty = maxYWorld.readOnlyProperty

    /**
     * @return min x in world coordinate system
     */
    fun getMinXWorld() = position.x + getMinXLocal()

    /**
     * @return min y in world coordinate system
     */
    fun getMinYWorld() = position.y + getMinYLocal()

    /**
     * @return max x in world coordinates
     */
    fun getMaxXWorld() = position.x + getMinXLocal() + getWidth()

    /**
     * @return max y in world coordinates
     */
    fun getMaxYWorld() = position.y + getMinYLocal() + getHeight()

    private val onHitBoxChange = ListChangeListener<HitBox> { c ->
        minXLocal.set(computeMinXLocal())
        minYLocal.set(computeMinYLocal())
        width.set(computeWidth())
        height.set(computeHeight())

        while (c.next()) {
            if (c.wasAdded()) {
                for (box in c.getAddedSubList()) {
                    box.bindX(position.xProperty())
                    box.bindY(position.yProperty())
                }

            } else if (c.wasRemoved()) {
                for (box in c.getRemoved()) {
                    box.unbind()
                }
            }
        }
    }

    init {
        hitBoxes.addAll(*boxes)
        minXLocal.set(computeMinXLocal())
        minYLocal.set(computeMinYLocal())
        width.set(computeWidth())
        height.set(computeHeight())

        hitBoxes.addListener(onHitBoxChange)
    }

    override fun onAdded() {
        minXWorld.bind(minXLocal.add(position.xProperty()))
        minYWorld.bind(minYLocal.add(position.yProperty()))

        // TODO: use minXWorld
        maxXWorld.bind(minXLocal.add(position.xProperty()).add(width))
        maxYWorld.bind(minYLocal.add(position.yProperty()).add(height))

        for (i in hitBoxes.indices) {
            hitBoxes[i].bindX(position.xProperty())
            hitBoxes[i].bindX(position.yProperty())
        }
    }

    override fun onRemoved() {
        hitBoxes.removeListener(onHitBoxChange)
        for (i in hitBoxes.indices) {
            hitBoxes[i].unbind()
        }

        minXWorld.unbind()
        minYWorld.unbind()

        maxXWorld.unbind()
        maxYWorld.unbind()
    }

    /**
     * @return unmodifiable list of hit boxes
     */
    fun hitBoxesProperty(): ObservableList<HitBox> {
        return FXCollections.unmodifiableObservableList(hitBoxes)
    }

    /**
     * Add a hit (collision) bounding box.
     *
     * @param hitBox the bounding box
     */
    fun addHitBox(hitBox: HitBox) {
        hitBoxes.add(hitBox)
    }

    /**
     * Removes a hit box with given name from the list of hit boxes for this entity.
     *
     * @param name hit box name
     */
    fun removeHitBox(name: String) {
        hitBoxes.removeIf { h -> h.name == name }
    }

    /**
     * Remove all hit boxes.
     */
    fun clearHitBoxes() {
        hitBoxes.clear()
    }

    /**
     * Returns total width of the bounding box, i.e.
     * distance from the leftmost side to the rightmost side.
     *
     * @return width of the bounding box
     */
    fun getWidth(): Double = width.value

    /**
     * @return width property
     */
    fun widthProperty(): ReadOnlyDoubleProperty = width.readOnlyProperty

    /**
     * Returns total height of the bounding box, i.e.
     * distance from the topmost side to the bottommost side.
     *
     * @return height of the bounding box
     */
    fun getHeight(): Double = height.value

    /**
     * @return height property
     */
    fun heightProperty(): ReadOnlyDoubleProperty = height.readOnlyProperty

    /**
     * Computes width of entity based on its hit boxes.
     *
     * @return width
     */
    private fun computeWidth(): Double {
        return hitBoxes.map { it.bounds.maxX - getMinXLocal() }
                .max() ?: 0.0
    }

    /**
     * Computes height of entity based on its hit boxes.
     *
     * @return height
     */
    private fun computeHeight(): Double {
        return hitBoxes.map { it.bounds.maxY - getMinYLocal() }
                .max() ?: 0.0
    }

    private fun computeMinXLocal(): Double {
        return hitBoxes.map { it.minX }
                .min() ?: 0.0
    }

    private fun computeMinYLocal(): Double {
        return hitBoxes.map { it.minY }
                .min() ?: 0.0
    }

    /**
     * Internal GC-friendly (and has less checks than JavaFX's BoundingBox)
     * check for collision between two hit boxes.
     * Assuming hit boxes are bound to x, y of entities so the coords
     * are correctly translated into the world coord space.
     *
     * @param box1 hit box 1
     * @param box2 hit box 2
     * @return true iff box1 is colliding with box2
     */
    private fun checkCollision(box1: HitBox, box2: HitBox): Boolean {
        return box2.maxXWorld >= box1.minXWorld &&
                box2.maxYWorld >= box1.minYWorld &&
                box2.minXWorld <= box1.maxXWorld &&
                box2.minYWorld <= box1.maxYWorld
    }

    private fun checkCollision(box1: HitBox, box2: HitBox, angle1: Double, angle2: Double): Boolean {
        return SAT.isColliding(box1, box2, angle1, angle2)
    }

    /**
     * Checks for collision with another bounding box. Returns collision result
     * containing the first hit box that triggered collision.
     * If no collision - [CollisionResult.NO_COLLISION] will be returned.
     * If there is collision, the CollisionResult object must be put into pooler
     * after using the data.
     *
     * @param other bounding box to check collision against
     * @return collision result
     */
    fun checkCollision(other: BoundingBoxComponent): CollisionResult {
        return checkCollisionInternal(other, true)
    }

    /**
     * GC-friendly (no object allocations) version of [.checkCollision].
     *
     * @param other bbox of other entity
     * @return [CollisionResult.NO_COLLISION] if no collision, else [CollisionResult.COLLISION]
     */
    private fun checkCollisionInternal(other: BoundingBoxComponent, reportBoxes: Boolean): CollisionResult {
        for (i in hitBoxes.indices) {
            val box1 = hitBoxes[i]

            for (j in other.hitBoxes.indices) {
                val box2 = other.hitBoxes[j]

                val collision: Boolean

                val angle1 = getEntity().rotation
                val angle2 = other.getEntity().rotation

                if (angle1 == 0.0 && angle2 == 0.0) {
                    collision = checkCollision(box1, box2)
                } else {
                    collision = checkCollision(box1, box2, angle1, angle2)
                }

                if (collision) {
                    if (reportBoxes) {
                        val result = Pools.obtain(CollisionResult::class.java)
                        result.init(box1, box2)

                        return result
                    } else {
                        return CollisionResult.COLLISION
                    }
                }
            }
        }

        return CollisionResult.NO_COLLISION
    }

    /**
     * @param other the other entity
     * @return true iff this entity is colliding with other based on
     * their hit boxes, in current frame
     */
    fun isCollidingWith(other: BoundingBoxComponent): Boolean {
        return checkCollisionInternal(other, false) != CollisionResult.NO_COLLISION
    }

    /**
     * @param bounds a rectangular box that represents bounds
     * @return true iff entity is partially or entirely within given bounds
     */
    fun isWithin(bounds: Rectangle2D): Boolean {
        return isWithin(bounds.minX, bounds.minY, bounds.maxX, bounds.maxY)
    }

    /**
     * @param minX min x
     * @param minY min y
     * @param maxX max x
     * @param maxY max y
     * @return true iff entity is partially or entirely within given bounds
     */
    fun isWithin(minX: Double, minY: Double, maxX: Double, maxY: Double): Boolean {
        return !isOutside(minX, minY, maxX, maxY)
    }

    /**
     * @param bounds a rectangular box that represents bounds
     * @return true iff entity is completely outside given bounds
     */
    fun isOutside(bounds: Rectangle2D): Boolean {
        return isOutside(bounds.minX, bounds.minY, bounds.maxX, bounds.maxY)
    }

    /**
     * @param minX min x
     * @param minY min y
     * @param maxX max x
     * @param maxY max y
     * @return true iff entity is completely outside given bounds
     */
    fun isOutside(minX: Double, minY: Double, maxX: Double, maxY: Double): Boolean {
        return (position.x + getMinXLocal() + getWidth() <= minX || position.x + getMinXLocal() >= maxX
                || position.y + getMinYLocal() + getHeight() <= minY || position.y + getMinYLocal() >= maxY)
    }

    /**
     * Forms a rectangle around the entity by extending min and max bounds
     * with width in X and with height in Y directions.
     *
     * @param width width to extend by in each direction
     * @param height height to extend by in each direction
     * @return rectangular area
     */
    fun range(width: Double, height: Double): Rectangle2D {
        val minX = getMinXWorld() - width
        val minY = getMinYWorld() - height
        val maxX = getMaxXWorld() + width
        val maxY = getMaxYWorld() + height

        return Rectangle2D(minX, minY, maxX - minX, maxY - minY)
    }

    override fun write(bundle: Bundle) {
        bundle.put("hitBoxes", ArrayList(hitBoxes))
    }

    override fun read(bundle: Bundle) {
        hitBoxes.addAll(bundle.get<ArrayList<HitBox>>("hitBoxes"))
    }

    override fun copy(): BoundingBoxComponent {
        val list = ArrayList<HitBox>()
        for (hitBox in hitBoxes) {
            val copy = hitBox.copy()
            list.add(copy)
        }
        return BoundingBoxComponent(*list.toTypedArray())
    }

    override fun toString(): String {
        // lowercase, so that it gets sorted after Position, Rotation, Type, View
        return "bbox($hitBoxes)"
    }
}