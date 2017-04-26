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
