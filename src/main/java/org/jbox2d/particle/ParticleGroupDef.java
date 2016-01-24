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

/*******************************************************************************
 * Copyright (c) 2013, Daniel Murphy
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.jbox2d.particle;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;

import java.util.Set;

/**
 * A particle group definition holds all the data needed to construct a particle group. You can
 * safely re-use these definitions.
 */
public class ParticleGroupDef {

    private int typeFlags = ParticleTypeInternal.b2_waterParticle;   // 0

    /**
     * @return the particle-behavior flags from {@link ParticleTypeInternal}
     */
    public int getTypeFlags() {
        return typeFlags;
    }

    /**
     * Set particle types. E.g. EnumSet.of(ParticleType...).
     *
     * @param types particle types
     */
    public void setTypes(Set<ParticleType> types) {
        for (ParticleType type : types) {
            typeFlags |= type.bit;
        }
    }

    private int groupFlags = 0;

    /**
     * @return the group-construction flags
     */
    public int getGroupFlags() {
        return groupFlags;
    }

    public void setGroupFlags(int groupFlags) {
        this.groupFlags = groupFlags;
    }

    private final Vec2 position = new Vec2();

    /**
     * @return the world position of the group.
     */
    public Vec2 getPosition() {
        return position;
    }

    /**
     * Moves the group's shape a distance equal to the value of
     * position.
     *
     * @param x x coord
     * @param y y coord
     */
    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    private float angle = 0;

    /**
     * @return The world angle of the group in radians. Rotates the shape by an angle equal to the value of
     * angle
     */
    public float getAngle() {
        return angle;
    }

    /**
     * The world angle of the group in radians. Rotates the shape by an angle equal to the value of
     * angle.
     *
     * @param angle in radians
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * The linear velocity of the group's origin in world co-ordinates.
     */
    private final Vec2 linearVelocity = new Vec2();

    public Vec2 getLinearVelocity() {
        return linearVelocity;
    }

    /**
     * The angular velocity of the group.
     */
    public float angularVelocity = 0;

    public float getAngularVelocity() {
        return angularVelocity;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    /**
     * The color of all particles in the group.
     */
    private ParticleColor color = null;

    public ParticleColor getColor() {
        return color;
    }

    public void setColor(ParticleColor color) {
        this.color = color;
    }

    /**
     * The strength of cohesion among the particles in a group with flag b2_elasticParticle or
     * b2_springParticle.
     */
    private float strength = 1;

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    private Shape shape = null;

    /**
     * @return shape containing the particle group
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * @param shape the shape containing the particle group
     */
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    /**
     * If true, destroy the group automatically after its last particle has been destroyed.
     */
    private boolean destroyAutomatically = true;

    public boolean isDestroyAutomatically() {
        return destroyAutomatically;
    }

    public void setDestroyAutomatically(boolean destroyAutomatically) {
        this.destroyAutomatically = destroyAutomatically;
    }

    /**
     * Use this to store application-specific group data.
     */
    private Object userData = null;

    public Object getUserData() {
        return userData;
    }
}
