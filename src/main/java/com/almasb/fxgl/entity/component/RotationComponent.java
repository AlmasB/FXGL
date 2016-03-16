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

import com.almasb.ents.component.DoubleComponent;
import javafx.geometry.Point2D;

/**
 * Adds rotation data to an entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RotationComponent extends DoubleComponent {

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

    @Override
    public String toString() {
        return "Rotation(" + getValue() + "deg)";
    }
}
