/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.entity;

import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.component.*;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;

/**
 * Entity that guarantees to have Type, Position, Rotation, BoundingBox and View
 * components.
 * Provides methods to conveniently access commonly used features.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class GameEntity extends Entity {
    private TypeComponent type;
    private PositionComponent position;
    private RotationComponent rotation;
    private BoundingBoxComponent bbox;
    private MainViewComponent view;

    public GameEntity() {
        type = new TypeComponent();
        position = new PositionComponent();
        rotation = new RotationComponent();
        bbox = new BoundingBoxComponent();
        view = new MainViewComponent();

        addComponent(type);
        addComponent(position);
        addComponent(rotation);
        addComponent(bbox);
        addComponent(view);
    }

    /**
     * @return type component
     */
    public final TypeComponent getTypeComponent() {
        return type;
    }

    /**
     * @return position component
     */
    public final PositionComponent getPositionComponent() {
        return position;
    }

    /**
     * @return rotation component
     */
    public final RotationComponent getRotationComponent() {
        return rotation;
    }

    /**
     * @return bounding box component
     */
    public final BoundingBoxComponent getBoundingBoxComponent() {
        return bbox;
    }

    /**
     * @return view component
     */
    public final MainViewComponent getMainViewComponent() {
        return view;
    }

    // TYPE BEGIN

    /**
     * @return entity type
     */
    public final Object getType() {
        return type.getValue();
    }

    /**
     * <pre>
     *     Example:
     *     entity.isType(Type.PLAYER);
     * </pre>
     *
     * @param type entity type
     * @return true iff this type component is of given type
     */
    public final boolean isType(Object type) {
        return this.type.isType(type);
    }

    // TYPE END

    // POSITION BEGIN

    /**
     * @return top left point of this entity in world coordinates
     */
    public final Point2D getPosition() {
        return position.getValue();
    }

    /**
     * Set top left position of this entity in world coordinates.
     *
     * @param position point
     */
    public final void setPosition(Point2D position) {
        this.position.setValue(position);
    }

    /**
     * @return top left x
     */
    public final double getX() {
        return position.getX();
    }

    /**
     * @return top left y
     */
    public final double getY() {
        return position.getY();
    }

    /**
     * Set position x of this entity.
     *
     * @param x x coordinate
     */
    public final void setX(double x) {
        position.setX(x);
    }

    /**
     * Set position y of this entity.
     *
     * @param y y coordinate
     */
    public final void setY(double y) {
        position.setY(y);
    }

    /**
     * Translate x and y by given vector.
     *
     * @param vector translate vector
     */
    public final void translate(Point2D vector) {
        position.translate(vector);
    }

    /**
     * Translate X by given value.
     *
     * @param dx dx
     */
    public final void translateX(double dx) {
        position.translateX(dx);
    }

    /**
     * Translate Y by given value.
     *
     * @param dy dy
     */
    public final void translateY(double dy) {
        position.translateY(dy);
    }

    /**
     * @param other the other component
     * @return distance in pixels from this position to the other
     */
    public final double distance(GameEntity other) {
        return position.distance(other.position);
    }

    // POSITION END

    // ROTATION BEGIN

    /**
     * @return rotation angle
     */
    public final double getRotation() {
        return rotation.getValue();
    }

    /**
     * Set absolute rotation angle.
     *
     * @param angle rotation angle
     */
    public final void setRotation(double angle) {
        rotation.setValue(angle);
    }

    /**
     * Rotate entity view by given angle clockwise.
     * To rotate counter clockwise use a negative angle value.
     *
     * Note: this doesn't affect hit boxes. For more accurate
     * collisions use {@link com.almasb.fxgl.physics.PhysicsComponent}.
     *
     * @param angle rotation angle in degrees
     */
    public final void rotateBy(double angle) {
        rotation.rotateBy(angle);
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
    public final void rotateToVector(Point2D vector) {
        rotation.rotateToVector(vector);
    }

    // ROTATION END

    // BBOX BEGIN

    /**
     * @return width of this entity based on bounding box
     */
    public final double getWidth() {
        return bbox.getWidth();
    }

    /**
     * @return height of this entity based on bounding box
     */
    public final double getHeight() {
        return bbox.getHeight();
    }

    /**
     * @return the righmost x of this entity in world coordinates
     */
    public final double getRightX() {
        return bbox.getMaxXWorld();
    }

    /**
     * @return the bottom y of this entity in world coordinates
     */
    public final double getBottomY() {
        return bbox.getMaxYWorld();
    }

    /**
     * @return center point of this entity in world coordinates
     */
    public final Point2D getCenter() {
        return bbox.getCenterWorld();
    }

    /**
     * @param other the other game entity
     * @return true iff bbox of this entity is colliding with bbox of other
     */
    public final boolean isColliding(GameEntity other) {
        return bbox.isCollidingWith(other.bbox);
    }

    /**
     * @param bounds a rectangular box that represents bounds
     * @return true iff entity is partially or entirely within given bounds
     */
    public final boolean isWithin(Rectangle2D bounds) {
        return bbox.isWithin(bounds);
    }

    // BBOX END

    // VIEW BEGIN

    /**
     * @return entity view
     */
    public final EntityView getView() {
        return this.view.getView();
    }

    /**
     * Set view without generating bounding boxes from view.
     *
     * @param view the view
     */
    public final void setView(Node view) {
        this.view.setView(view);
    }

    /**
     * Set view from texture.
     *
     * @param textureName name of texture
     */
    public final void setViewFromTexture(String textureName) {
        this.view.setTexture(textureName);
    }

    /**
     * Set view from texture and generate bbox from it.
     *
     * @param textureName name of texture
     */
    public final void setViewFromTextureWithBBox(String textureName) {
        this.view.setTexture(textureName, true);
    }

    /**
     * Set view and generate bounding boxes from view.
     *
     * @param view the view
     */
    public final void setViewWithBBox(Node view) {
        this.view.setView(view, true);
    }

    /**
     * @return render layer
     */
    public final RenderLayer getRenderLayer() {
        return this.view.getRenderLayer();
    }

    /**
     * Set render layer.
     *
     * @param layer render layer
     */
    public final void setRenderLayer(RenderLayer layer) {
        this.view.setRenderLayer(layer);
    }

    // VIEW END

    @Override
    public String toString() {
        return "GameEntity(" + type + "," + position + "," + rotation  + ")";
    }
}
