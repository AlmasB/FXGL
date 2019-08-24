/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.pooling;

import com.almasb.fxgl.core.math.Vec2;
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

    IDynamicStack<Contact> getPolyContactStack();

    IDynamicStack<Contact> getCircleContactStack();

    IDynamicStack<Contact> getPolyCircleContactStack();

    IDynamicStack<Contact> getEdgeCircleContactStack();

    IDynamicStack<Contact> getEdgePolyContactStack();

    IDynamicStack<Contact> getChainCircleContactStack();

    IDynamicStack<Contact> getChainPolyContactStack();

    Vec2 popVec2();

    Vec2[] popVec2(int num);

    void pushVec2(int num);

    Vec3 popVec3();

    Vec3[] popVec3(int num);

    void pushVec3(int num);

    Mat22 popMat22();

    Mat22[] popMat22(int num);

    void pushMat22(int num);

    Mat33 popMat33();

    void pushMat33(int num);

    Rotation popRot();

    void pushRot(int num);

    Collision getCollision();

    TimeOfImpact getTimeOfImpact();

    Distance getDistance();

    Vec2[] getVec2Array(int argLength);
}
