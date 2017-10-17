/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxglgames.geowars.grid.Grid;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BulletControl extends Control {

    private static final Color PARTICLE_COLOR = Color.YELLOW.brighter();
    private static final Duration PARTICLE_DURATION = Duration.seconds(1.2);

    static {
        ExhaustParticleControl.colorImage(PARTICLE_COLOR);
    }

    private BoundingBoxComponent bbox;

    private Point2D velocity;
    private Grid grid;

    public BulletControl(Grid grid) {
        this.grid = grid;
    }

    @Override
    public void onAdded(Entity entity) {
        velocity = entity.getControl(ProjectileControl.class).getVelocity();
        bbox = entity.getComponent(BoundingBoxComponent.class);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        grid.applyExplosiveForce(velocity.magnitude() / 60 * 18, bbox.getCenterWorld(), 80 * 60 * tpf);

        if (bbox.getMinXWorld() < 0) {
            spawnParticles(0, bbox.getCenterWorld().getY(), 1, FXGLMath.random(-1.0f, 1.0f));

        } else if (bbox.getMaxXWorld() > FXGL.getApp().getWidth()) {
            spawnParticles(FXGL.getApp().getWidth(), bbox.getCenterWorld().getY(), -1, FXGLMath.random(-1.0f, 1.0f));

        } else if (bbox.getMinYWorld() < 0) {
            spawnParticles(bbox.getCenterWorld().getX(), 0, FXGLMath.random(-1.0f, 1.0f), 1);

        } else if (bbox.getMaxYWorld() > FXGL.getApp().getHeight()) {
            spawnParticles(bbox.getCenterWorld().getX(), FXGL.getApp().getHeight(), FXGLMath.random(-1.0f, 1.0f), -1);
        }
    }

    private void spawnParticles(double x, double y, double dirX, double dirY) {
        Entities.builder()
                .at(x, y)
                .viewFromNode(new Texture(ExhaustParticleControl.coloredImages.get(PARTICLE_COLOR)))
                .with(new ProjectileControl(new Point2D(dirX, dirY), FXGLMath.random(150, 280)),
                        new ExpireCleanControl(PARTICLE_DURATION),
                        new ParticleControl())
                .buildAndAttach(FXGL.getApp().getGameWorld());
    }
}
