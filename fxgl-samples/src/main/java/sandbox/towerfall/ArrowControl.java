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

package sandbox.towerfall;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ArrowControl extends AbstractControl {

    private Point2D velocity;

    public ArrowControl(Point2D direction) {
        this.velocity = direction.normalize().multiply(550);
    }

    /**
     * @return velocity
     */
    public Point2D getVelocity() {
        return velocity;
    }

    /**
     * @return direction vector (normalized)
     */
    public Point2D getDirection() {
        return velocity.normalize();
    }

    /**
     * @return current speed
     */
    public double getSpeed() {
        return velocity.magnitude();
    }

    @Override
    public void onAdded(Entity entity) {}

    @Override
    public void onUpdate(Entity entity, double tpf) {
        //velocity = velocity.multiply()

        velocity = velocity.add(0, 4.3);

        Entities.getRotation(entity).rotateToVector(velocity);
        Entities.getPosition(entity).translate(velocity.multiply(tpf));
    }
}
