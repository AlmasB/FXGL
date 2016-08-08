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

package com.almasb.fxgl.entity.component;

import com.almasb.easyio.serialization.Bundle;
import com.almasb.ents.AbstractComponent;
import com.almasb.ents.CopyableComponent;
import com.almasb.ents.Entity;
import com.almasb.ents.component.Required;
import com.almasb.ents.serialization.SerializableComponent;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.physics.CollisionResult;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.util.Pooler;
import com.almasb.gameutils.pool.Pool;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Component that adds bounding box information to an entity.
 * The bounding box itself comprises a collection of hit boxes.
 *
 * TODO: enforce at least 1 hit box rule, this also optimizes a lot of stuff
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
public class BoundingBoxComponent extends AbstractComponent
        implements SerializableComponent, CopyableComponent<BoundingBoxComponent> {

    private static final Pooler pooler = FXGL.getPooler();

    static {
        pooler.registerPool(CollisionResult.class, new Pool<CollisionResult>() {
            @Override
            protected CollisionResult newObject() {
                return new CollisionResult();
            }
        });
    }

    public BoundingBoxComponent(HitBox... boxes) {
        hitBoxes.addAll(boxes);
        minXLocal.set(computeMinXLocal());
        minYLocal.set(computeMinYLocal());
        width.set(computeWidth());
        height.set(computeHeight());

        hitBoxes.addListener((ListChangeListener<? super HitBox>) c -> {
            minXLocal.set(computeMinXLocal());
            minYLocal.set(computeMinYLocal());
            width.set(computeWidth());
            height.set(computeHeight());
        });
    }

    private PositionComponent position;

    @Override
    public void onAdded(Entity entity) {
        position = entity.getComponentUnsafe(PositionComponent.class);
    }

    /**
     * Contains all hit boxes (collision bounding boxes) for this entity.
     */
    private ObservableList<HitBox> hitBoxes = FXCollections.observableArrayList();

    /**
     * @return unmodifiable list of hit boxes
     */
    public final ObservableList<HitBox> hitBoxesProperty() {
        return FXCollections.unmodifiableObservableList(hitBoxes);
    }

    /**
     * Add a hit (collision) bounding box.
     *
     * @param hitBox the bounding box
     */
    public final void addHitBox(HitBox hitBox) {
        hitBoxes.add(hitBox);
    }

    /**
     * Removes a hit box with given name from the list of hit boxes for this entity.
     *
     * @param name hit box name
     */
    public final void removeHitBox(String name) {
        hitBoxes.removeIf(h -> h.getName().equals(name));
    }

    /**
     * Remove all hit boxes.
     */
    public final void clearHitBoxes() {
        hitBoxes.clear();
    }

    private ReadOnlyDoubleWrapper width = new ReadOnlyDoubleWrapper();
    private ReadOnlyDoubleWrapper height = new ReadOnlyDoubleWrapper();

    /**
     * Returns total width of the bounding box, i.e.
     * distance from the leftmost side to the rightmost side.
     *
     * @return width of the bounding box
     */
    public double getWidth() {
        return width.get();
    }

    /**
     * @return width property
     */
    public ReadOnlyDoubleProperty widthProperty() {
        return width.getReadOnlyProperty();
    }

    /**
     * Returns total height of the bounding box, i.e.
     * distance from the topmost side to the bottommost side.
     *
     * @return height of the bounding box
     */
    public double getHeight() {
        return height.get();
    }

    /**
     * @return height property
     */
    public ReadOnlyDoubleProperty heightProperty() {
        return height.getReadOnlyProperty();
    }

    /**
     * Computes width of entity based on its hit boxes.
     *
     * @return width
     */
    private double computeWidth() {
        return hitBoxes.stream()
                .mapToDouble(h -> h.getBounds().getMaxX() - getMinXLocal())
                .max()
                .orElse(0);
    }

    /**
     * Computes height of entity based on its hit boxes.
     *
     * @return height
     */
    private double computeHeight() {
        return hitBoxes.stream()
                .mapToDouble(h -> h.getBounds().getMaxY() - getMinYLocal())
                .max()
                .orElse(0);
    }

    private ReadOnlyDoubleWrapper minXLocal = new ReadOnlyDoubleWrapper();
    private ReadOnlyDoubleWrapper minYLocal = new ReadOnlyDoubleWrapper();

    /**
     * @return min X poperty in local coordinates
     */
    public ReadOnlyDoubleProperty minXLocalProperty() {
        return minXLocal.getReadOnlyProperty();
    }

    /**
     * @return min x of bbox in local coordinate system
     */
    public double getMinXLocal() {
        return minXLocal.get();
    }

    /**
     * @return min Y poperty in local coordinates
     */
    public ReadOnlyDoubleProperty minYLocalProperty() {
        return minYLocal.getReadOnlyProperty();
    }

    /**
     * @return min y of bbox in local coordinate system
     */
    public double getMinYLocal() {
        return minYLocal.get();
    }

    /**
     * Note: same as width, unless specified otherwise.
     *
     * @return max x of bbox in local coordinates
     */
    public double getMaxXLocal() {
        return getWidth();
    }

    /**
     * Note: same as height, unless specified otherwise.
     *
     * @return max y of bbox in local coordinates
     */
    public double getMaxYLocal() {
        return getHeight();
    }

    /**
     * @return min x in world coordinate system
     */
    public double getMinXWorld() {
        return getPositionX() + getMinXLocal();
    }

    /**
     * @return min y in world coordinate system
     */
    public double getMinYWorld() {
        return getPositionY() + getMinYLocal();
    }

    /**
     * @return max x in world coordinates
     */
    public double getMaxXWorld() {
        return getPositionX() + getMinXLocal() + getWidth();
    }

    /**
     * @return max y in world coordinates
     */
    public double getMaxYWorld() {
        return getPositionY() + getMinYLocal() + getHeight();
    }

    /**
     * @return center point in local coordinates
     */
    public Point2D getCenterLocal() {
        return new Point2D(getWidth() / 2, getHeight() / 2);
    }

    /**
     * If entity doesn't have {@link PositionComponent} then the center point
     * is the same as local center.
     *
     * @return center point in world coordinates
     */
    public Point2D getCenterWorld() {
        return getCenterLocal().add(getMinXWorld(), getMinYWorld());
    }

    private double computeMinXLocal() {
        return hitBoxes.stream()
                .mapToDouble(HitBox::getMinX)
                .min()
                .orElse(0);
    }

    private double computeMinYLocal() {
        return hitBoxes.stream()
                .mapToDouble(HitBox::getMinY)
                .min()
                .orElse(0);
    }

    private double getPositionX() {
        return position.getX();
    }

    private double getPositionY() {
        return position.getY();
    }

    // TODO: refactor
    private boolean isXFlipped() {
        return false;
    }

    /**
     * Internal GC-friendly (and has less checks than JavaFX's BoundingBox)
     * check for collision between two hit boxes with given x,y of entities.
     *
     * @param box1 hit box 1
     * @param x1 x of entity 1
     * @param y1 y of entity 1
     * @param box2 hit box 2
     * @param x2 x of entity 2
     * @param y2 y of entity 2
     * @return true iff box1 is colliding with box2
     */
    private boolean checkCollision(HitBox box1, double x1, double y1, HitBox box2, double x2, double y2) {
        double minX1 = x1 + box1.getMinX();
        double minY1 = y1 + box1.getMinY();
        double maxX1 = minX1 + box1.getWidth();
        double maxY1 = minY1 + box1.getHeight();

        double minX2 = x2 + box2.getMinX();
        double minY2 = y2 + box2.getMinY();
        double maxX2 = minX2 + box2.getWidth();
        double maxY2 = minY2 + box2.getHeight();

        return maxX2 >= minX1 &&
                maxY2 >= minY1 &&
                minX2 <= maxX1 &&
                minY2 <= maxY1;
    }

    /**
     * Checks for collision with another entity. Returns collision result
     * containing the first hit box that triggered collision.
     * If no collision - {@link CollisionResult#NO_COLLISION} will be returned.
     * If there is collision, the CollisionResult object must be put into pooler
     * after using the data.
     *
     * @param other entity to check collision against
     * @return collision result
     */
    public final CollisionResult checkCollision(BoundingBoxComponent other) {
        for (int i = 0; i < hitBoxes.size(); i++) {
            HitBox box1 = hitBoxes.get(i);

            for (int j = 0; j < other.hitBoxes.size(); j++) {
                HitBox box2 = other.hitBoxes.get(j);

                if (checkCollision(box1, getPositionX(), getPositionY(),
                        box2, other.getPositionX(), other.getPositionY())) {

                    CollisionResult result = pooler.get(CollisionResult.class);
                    result.init(box1, box2);

                    return result;
                }
            }
        }

        return CollisionResult.NO_COLLISION;
    }

    /**
     * GC-friendly (no object allocations) version of {@link #checkCollision(BoundingBoxComponent)}.
     *
     * @param other bbox of other entity
     * @return {@link CollisionResult#NO_COLLISION} if no collision, else {@link CollisionResult#COLLISION}
     */
    private CollisionResult checkCollisionInternal(BoundingBoxComponent other) {
        for (int i = 0; i < hitBoxes.size(); i++) {
            HitBox box1 = hitBoxes.get(i);

            for (int j = 0; j < other.hitBoxes.size(); j++) {
                HitBox box2 = other.hitBoxes.get(j);

                if (checkCollision(box1, getPositionX(), getPositionY(),
                        box2, other.getPositionX(), other.getPositionY())) {

                    return CollisionResult.COLLISION;
                }
            }
        }

        return CollisionResult.NO_COLLISION;
    }

    /**
     * @param other the other entity
     * @return true iff this entity is colliding with other based on
     * their hit boxes, in current frame
     */
    public final boolean isCollidingWith(BoundingBoxComponent other) {
        return checkCollisionInternal(other) != CollisionResult.NO_COLLISION;
    }

    /**
     * @param bounds a rectangular box that represents bounds
     * @return true iff entity is partially or entirely within given bounds
     */
    public final boolean isWithin(Rectangle2D bounds) {
        return isWithin(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
    }

    /**
     * @param minX min x
     * @param minY min y
     * @param maxX max x
     * @param maxY max y
     * @return true iff entity is partially or entirely within given bounds
     */
    public final boolean isWithin(double minX, double minY, double maxX, double maxY) {
        return !isOutside(minX, minY, maxX, maxY);
    }

    /**
     * @param bounds a rectangular box that represents bounds
     * @return true iff entity is completely outside given bounds
     */
    public final boolean isOutside(Rectangle2D bounds) {
        return isOutside(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
    }

    /**
     * @param minX min x
     * @param minY min y
     * @param maxX max x
     * @param maxY max y
     * @return true iff entity is completely outside given bounds
     */
    public final boolean isOutside(double minX, double minY, double maxX, double maxY) {
        return getPositionX() + getMinXLocal() + getWidth() < minX || getPositionX() + getMinXLocal() > maxX
                || getPositionY() + getMinYLocal() + getHeight() < minY || getPositionY() + getMinYLocal() > maxY;
    }

    /**
     * Forms a rectangle around the entity by extending min and max bounds
     * with width in X and with height in Y directions.
     *
     * @param width width to extend by in each direction
     * @param height height to extend by in each direction
     * @return rectangular area
     */
    public final Rectangle2D range(double width, double height) {
        double minX = getPositionX() - width;
        double minY = getPositionY() - height;
        double maxX = getMaxXWorld() + width;
        double maxY = getMaxYWorld() + height;

        return new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public void write(@NotNull Bundle bundle) {
        bundle.put("hitBoxes", new ArrayList<>(hitBoxes));
    }

    @Override
    public void read(@NotNull Bundle bundle) {
        hitBoxes.addAll(bundle.<ArrayList<HitBox>>get("hitBoxes"));
    }

    @Override
    public BoundingBoxComponent copy() {
        // hit boxes are immutable so can safely reuse them
        return new BoundingBoxComponent(hitBoxes.toArray(new HitBox[0]));
    }

    //
//    private BooleanProperty xFlipped = new SimpleBooleanProperty(false);
//    private double xFlipLine = 0;
//
//    /**
//     * Line to flip around. E.g. an entity with texture 200x100 as scene view
//     * with xFlipLine = 100 will be mirrored perfectly.
//     *
//     * @return vertical line at X point to use as pivot point for flip
//     */
//    public final double getXFlipLine() {
//        return xFlipLine;
//    }
//
//    /**
//     *
//     * @return x flipped property
//     */
//    public final BooleanProperty xFlippedProperty() {
//        return xFlipped;
//    }
//
//    /**
//     *
//     * @return true iff x axis is flipped
//     */
//    public final boolean isXFlipped() {
//        return xFlippedProperty().get();
//    }
//
//    /**
//     * Flip X axis of the entity. If set to true, the scene view
//     * will be drawn from right to left. This also affects hit boxes
//     *
//     * @param b x flipped flag
//     * @defaultValue false
//     */
//    public final void setXFlipped(boolean b) {
//        xFlippedProperty().set(b);
//    }
//
//    /**
//     * Flip X axis of the entity. If set to true, the scene view
//     * will be drawn from right to left. This also affects hit boxes
//     *
//     * @param b x flipped flag
//     * @param xFlipLine x flip line (pivot line)
//     * @defaultValue false
//     */
//    public final void setXFlipped(boolean b, double xFlipLine) {
//        this.xFlipLine = xFlipLine;
//        xFlippedProperty().set(b);
//    }
}
