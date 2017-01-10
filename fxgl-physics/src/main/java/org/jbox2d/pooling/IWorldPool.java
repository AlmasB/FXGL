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
package org.jbox2d.pooling;

import com.almasb.fxgl.core.math.Vec2;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Collision;
import org.jbox2d.collision.Distance;
import org.jbox2d.collision.TimeOfImpact;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Mat33;
import org.jbox2d.common.Rotation;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * World pool interface
 * @author Daniel
 *
 */
public interface IWorldPool {

    public IDynamicStack<Contact> getPolyContactStack();

    public IDynamicStack<Contact> getCircleContactStack();

    public IDynamicStack<Contact> getPolyCircleContactStack();

    public IDynamicStack<Contact> getEdgeCircleContactStack();

    public IDynamicStack<Contact> getEdgePolyContactStack();

    public IDynamicStack<Contact> getChainCircleContactStack();

    public IDynamicStack<Contact> getChainPolyContactStack();

    public Vec2 popVec2();

    public Vec2[] popVec2(int num);

    public void pushVec2(int num);

    public Vec3 popVec3();

    public Vec3[] popVec3(int num);

    public void pushVec3(int num);

    public Mat22 popMat22();

    public Mat22[] popMat22(int num);

    public void pushMat22(int num);

    public Mat33 popMat33();

    public void pushMat33(int num);

    public AABB popAABB();

    public AABB[] popAABB(int num);

    public void pushAABB(int num);

    public Rotation popRot();

    public void pushRot(int num);

    public Collision getCollision();

    public TimeOfImpact getTimeOfImpact();

    public Distance getDistance();

    public float[] getFloatArray(int argLength);

    public int[] getIntArray(int argLength);

    public Vec2[] getVec2Array(int argLength);
}
