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

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.TypeComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EnemyBatControl extends BatControl {
    private Entity ball;

    @Override
    public void onUpdate(Entity entity, double tpf) {
        super.onUpdate(entity, tpf);

        if (ball == null) {
            for (Entity e : entity.getWorld().getEntitiesByComponent(TypeComponent.class)) {
                if (e.getComponentUnsafe(TypeComponent.class).isType(EntityType.BALL)) {
                    ball = e;
                    break;
                }
            }
        } else {
            moveAI();
        }
    }

    private void moveAI() {
        BoundingBoxComponent ballBox = ball.getComponentUnsafe(BoundingBoxComponent.class);
        BoundingBoxComponent batBox = getEntity().getComponentUnsafe(BoundingBoxComponent.class);

        boolean isBallToLeft = ballBox.getMaxXWorld() <= batBox.getMinXWorld();

        if (ballBox.getMinYWorld() < batBox.getMinYWorld()) {
            if (isBallToLeft)
                up();
            else
                down();
        } else if (ballBox.getMinYWorld() > batBox.getMinYWorld()) {
            if (isBallToLeft)
                down();
            else
                up();
        } else {
            stop();
        }
    }
}
