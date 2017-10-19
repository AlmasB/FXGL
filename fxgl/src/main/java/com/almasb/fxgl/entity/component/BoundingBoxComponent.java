/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.fxgl.core.pool.Pool;
import com.almasb.fxgl.core.pool.Pools;
import com.almasb.fxgl.entity.Component;
import com.almasb.fxgl.entity.CopyableComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.serialization.SerializableComponent;
import com.almasb.fxgl.io.serialization.Bundle;
import com.almasb.fxgl.physics.CollisionResult;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.SAT;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Component that adds bounding box information to an entity.
 * The bounding box itself comprises a collection of hit boxes.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@CoreComponent
@Required(PositionComponent.class)
public class BoundingBoxComponent extends Component
        implements SerializableComponent, CopyableComponent<BoundingBoxComponent> {

    static {
        Pools.set(CollisionResult.class, new Pool<CollisionResult>() {
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

        hitBoxes.addListener(onHitBoxChange);
    }

    private PositionComponent position;

    @Override
    public void onAdded(Entity entity) {
        minXWorld.bind(minXLocal.add(position.xProperty()));
        minYWorld.bind(minYLocal.add(position.yProperty()));

        maxXWorld.bind(minXLocal.add(position.xProperty()).add(width));
        maxYWorld.bind(minYLocal.add(position.yProperty()).add(height));

        for (int i = 0; i < hitBoxes.size(); i++) {
            hitBoxes.get(i).bindX(position.xProperty());
            hitBoxes.get(i).bindX(position.yProperty());
        }
    }

    @Override
    public void onRemoved(Entity entity) {
        hitBoxes.removeListener(onHitBoxChange);
        for (int i = 0; i < hitBoxes.size(); i++) {
            hitBoxes.get(i).unbind();
        }

        minXWorld.unbind();
        minYWorld.unbind();

        maxXWorld.unbind();
        maxYWorld.unbind();
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

    private ReadOnlyDoubleWrapper minXWorld = new ReadOnlyDoubleWrapper();
    private ReadOnlyDoubleWrapper minYWorld = new ReadOnlyDoubleWrapper();
    private ReadOnlyDoubleWrapper maxXWorld = new ReadOnlyDoubleWrapper();
    private ReadOnlyDoubleWrapper maxYWorld = new ReadOnlyDoubleWrapper();

    public ReadOnlyDoubleProperty minXWorldProperty() {
        return minXWorld.getReadOnlyProperty();
    }

    public ReadOnlyDoubleProperty minYWorldProperty() {
        return minYWorld.getReadOnlyProperty();
    }

    public ReadOnlyDoubleProperty maxXWorldProperty() {
        return maxXWorld.getReadOnlyProperty();
    }

    /**
     * @return min x in world coordinate system
     */
    public double getMinXWorld() {
        return getPositionX() + getMinXLocal();
    }

    public ReadOnlyDoubleProperty maxYWorldProperty() {
        return maxYWorld.getReadOnlyProperty();
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

    private ListChangeListener<? super HitBox> onHitBoxChange = (ListChangeListener<? super HitBox>) c -> {
        minXLocal.set(computeMinXLocal());
        minYLocal.set(computeMinYLocal());
        width.set(computeWidth());
        height.set(computeHeight());

        while (c.next()) {
            if (c.wasAdded()) {
                if (position == null)
                    continue;

                for (HitBox box : c.getAddedSubList()) {
                    box.bindX(position.xProperty());
                    box.bindY(position.yProperty());
                }

            } else if (c.wasRemoved()) {
                for (HitBox box : c.getRemoved()) {
                    box.unbind();
                }
            }
        }
    };

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
    private boolean checkCollision(HitBox box1, HitBox box2) {
        return box2.getMaxXWorld() >= box1.getMinXWorld() &&
                box2.getMaxYWorld() >= box1.getMinYWorld() &&
                box2.getMinXWorld() <= box1.getMaxXWorld() &&
                box2.getMinYWorld() <= box1.getMaxYWorld();
    }

    private boolean checkCollision(HitBox box1, HitBox box2, double angle1, double angle2) {
        return SAT.isColliding(box1, box2, angle1, angle2);
    }

    /**
     * Checks for collision with another bounding box. Returns collision result
     * containing the first hit box that triggered collision.
     * If no collision - {@link CollisionResult#NO_COLLISION} will be returned.
     * If there is collision, the CollisionResult object must be put into pooler
     * after using the data.
     *
     * @param other bounding box to check collision against
     * @return collision result
     */
    public final CollisionResult checkCollision(BoundingBoxComponent other) {
        boolean checkRotation = getEntity().hasComponent(RotationComponent.class)
                && other.getEntity().hasComponent(RotationComponent.class);

        for (int i = 0; i < hitBoxes.size(); i++) {
            HitBox box1 = hitBoxes.get(i);

            for (int j = 0; j < other.hitBoxes.size(); j++) {
                HitBox box2 = other.hitBoxes.get(j);

                boolean collision;

                if (checkRotation) {
                    double angle1 = getEntity().getComponent(RotationComponent.class).getValue();
                    double angle2 = other.getEntity().getComponent(RotationComponent.class).getValue();

                    if (angle1 == 0 && angle2 == 0) {
                        collision = checkCollision(box1, box2);
                    } else {
                        collision = checkCollision(box1, box2, angle1, angle2);
                    }
                } else {
                    collision = checkCollision(box1, box2);
                }

                if (collision) {

                    CollisionResult result = Pools.obtain(CollisionResult.class);
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
        boolean checkRotation = getEntity().hasComponent(RotationComponent.class)
                && other.getEntity().hasComponent(RotationComponent.class);

        for (int i = 0; i < hitBoxes.size(); i++) {
            HitBox box1 = hitBoxes.get(i);

            for (int j = 0; j < other.hitBoxes.size(); j++) {
                HitBox box2 = other.hitBoxes.get(j);

                boolean collision;

                if (checkRotation) {
                    double angle1 = getEntity().getComponent(RotationComponent.class).getValue();
                    double angle2 = other.getEntity().getComponent(RotationComponent.class).getValue();

                    if (angle1 == 0 && angle2 == 0) {
                        collision = checkCollision(box1, box2);
                    } else {
                        collision = checkCollision(box1, box2, angle1, angle2);
                    }
                } else {
                    collision = checkCollision(box1, box2);
                }

                if (collision) {
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
        // TODO: but we can't use same objects because of bind()
        return new BoundingBoxComponent(hitBoxes.toArray(new HitBox[0]));
    }

//    private boolean isXFlipped() {
//        return false;
//    }

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
