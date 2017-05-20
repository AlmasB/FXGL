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
package com.almasb.fxgl.physics.box2d.dynamics.contacts;

import com.almasb.fxgl.physics.box2d.collision.Manifold;
import com.almasb.fxgl.physics.box2d.collision.shapes.ChainShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.EdgeShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.PolygonShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.ShapeType;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.dynamics.Fixture;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

public class ChainAndPolygonContact extends Contact {

    public ChainAndPolygonContact(IWorldPool argPool) {
        super(argPool);
    }

    @Override
    public void init(Fixture fA, int indexA, Fixture fB, int indexB) {
        super.init(fA, indexA, fB, indexB);
        assert (m_fixtureA.getType() == ShapeType.CHAIN);
        assert (m_fixtureB.getType() == ShapeType.POLYGON);
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
