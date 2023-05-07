/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.contacts;

import com.almasb.fxgl.physics.box2d.collision.Manifold;
import com.almasb.fxgl.physics.box2d.collision.shapes.ChainShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.EdgeShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.PolygonShape;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

public class ChainAndPolygonContact extends Contact {

    public ChainAndPolygonContact(IWorldPool argPool) {
        super(argPool);
    }

    private final EdgeShape edge = new EdgeShape();

    @Override
    public void evaluate(Manifold manifold, Transform xfA, Transform xfB) {
        ChainShape chain = (ChainShape) m_fixtureA.getShape();
        chain.getChildEdge(edge, m_indexA);
        pool.getCollision().collideEdgeAndPolygon(manifold, edge, xfA,
                (PolygonShape) m_fixtureB.getShape(), xfB);
    }
}
