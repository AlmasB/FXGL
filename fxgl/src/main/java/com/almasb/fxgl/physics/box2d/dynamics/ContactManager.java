/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.dynamics;

import com.almasb.fxgl.physics.box2d.callbacks.ContactFilter;
import com.almasb.fxgl.physics.box2d.callbacks.ContactListener;
import com.almasb.fxgl.physics.box2d.callbacks.PairCallback;
import com.almasb.fxgl.physics.box2d.collision.broadphase.BroadPhase;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.Contact;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.ContactEdge;

/**
 * Delegate of World.
 *
 * @author Daniel Murphy
 */
class ContactManager implements PairCallback {

    public Contact m_contactList = null;
    public int m_contactCount = 0;
    public ContactFilter m_contactFilter = new ContactFilter();
    public ContactListener m_contactListener = null;

    private final World pool;
    public final BroadPhase m_broadPhase;

    ContactManager(World argPool, BroadPhase broadPhase) {
        pool = argPool;
        m_broadPhase = broadPhase;
    }

    /**
     * Broad-phase callback.
     *
     * @param proxyUserDataA proxy user data A
     * @param proxyUserDataB proxy user data B
     */
    @Override
    public void addPair(Object proxyUserDataA, Object proxyUserDataB) {
        FixtureProxy proxyA = (FixtureProxy) proxyUserDataA;
        FixtureProxy proxyB = (FixtureProxy) proxyUserDataB;

        Fixture fixtureA = proxyA.fixture;
        Fixture fixtureB = proxyB.fixture;

        int indexA = proxyA.childIndex;
        int indexB = proxyB.childIndex;

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        // Are the fixtures on the same body?
        if (bodyA == bodyB) {
            return;
        }

        // TODO_ERIN use a hash table to remove a potential bottleneck when both
        // bodies have a lot of contacts.
        // Does a contact already exist?
        ContactEdge edge = bodyB.getContactList();
        while (edge != null) {
            if (edge.other == bodyA) {
                Fixture fA = edge.contact.getFixtureA();
                Fixture fB = edge.contact.getFixtureB();
                int iA = edge.contact.getChildIndexA();
                int iB = edge.contact.getChildIndexB();

                if (fA == fixtureA && iA == indexA && fB == fixtureB && iB == indexB) {
                    // A contact already exists.
                    return;
                }

                if (fA == fixtureB && iA == indexB && fB == fixtureA && iB == indexA) {
                    // A contact already exists.
                    return;
                }
            }

            edge = edge.next;
        }

        // Does a joint override collision? is at least one body dynamic?
        if (!bodyB.shouldCollide(bodyA)) {
            return;
        }

        // Check user filtering.
        if (m_contactFilter != null && !m_contactFilter.shouldCollide(fixtureA, fixtureB)) {
            return;
        }

        // Call the factory.
        Contact c = pool.popContact(fixtureA, indexA, fixtureB, indexB);
        if (c == null) {
            return;
        }

        // Contact creation may swap fixtures.
        fixtureA = c.getFixtureA();
        fixtureB = c.getFixtureB();
        indexA = c.getChildIndexA();
        indexB = c.getChildIndexB();
        bodyA = fixtureA.getBody();
        bodyB = fixtureB.getBody();

        // Insert into the world.
        c.m_prev = null;
        c.m_next = m_contactList;
        if (m_contactList != null) {
            m_contactList.m_prev = c;
        }
        m_contactList = c;

        // Connect to island graph.

        // Connect to body A
        c.m_nodeA.contact = c;
        c.m_nodeA.other = bodyB;

        c.m_nodeA.prev = null;
        c.m_nodeA.next = bodyA.m_contactList;
        if (bodyA.m_contactList != null) {
            bodyA.m_contactList.prev = c.m_nodeA;
        }
        bodyA.m_contactList = c.m_nodeA;

        // Connect to body B
        c.m_nodeB.contact = c;
        c.m_nodeB.other = bodyA;

        c.m_nodeB.prev = null;
        c.m_nodeB.next = bodyB.m_contactList;
        if (bodyB.m_contactList != null) {
            bodyB.m_contactList.prev = c.m_nodeB;
        }
        bodyB.m_contactList = c.m_nodeB;

        // wake up the bodies
        if (!fixtureA.isSensor() && !fixtureB.isSensor()) {
            bodyA.setAwake(true);
            bodyB.setAwake(true);
        }

        ++m_contactCount;
    }

    public void findNewContacts() {
        m_broadPhase.updatePairs(this);
    }

    public void destroy(Contact c) {
        Fixture fixtureA = c.getFixtureA();
        Fixture fixtureB = c.getFixtureB();
        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        if (m_contactListener != null && c.isTouching()) {
            m_contactListener.endContact(c);
        }

        // Remove from the world.
        if (c.m_prev != null) {
            c.m_prev.m_next = c.m_next;
        }

        if (c.m_next != null) {
            c.m_next.m_prev = c.m_prev;
        }

        if (c == m_contactList) {
            m_contactList = c.m_next;
        }

        // Remove from body 1
        if (c.m_nodeA.prev != null) {
            c.m_nodeA.prev.next = c.m_nodeA.next;
        }

        if (c.m_nodeA.next != null) {
            c.m_nodeA.next.prev = c.m_nodeA.prev;
        }

        if (c.m_nodeA == bodyA.m_contactList) {
            bodyA.m_contactList = c.m_nodeA.next;
        }

        // Remove from body 2
        if (c.m_nodeB.prev != null) {
            c.m_nodeB.prev.next = c.m_nodeB.next;
        }

        if (c.m_nodeB.next != null) {
            c.m_nodeB.next.prev = c.m_nodeB.prev;
        }

        if (c.m_nodeB == bodyB.m_contactList) {
            bodyB.m_contactList = c.m_nodeB.next;
        }

        // Call the factory.
        pool.pushContact(c);
        --m_contactCount;
    }

    /**
     * This is the top level collision call for the time step. Here all the narrow phase collision is
     * processed for the world contact list.
     */
    public void collide() {
        // Update awake contacts.
        Contact c = m_contactList;
        while (c != null) {
            Fixture fixtureA = c.getFixtureA();
            Fixture fixtureB = c.getFixtureB();
            int indexA = c.getChildIndexA();
            int indexB = c.getChildIndexB();
            Body bodyA = fixtureA.getBody();
            Body bodyB = fixtureB.getBody();

            // is this contact flagged for filtering?
            if ((c.m_flags & Contact.FILTER_FLAG) == Contact.FILTER_FLAG) {
                // Should these bodies collide?
                if (!bodyB.shouldCollide(bodyA)) {
                    Contact cNuke = c;
                    c = cNuke.getNext();
                    destroy(cNuke);
                    continue;
                }

                // Check user filtering.
                if (m_contactFilter != null && !m_contactFilter.shouldCollide(fixtureA, fixtureB)) {
                    Contact cNuke = c;
                    c = cNuke.getNext();
                    destroy(cNuke);
                    continue;
                }

                // Clear the filtering flag.
                c.m_flags &= ~Contact.FILTER_FLAG;
            }

            boolean activeA = bodyA.isAwake() && bodyA.getType() != BodyType.STATIC;
            boolean activeB = bodyB.isAwake() && bodyB.getType() != BodyType.STATIC;

            // At least one body must be awake and it must be dynamic or kinematic.
            if (!activeA && !activeB) {
                c = c.getNext();
                continue;
            }

            int proxyIdA = fixtureA.m_proxies[indexA].proxyId;
            int proxyIdB = fixtureB.m_proxies[indexB].proxyId;
            boolean overlap = m_broadPhase.testOverlap(proxyIdA, proxyIdB);

            // Here we destroy contacts that cease to overlap in the broad-phase.
            if (!overlap) {
                Contact cNuke = c;
                c = cNuke.getNext();
                destroy(cNuke);
                continue;
            }

            // The contact persists.
            c.update(m_contactListener);
            c = c.getNext();
        }
    }
}
