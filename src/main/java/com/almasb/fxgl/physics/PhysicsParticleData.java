/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.physics;

import org.jbox2d.particle.ParticleType;

/**
 * Holds information about particle type.
 * Since a particle may be of many types, API allows chain calls.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class PhysicsParticleData {

    private boolean wall, spring, elastic, viscous, powder, tensile;
    private int flags = 0;

    /**
     * @return jbox2d flags for particle definition
     */
    int getFlags() {
        return flags;
    }

    public boolean isWall() {
        return wall;
    }

    public boolean isSpring() {
        return spring;
    }

    public boolean isElastic() {
        return elastic;
    }

    public boolean isViscous() {
        return viscous;
    }

    public boolean isPowder() {
        return powder;
    }

    public boolean isTensile() {
        return tensile;
    }

    public PhysicsParticleData setWater() {
        // this is a no-op because jbox2d by default uses water
        // but this makes the intention clearer
        return this;
    }

    public PhysicsParticleData setWall() {
        flags |= ParticleType.b2_wallParticle;
        return this;
    }

    public PhysicsParticleData setSpring() {
        flags |= ParticleType.b2_springParticle;
        return this;
    }

    public PhysicsParticleData setElastic() {
        flags |= ParticleType.b2_elasticParticle;
        return this;
    }

    public PhysicsParticleData setViscous() {
        flags |= ParticleType.b2_viscousParticle;
        return this;
    }

    public PhysicsParticleData setPowder() {
        flags |= ParticleType.b2_powderParticle;
        return this;
    }

    public PhysicsParticleData setTensile() {
        flags |= ParticleType.b2_tensileParticle;
        return this;
    }
}
