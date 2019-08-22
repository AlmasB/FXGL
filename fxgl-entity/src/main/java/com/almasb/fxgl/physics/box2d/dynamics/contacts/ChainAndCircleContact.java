/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.contacts;

import com.almasb.fxgl.physics.box2d.collision.Manifold;
import com.almasb.fxgl.physics.box2d.collision.shapes.ChainShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.CircleShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.EdgeShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.ShapeType;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.dynamics.Fixture;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

public class ChainAndCircleContact extends Contact {

    public ChainAndCircleContact(IWorldPool argPool) {
        super(argPool);
    }

    @Override
    public void init(Fixture fA, int indexA, Fixture fB, int indexB) {
        super.init(fA, indexA, fB, indexB);
        assert m_fixtureA.getType() == ShapeType.CHAIN;
        assert m_fixtureB.getType() == ShapeType.CIRCLE;
    }

    private final EdgeShape edge = new EdgeShape();

    @Override
    public void evaluate(Manifold manifold, Transform xfA, Transform xfB) {
        ChainShape chain = (ChainShape) m_fixtureA.getShape();
        chain.getChildEdge(edge, m_indexA);
        pool.getCollision().collideEdgeAndCircle(manifold, edge, xfA,
                (CircleShape) m_fixtureB.getShape(), xfB);
    }
}
