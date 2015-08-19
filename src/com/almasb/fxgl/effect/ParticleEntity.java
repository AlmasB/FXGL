/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.effect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * Particles are attached to ParticleEntity so they can be part of the
 * scenegraph. The actual render of particles happens on Canvas.
 *
 * Translation of this entity will also affect the position of newly
 * spawned particles.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public final class ParticleEntity extends Entity {

    /**
     * Particles attached to entity
     */
    private List<Particle> particles = new ArrayList<>();

    /**
     * Particle emitter used to create new particles
     */
    private ParticleEmitter emitter;

    public ParticleEntity(EntityType type) {
        super(type);
    }

    /**
     * Set particle emitter to this entity.
     * The emitter determines the particle behavior
     *
     * @param emitter
     */
    public void setEmitter(ParticleEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    protected void onUpdate(long now) {
        if (emitter == null)
            return;

        particles.addAll(emitter.emit(getTranslateX(), getTranslateY()));

        for (Iterator<Particle> it = particles.iterator(); it.hasNext(); ) {
            Particle p = it.next();
            if (p.update())
                it.remove();
        }
    }

    @Override
    protected void onClean() {
        particles.clear();
    }

    /**
     * Do NOT call manually.
     *
     * @param g
     * @param viewportOrigin
     */
    public void renderParticles(GraphicsContext g, Point2D viewportOrigin) {
        particles.forEach(p -> p.render(g, viewportOrigin));
    }
}
