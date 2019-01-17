/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.HitBox;
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

    private final Filter filter = new Filter();

    private final Body body;
    private final Shape shape;

    private Object userData;

    private HitBox hitBox;

    private float density;
    private float friction;
    private float restitution;
    private boolean isSensor;

    private FixtureProxy[] proxies;
    private int proxyCount = 0;

    Fixture(Body body, FixtureDef def) {
        this.body = body;
        shape = def.getShape().clone();

        userData = def.getUserData();
        density = def.getDensity();
        friction = def.getFriction();
        restitution = def.getRestitution();
        isSensor = def.isSensor();

        filter.set(def.getFilter());

        // Reserve proxy space
        int childCount = shape.getChildCount();

        proxies = new FixtureProxy[childCount];
        for (int i = 0; i < childCount; i++) {
            proxies[i] = new FixtureProxy();
        }
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
        return shape;
    }

    /**
     * @return the type of the child shape. You can use this to down cast to the concrete shape.
     */
    public ShapeType getType() {
        return shape.getType();
    }

    /**
     * @return the contact filtering data
     */
    public Filter getFilterData() {
        return filter;
    }

    /**
     * Set the contact filtering data.
     * This will not update contacts until the next time step when either parent body is awake.
     * This automatically calls refilter.
     * This is an expensive operation and should not be called frequently.
     *
     * @param filter filter
     */
    public void setFilterData(final Filter filter) {
        this.filter.set(filter);

        refilter();
    }

    /**
     * The same as in the fixture definition, unless explicitly changed.
     * Use this to store your application specific data.
     *
     * @return user data
     */
    public Object getUserData() {
        return userData;
    }

    /**
     * Set the user data.
     * Use this to store your application specific data.
     *
     * @param data user data
     */
    public void setUserData(Object data) {
        userData = data;
    }

    public HitBox getHitBox() {
        return hitBox;
    }

    public void setHitBox(HitBox hitBox) {
        this.hitBox = hitBox;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getFriction() {
        return friction;
    }

    /**
     * This will <b>NOT</b> change the friction of existing contacts.
     */
    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getRestitution() {
        return restitution;
    }

    /**
     * This will <b>NOT</b> change the restitution of existing contacts.
     */
    public void setRestitution(float restitution) {
        this.restitution = restitution;
    }

    /**
     * @return true if the fixture / shape is a sensor (non-solid)
     */
    public boolean isSensor() {
        return isSensor;
    }

    /**
     * Set if this fixture is a sensor.
     */
    public void setSensor(boolean sensor) {
        if (sensor != isSensor) {
            body.setAwake(true);
            isSensor = sensor;
        }
    }

    public int getProxyCount() {
        return proxyCount;
    }

    public int getProxyId(int index) {
        return proxies[index].proxyId;
    }

    /**
     * Call this if you want to establish collision that was previously disabled by
     * ContactFilter::ShouldCollide.
     */
    public void refilter() {
        // Flag associated contacts for filtering.
        ContactEdge edge = body.getContactList();
        while (edge != null) {
            Contact contact = edge.contact;
            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();
            if (fixtureA == this || fixtureB == this) {
                contact.flagForFiltering();
            }
            edge = edge.next;
        }

        World world = body.getWorld();

        if (world == null) {
            return;
        }

        // Touch each proxy so that new pairs may be created
        BroadPhase broadPhase = world.getContactManager().broadPhase;
        for (int i = 0; i < proxyCount; ++i) {
            broadPhase.touchProxy(proxies[i].proxyId);
        }
    }

    /**
     * Test a point for containment in this fixture.
     * This only works for convex shapes.
     *
     * @param p a point in world coordinates
     */
    public boolean containsPoint(Vec2 p) {
        return shape.testPoint(body.m_xf, p);
    }

    /**
     * Cast a ray against this shape.
     *
     * @param output the ray-cast results
     * @param input the ray-cast input parameters
     */
    public boolean raycast(RayCastOutput output, RayCastInput input, int childIndex) {
        return shape.raycast(output, input, body.m_xf, childIndex);
    }

    /**
     * Get the mass data for this fixture.
     * The mass data is based on the density and the shape.
     * The rotational inertia is about the shape's origin.
     */
    public void getMassData(MassData massData) {
        shape.computeMass(massData, density);
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
        return shape.computeDistanceToOut(body.getTransform(), p, childIndex, normalOut);
    }

    // These support body activation/deactivation.
    void createProxies(BroadPhase broadPhase, final Transform xf) {
        // Create proxies in the broad-phase.
        proxyCount = shape.getChildCount();

        for (int i = 0; i < proxyCount; ++i) {
            FixtureProxy proxy = proxies[i];
            shape.computeAABB(proxy.aabb, xf, i);
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
            shape.computeAABB(aabb1, transform1, proxy.childIndex);
            shape.computeAABB(aabb2, transform2, proxy.childIndex);

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