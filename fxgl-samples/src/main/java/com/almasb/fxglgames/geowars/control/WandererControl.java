/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.Entity;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class WandererControl extends Control {

    // from CRYtek
    private static final int NOISE_TABLE_SIZE = 256;
    private static final int NOISE_MASK = 255;

    private static float[] gx = new float[NOISE_TABLE_SIZE];
    private static float[] gy = new float[NOISE_TABLE_SIZE];

    static {
        setSeedAndReinitialize();
    }

    private int screenWidth, screenHeight;

    private float angleAdjustRate = FXGLMath.random(0, 0.5f);

    private Vec2 velocity = new Vec2();
    private double directionAngle = FXGLMath.random(-1, 1) * FXGLMath.PI2 * FXGLMath.radiansToDegrees;

    private int moveSpeed;
    private int rotationSpeed = FXGLMath.random(-100, 100);

    private float tx = FXGLMath.random(1000, 10000);

    private Entity wanderer;

    public WandererControl(int moveSpeed) {
        screenWidth = (int) FXGL.getApp().getWidth();
        screenHeight = (int) FXGL.getApp().getHeight();
        this.moveSpeed = moveSpeed;
    }

    @Override
    public void onAdded(Entity entity) {
        wanderer = (Entity) entity;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        adjustAngle(tpf);
        move(tpf);
        rotate(tpf);

        tx += tpf;

        checkScreenBounds();
    }

    private void adjustAngle(double tpf) {
        if (FXGLMath.randomBoolean(angleAdjustRate)) {
            directionAngle += FXGLMath.radiansToDegrees * noise1D(tx);
        }
    }

    private void move(double tpf) {
        Vec2 directionVector = Vec2.fromAngle(directionAngle).mulLocal(moveSpeed);

        velocity.addLocal(directionVector).mulLocal((float)tpf);

        wanderer.translate(new Point2D(velocity.x, velocity.y));
    }

    private void checkScreenBounds() {
        if (wanderer.getX() < 0
                || wanderer.getY() < 0
                || wanderer.getRightX() >= screenWidth
                || wanderer.getBottomY() >= screenHeight) {

            Point2D newDirectionVector = new Point2D(screenWidth / 2, screenHeight / 2)
                    .subtract(wanderer.getCenter());

            double angle = Math.toDegrees(Math.atan(newDirectionVector.getY() / newDirectionVector.getX()));
            directionAngle = newDirectionVector.getX() > 0 ? angle : 180 + angle;
        }
    }

    private void rotate(double tpf) {
        wanderer.rotateBy(rotationSpeed * tpf);
    }

    private static void setSeedAndReinitialize() {
        // Generate the gradient lookup tables
        for (int i = 0; i < NOISE_TABLE_SIZE; i++) {
            // Ken Perlin proposes that the gradients are taken from the unit
            // circle/sphere for 2D/3D.
            // So lets generate a good pseudo-random vector and normalize it

            Vec2 v = new Vec2();
            // cry_frand is in the 0..1 range
            v.x = -0.5f + FXGLMath.random();
            v.y = -0.5f + FXGLMath.random();
            v.normalizeLocal();

            gx[i] = v.x;
            gy[i] = v.y;
        }
    }

    private float noise1D(float x) {
        // Compute what gradients to use
        int qx0 = (int)Math.floor(x);
        int qx1 = qx0 + 1;
        float tx0 = x - (float)qx0;
        float tx1 = tx0 - 1;

        // Make sure we don't come outside the lookup table
        qx0 = qx0 & NOISE_MASK;
        qx1 = qx1 & NOISE_MASK;

        // Compute the dotproduct between the vectors and the gradients
        float v0 = gx[qx0] * tx0;
        float v1 = gx[qx1] * tx1;

        // Modulate with the weight function
        float wx = (3 - 2 * tx0) * tx0 * tx0;

        return v0 - wx * (v0 - v1);
    }
}