/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics.contacts

import com.almasb.fxgl.physics.box2d.collision.Manifold
import com.almasb.fxgl.physics.box2d.collision.shapes.ChainShape
import com.almasb.fxgl.physics.box2d.collision.shapes.CircleShape
import com.almasb.fxgl.physics.box2d.collision.shapes.EdgeShape
import com.almasb.fxgl.physics.box2d.collision.shapes.PolygonShape
import com.almasb.fxgl.physics.box2d.common.Transform
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

class ChainAndCircleContact(pool: IWorldPool) : Contact(pool) {

    private val edge = EdgeShape()

    override fun evaluate(manifold: Manifold, xfA: Transform, xfB: Transform) {
        val chain = m_fixtureA.shape as ChainShape
        chain.getChildEdge(edge, m_indexA)
        pool.collision.collideEdgeAndCircle(manifold, edge, xfA, m_fixtureB.shape as CircleShape, xfB)
    }
}

class ChainAndPolygonContact(pool: IWorldPool) : Contact(pool) {

    private val edge = EdgeShape()

    override fun evaluate(manifold: Manifold, xfA: Transform, xfB: Transform) {
        val chain = m_fixtureA.shape as ChainShape
        chain.getChildEdge(edge, m_indexA)
        pool.collision.collideEdgeAndPolygon(manifold, edge, xfA, m_fixtureB.shape as PolygonShape, xfB)
    }
}

class CircleContact(pool: IWorldPool) : Contact(pool) {

    override fun evaluate(manifold: Manifold, xfA: Transform, xfB: Transform) {
        pool.collision.collideCircles(manifold, m_fixtureA.shape as CircleShape, xfA, m_fixtureB.shape as CircleShape, xfB)
    }
}

class EdgeAndCircleContact(pool: IWorldPool) : Contact(pool) {

    override fun evaluate(manifold: Manifold, xfA: Transform, xfB: Transform) {
        pool.collision.collideEdgeAndCircle(manifold, m_fixtureA.shape as EdgeShape, xfA, m_fixtureB.shape as CircleShape, xfB)
    }
}

class EdgeAndPolygonContact(pool: IWorldPool) : Contact(pool) {

    override fun evaluate(manifold: Manifold, xfA: Transform, xfB: Transform) {
        pool.collision.collideEdgeAndPolygon(manifold, m_fixtureA.shape as EdgeShape, xfA, m_fixtureB.shape as PolygonShape, xfB)
    }
}

class PolygonAndCircleContact(pool: IWorldPool) : Contact(pool) {

    override fun evaluate(manifold: Manifold, xfA: Transform, xfB: Transform) {
        pool.collision.collidePolygonAndCircle(manifold, m_fixtureA.shape as PolygonShape, xfA, m_fixtureB.shape as CircleShape, xfB)
    }
}

class PolygonContact(pool: IWorldPool) : Contact(pool) {

    override fun evaluate(manifold: Manifold, xfA: Transform, xfB: Transform) {
        pool.collision.collidePolygons(manifold, m_fixtureA.shape as PolygonShape, xfA, m_fixtureB.shape as PolygonShape, xfB)
    }
}