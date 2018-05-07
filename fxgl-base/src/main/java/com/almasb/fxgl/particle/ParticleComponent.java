/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.particle;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.collection.UnorderedArray;
import com.almasb.fxgl.core.pool.Pools;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.PositionComponent;
import com.almasb.fxgl.util.EmptyRunnable;

import java.util.Iterator;

import static com.almasb.fxgl.util.BackportKt.forEach;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ParticleComponent extends Component {

    private ParticleEmitter emitter;
    private Entity parent = new Entity();

    protected Array<Particle> particles = new UnorderedArray<>(256);

    private PositionComponent position;

    private Runnable onFinished = EmptyRunnable.INSTANCE;

    /**
     * Constructs particle component with specified emitter.
     *
     * @param emitter particle emitter
     */
    public ParticleComponent(ParticleEmitter emitter) {
        this.emitter = emitter;
    }

    protected ParticleComponent() {}

    @Override
    public void onUpdate(double tpf) {
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
    public void onRemoved() {
        forEach(particles, Pools::free);
        particles.clear();

        parent.removeFromWorld();
    }

    /**
     * @return particle emitter attached to component
     */
    public final ParticleEmitter getEmitter() {
        return emitter;
    }

    public final void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }
}
