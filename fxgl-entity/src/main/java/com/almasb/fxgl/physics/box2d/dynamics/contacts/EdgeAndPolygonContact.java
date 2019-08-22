/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.contacts;

import com.almasb.fxgl.physics.box2d.collision.Manifold;
import com.almasb.fxgl.physics.box2d.collision.shapes.EdgeShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.PolygonShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.ShapeType;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.dynamics.Fixture;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

public class EdgeAndPolygonContact extends Contact {

    public EdgeAndPolygonContact(IWorldPool argPool) {
        super(argPool);
    }

    @Override
    public void init(Fixture fA, int indexA, Fixture fB, int indexB) {
        super.init(fA, indexA, fB, indexB);
        assert m_fixtureA.getType() == ShapeType.EDGE;
        assert m_fixtureB.getType() == ShapeType.POLYGON;
    }

    @Override
    public void evaluate(Manifold manifold, Transform xfA, Transform xfB) {
        pool.getCollision().collideEdgeAndPolygon(manifold, (EdgeShape) m_fixtureA.getShape(), xfA,
                (PolygonShape) m_fixtureB.getShape(), xfB);
    }
}
