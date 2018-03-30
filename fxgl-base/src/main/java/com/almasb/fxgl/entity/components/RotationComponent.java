/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components;

import com.almasb.fxgl.devtools.DeveloperEditable;
import com.almasb.fxgl.entity.component.CopyableComponent;
import com.almasb.fxgl.entity.component.CoreComponent;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;

/**
 * Adds rotation data to an entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@CoreComponent
public class RotationComponent extends DoubleComponent implements CopyableComponent<RotationComponent> {

    /**
     * Constructs rotation with angle = 0.
     */
    public RotationComponent() {
        this(0);
    }

    /**
     * Constructs rotation with given angle.
     *
     * @param angle the angle
     */
    public RotationComponent(double angle) {
        super(angle);
    }

    /**
     * Rotate entity view by given angle.
     * Note: this doesn't affect hit boxes. For more accurate
     * collisions use {@link com.almasb.fxgl.physics.PhysicsComponent}.
     *
     * @param byAngle rotation angle in degrees
     */
    public final void rotateBy(double byAngle) {
        setValue(getValue() + byAngle);
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
        double angle = Math.toDegrees(Math.atan2(vector.getY(), vector.getX()));
        setValue(angle);
    }

    @DeveloperEditable("Angle")
    public final DoubleProperty angleProperty() {
        return valueProperty();
    }

    @Override
    public String toString() {
        return "Rotation(" + getValue() + "deg)";
    }

    @Override
    public RotationComponent copy() {
        return new RotationComponent(getValue());
    }
}
