/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.grid;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.pool.Pools;
import com.almasb.fxgl.ecs.Entity;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Spring {
    private final PointMass end1;
    private final PointMass end2;

    private final double lengthAtRest;

    private final float stiffness;
    private final float damping;

    public Spring(PointMass end1, PointMass end2, double stiffness, double damping, boolean visible, Entity gridEntity) {
        this.end1 = end1;
        this.end2 = end2;
        this.stiffness = (float) stiffness;
        this.damping = (float) damping / 10;
        lengthAtRest = end1.getPosition().distance(end2.getPosition().x, end2.getPosition().y) * 0.95f;

        if (visible) {
            gridEntity.getControlUnsafe(GridControl.class).addControl(new LineControl(end1, end2));
        }
    }

    public void update() {
        Vec2 current = Pools.obtain(Vec2.class)
                .set(end1.getPosition())
                .subLocal(end2.getPosition());

        float currentLength = current.length();

        if (currentLength > lengthAtRest) {
            Vec2 dv = Pools.obtain(Vec2.class)
                    .set(end2.getVelocity())
                    .subLocal(end1.getVelocity())
                    .mulLocal(damping);

            Vec2 force = current.normalizeLocal()
                    .mulLocal(currentLength - lengthAtRest)
                    .mulLocal(stiffness)
                    .subLocal(dv);

            end2.applyForce(force);
            end1.applyForce(force.negateLocal());

            Pools.free(dv);
        }

        Pools.free(current);
    }
}
