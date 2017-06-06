/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.pooling;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.collision.AABB;
import com.almasb.fxgl.physics.box2d.collision.Collision;
import com.almasb.fxgl.physics.box2d.collision.Distance;
import com.almasb.fxgl.physics.box2d.collision.TimeOfImpact;
import com.almasb.fxgl.physics.box2d.common.Mat22;
import com.almasb.fxgl.physics.box2d.common.Mat33;
import com.almasb.fxgl.physics.box2d.common.Rotation;
import com.almasb.fxgl.physics.box2d.common.Vec3;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.Contact;

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
