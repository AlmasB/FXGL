/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics.box2d.dynamics.contacts;

import com.almasb.fxgl.physics.box2d.dynamics.Fixture;
import com.almasb.fxgl.physics.box2d.pooling.IWorldPool;

// updated to rev 100
public interface ContactCreator {

    public Contact contactCreateFcn(IWorldPool argPool, Fixture fixtureA, Fixture fixtureB);

    public void contactDestroyFcn(IWorldPool argPool, Contact contact);
}
