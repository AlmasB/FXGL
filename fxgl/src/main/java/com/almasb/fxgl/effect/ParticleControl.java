/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.effect;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.UnorderedArray;
import com.almasb.fxgl.core.pool.Pools;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.util.EmptyRunnable;

import java.util.Iterator;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
public class ParticleControl extends Control {

    private ParticleEmitter emitter;
    private Entity parent = new Entity();

    protected Array<Particle> particles = new UnorderedArray<>(256);

    private PositionComponent position;

    private Runnable onFinished = EmptyRunnable.INSTANCE;

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

        if (parent.getWorld() == null) {
            entity.getWorld().addEntity(parent);
        }

        particles.addAll(emitter.emit(position.getX(), position.getY()));

        for (Iterator<Particle> it = particles.iterator(); it.hasNext(); ) {
            Particle p = it.next();
            if (p.update(tpf)) {
                it.remove();

                parent.getView().removeNode(p.getView());
                Pools.free(p);
            } else {
                if (p.getView().getParent() == null)
                    parent.getView().addNode(p.getView());
            }
        }

        if (particles.isEmpty() && emitter.isFinished()) {
            onFinished.run();
        }
    }

    @Override
    public void onRemoved(Entity entity) {
        // TODO: back to pool?
        particles.clear();

        parent.removeFromWorld();
    }

    /**
     * @return particle emitter attached to control
     */
    public final ParticleEmitter getEmitter() {
        return emitter;
    }

    public final void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }
}
