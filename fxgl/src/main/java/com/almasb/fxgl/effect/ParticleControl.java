/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.effect;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.entity.component.PositionComponent;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.util.Iterator;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
public class ParticleControl extends AbstractControl {

    private ParticleEmitter emitter;

    protected Array<Particle> particles = new Array<>(false, 256);

    private PositionComponent position;

    /**
     * Constructs particle control with specified emitter.
     *
     * @param emitter particle emitter
     */
    public ParticleControl(ParticleEmitter emitter) {
        this.emitter = emitter;
    }

    protected ParticleControl() {}

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (emitter == null)
            return;

        particles.addAll(emitter.emit(position.getX(), position.getY()));

        for (Iterator<Particle> it = particles.iterator(); it.hasNext(); ) {
            Particle p = it.next();
            if (p.update(tpf)) {
                it.remove();
                FXGL.getPooler().put(p);
            }
        }
    }

    @Override
    public void onRemoved(Entity entity) {
        particles.clear();
    }

    /**
     * Do NOT call manually.
     *
     * @param g graphics context
     * @param viewportOrigin viewport origin
     */
    public void renderParticles(GraphicsContext g, Point2D viewportOrigin) {
        for (Particle p : particles) {
            p.render(g, viewportOrigin);
        }
    }

    /**
     * @return particle emitter attached to control
     */
    public ParticleEmitter getEmitter() {
        return emitter;
    }
}
