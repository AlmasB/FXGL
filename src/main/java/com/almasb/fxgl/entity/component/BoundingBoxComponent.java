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

import com.almasb.ents.AbstractComponent;
import com.almasb.ents.Entity;
import com.almasb.fxgl.physics.CollisionResult;
import com.almasb.fxgl.physics.HitBox;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BoundingBoxComponent extends AbstractComponent {

    public BoundingBoxComponent(HitBox... boxes) {
        hitBoxes.addAll(boxes);
        width.set(computeWidth());
        height.set(computeHeight());

        hitBoxes.addListener((ListChangeListener<? super HitBox>) c -> {
            width.set(computeWidth());
            height.set(computeHeight());
        });
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

    private ReadOnlyDoubleWrapper width = new ReadOnlyDoubleWrapper();
    private ReadOnlyDoubleWrapper height = new ReadOnlyDoubleWrapper();

    public double getWidth() {
        return width.get();
    }

    public ReadOnlyDoubleProperty widthProperty() {
        return width.getReadOnlyProperty();
    }

    public double getHeight() {
        return height.get();
    }

    public ReadOnlyDoubleProperty heightProperty() {
        return height.getReadOnlyProperty();
    }

    /**
     * Computes width of entity based on its hit boxes.
     *
     * @return width
     * @implNote it computes the rightmost point in X, so
     * it will return incorrect width if hit box doesn't start from 0
     */
    private double computeWidth() {
        // we take maxX because even if entity is translated
        // maxX is relative to entity's 0 origin
        return hitBoxes.stream()
                .mapToDouble(HitBox::getMaxX)
                .max()
                .orElse(0);
    }

    /**
     * Computes height of entity based on its hit boxes.
     *
     * @return height
     * @implNote it computes the highest point in Y, so
     * it will return incorrect height if hit box doesn't start from 0
     */
    private double computeHeight() {
        // we take maxY because even if entity is translated
        // maxY is relative to entity's 0 origin
        return hitBoxes.stream()
                .mapToDouble(HitBox::getMaxY)
                .max()
                .orElse(0);
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
        Point2D position = getEntity().getComponent(PositionComponent.class)
                .map(PositionComponent::getPosition)
                .orElse(Point2D.ZERO);

        return getCenterLocal().add(position);
    }

    public double getMaxXLocal() {
        return getWidth();
    }

    public double getMaxXWorld() {
        double minX = getEntity().getComponent(PositionComponent.class)
                .map(PositionComponent::getX)
                .orElse(0.0);

        return minX + getWidth();
    }

    private double getMinX() {
        return getEntity().getComponent(PositionComponent.class)
                .map(PositionComponent::getX)
                .orElse(0.0);
    }

    private double getMinY() {
        return getEntity().getComponent(PositionComponent.class)
                .map(PositionComponent::getY)
                .orElse(0.0);
    }

    // TODO: refactor
    private boolean isXFlipped() {
        return false;
    }

    /**
     * Checks for collision with another entity. Returns collision result
     * containing the first hit box that triggered collision.
     * If no collision - {@link CollisionResult#NO_COLLISION} will be returned.
     *
     * @param other entity to check collision against
     * @return collision result
     */
    public final CollisionResult checkCollision(BoundingBoxComponent other) {
        for (HitBox box1 : hitBoxes) {
            Bounds b = isXFlipped() ? box1.translateXFlipped(getMinX(), getMinY(), getWidth()) : box1.translate(getMinX(), getMinY());
            for (HitBox box2 : other.hitBoxes) {
                Bounds b2 = other.isXFlipped()
                        ? box2.translateXFlipped(other.getMinX(), other.getMinY(), other.getWidth())
                        : box2.translate(other.getMinX(), other.getMinY());
                if (b.intersects(b2)) {
                    return new CollisionResult(box1, box2);
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
        return checkCollision(other) != CollisionResult.NO_COLLISION;
    }


    /**
     * @param bounds a rectangular box that represents bounds
     * @return true iff entity is partially or entirely within given bounds
     */
    public final boolean isWithin(Rectangle2D bounds) {
        return isWithin(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
    }

    /**
     *
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
     *
     * @param minX min x
     * @param minY min y
     * @param maxX max x
     * @param maxY max y
     * @return true iff entity is completely outside given bounds
     */
    public final boolean isOutside(double minX, double minY, double maxX, double maxY) {
        return getMinX() + getWidth() < minX || getMinX() > maxX
                || getMinY() + getHeight() < minY || getMinY() > maxY;
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
