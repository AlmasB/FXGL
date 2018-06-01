/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.collision.AABB;
import com.almasb.fxgl.physics.box2d.collision.RayCastInput;
import com.almasb.fxgl.physics.box2d.collision.RayCastOutput;
import com.almasb.fxgl.physics.box2d.collision.broadphase.BroadPhase;
import com.almasb.fxgl.physics.box2d.collision.shapes.MassData;
import com.almasb.fxgl.physics.box2d.collision.shapes.Shape;
import com.almasb.fxgl.physics.box2d.collision.shapes.ShapeType;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.Contact;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.ContactEdge;

/**
 * A fixture is used to attach a shape to a body for collision detection. A fixture inherits its
 * transform from its parent. Fixtures hold additional non-geometric data such as friction,
 * collision filters, etc. Fixtures are created via Body::CreateFixture.
 * Note: you cannot reuse fixtures.
 *
 * @author daniel
 */
public final class Fixture {

    private final FixtureDef state;

    private final Body body;

    private FixtureProxy[] proxies;
    private int proxyCount = 0;

    Fixture(Body body, FixtureDef def) {
        this.body = body;

        state = def.copy();

        // Reserve proxy space
        int childCount = state.getShape().getChildCount();

        proxies = new FixtureProxy[childCount];
        for (int i = 0; i < childCount; i++) {
            proxies[i] = new FixtureProxy();
        }
    }

    // These support body activation/deactivation.
    void createProxies(BroadPhase broadPhase, final Transform xf) {
        // Create proxies in the broad-phase.
        proxyCount = state.getShape().getChildCount();

        for (int i = 0; i < proxyCount; ++i) {
            FixtureProxy proxy = proxies[i];
            state.getShape().computeAABB(proxy.aabb, xf, i);
            proxy.proxyId = broadPhase.createProxy(proxy.aabb, proxy);
            proxy.fixture = this;
            proxy.childIndex = i;
        }
    }

    void destroyProxies(BroadPhase broadPhase) {
        // Destroy proxies in the broad-phase.
        for (int i = 0; i < proxyCount; ++i) {
            FixtureProxy proxy = proxies[i];
            broadPhase.destroyProxy(proxy.proxyId);
            proxy.proxyId = BroadPhase.NULL_PROXY;
        }

        proxyCount = 0;
    }

    private final AABB pool1 = new AABB();
    private final AABB pool2 = new AABB();
    private final Vec2 displacement = new Vec2();

    void synchronize(BroadPhase broadPhase, Transform transform1, Transform transform2) {
        if (proxyCount == 0) {
            return;
        }

        for (int i = 0; i < proxyCount; ++i) {
            FixtureProxy proxy = proxies[i];

            // Compute an AABB that covers the swept shape (may miss some rotation effect).
            AABB aabb1 = pool1;
            AABB aabb2 = pool2;
            state.getShape().computeAABB(aabb1, transform1, proxy.childIndex);
            state.getShape().computeAABB(aabb2, transform2, proxy.childIndex);

            proxy.aabb.combine(aabb1, aabb2);

            displacement.x = transform2.p.x - transform1.p.x;
            displacement.y = transform2.p.y - transform1.p.y;

            broadPhase.moveProxy(proxy.proxyId, proxy.aabb, displacement);
        }
    }

    // The proxies must be destroyed before calling this.
    void destroy() {
        proxies = null;
    }

    /**
     * @return the parent body of this fixture
     */
    public Body getBody() {
        return body;
    }

    /**
     * You can modify the child shape, however you should not change the number
     * of vertices because this will crash some collision caching mechanisms.
     *
     * @return child shape
     */
    public Shape getShape() {
        return state.getShape();
    }

    /**
     * @return the type of the child shape. You can use this to down cast to the concrete shape.
     */
    public ShapeType getType() {
        return state.getShape().getType();
    }

    /**
     * @return the contact filtering data
     */
    public Filter getFilterData() {
        return state.getFilter();
    }

    /**
     * Set the contact filtering data.
     * This will not update contacts until the next time step when either parent body is awake.
     * This automatically calls refilter.
     * This is an expensive operation and should not be called frequently.
     *
     * @param filter filter
     */
    public void setFilterData(Filter filter) {
        state.getFilter().set(filter);

        refilter();
    }

    /**
     * The same as in the fixture definition, unless explicitly changed.
     * Use this to store your application specific data.
     *
     * @return user data
     */
    public Object getUserData() {
        return state.getUserData();
    }

    /**
     * Set the user data.
     * Use this to store your application specific data.
     *
     * @param data user data
     */
    public void setUserData(Object data) {
        state.setUserData(data);
    }

    public float getDensity() {
        return state.getDensity();
    }

    public void setDensity(float density) {
        state.setDensity(density);
    }

    public float getFriction() {
        return state.getFriction();
    }

    /**
     * This will <b>NOT</b> change the friction of existing contacts.
     */
    public void setFriction(float friction) {
        state.setFriction(friction);
    }

    public float getRestitution() {
        return state.getRestitution();
    }

    /**
     * This will <b>NOT</b> change the restitution of existing contacts.
     */
    public void setRestitution(float restitution) {
        state.setRestitution(restitution);
    }

    /**
     * @return true if the fixture / shape is a sensor (non-solid)
     */
    public boolean isSensor() {
        return state.isSensor();
    }

    /**
     * Set if this fixture is a sensor.
     */
    public void setSensor(boolean sensor) {
        if (sensor != isSensor()) {
            body.setAwake(true);
            state.setSensor(sensor);
        }
    }

    /**
     * Call this if you want to establish collision that was previously disabled by
     * ContactFilter::ShouldCollide.
     */
    public void refilter() {
        // Flag associated contacts for filtering.

        for (ContactEdge edge : body.getContactEdges()) {
            Contact contact = edge.contact;

            if (contact.getFixtureA() == this || contact.getFixtureB() == this) {
                contact.flagForFiltering();
            }
        }

        World world = body.getWorld();

        if (world == null) {
            return;
        }

        touchProxies();
    }

    void touchProxies() {
        // Touch each proxy so that new pairs may be created
        BroadPhase broadPhase = body.getWorld().getContactManager().broadPhase;
        for (int i = 0; i < proxyCount; ++i) {
            broadPhase.touchProxy(proxies[i].proxyId);
        }
    }

    boolean testProxyOverlap(Fixture fixtureB, int indexA, int indexB) {
        BroadPhase broadPhase = body.getWorld().getContactManager().broadPhase;

        int proxyIdA = this.getProxyId(indexA);
        int proxyIdB = fixtureB.getProxyId(indexB);

        return broadPhase.testOverlap(proxyIdA, proxyIdB);
    }

    private int getProxyId(int index) {
        return proxies[index].proxyId;
    }

    /**
     * Test a point for containment in this fixture.
     * This only works for convex shapes.
     *
     * @param p a point in world coordinates
     */
    public boolean containsPoint(Vec2 p) {
        return state.getShape().testPoint(body.m_xf, p);
    }

    /**
     * Cast a ray against this shape.
     *
     * @param output the ray-cast results
     * @param input the ray-cast input parameters
     */
    public boolean raycast(RayCastOutput output, RayCastInput input, int childIndex) {
        return state.getShape().raycast(output, input, body.m_xf, childIndex);
    }

    /**
     * Get the mass data for this fixture.
     * The mass data is based on the density and the shape.
     * The rotational inertia is about the shape's origin.
     */
    public void getMassData(MassData massData) {
        state.getShape().computeMass(massData, state.getDensity());
    }

    /**
     * This AABB may be enlarged and/or stale.
     * If you need a more accurate AABB, compute it using the shape and the body transform.
     *
     * @return the fixture's AABB
     */
    public AABB getAABB(int childIndex) {
        return proxies[childIndex].aabb;
    }

    /**
     * Compute the distance from this fixture.
     *
     * @param p a point in world coordinates.
     * @return distance
     */
    public float computeDistance(Vec2 p, int childIndex, Vec2 normalOut) {
        return state.getShape().computeDistanceToOut(body.getTransform(), p, childIndex, normalOut);
    }

    /**
     * This proxy is used internally to connect fixtures to the broad-phase.
     */
    static class FixtureProxy {
        final AABB aabb = new AABB();
        Fixture fixture = null;
        int proxyId = BroadPhase.NULL_PROXY;
        int childIndex;

        // only we can create these
        private FixtureProxy() { }
    }
}
