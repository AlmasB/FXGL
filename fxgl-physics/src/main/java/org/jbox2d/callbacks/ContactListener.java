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

package org.jbox2d.callbacks;

import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * Implement this class to get contact information. You can use these results for
 * things like sounds and game logic. You can also get contact results by
 * traversing the contact lists after the time step. However, you might miss
 * some contacts because continuous physics leads to sub-stepping.
 * Additionally you may receive multiple callbacks for the same contact in a
 * single time step.
 * You should strive to make your callbacks efficient because there may be
 * many callbacks per time step.
 *
 * @warning You cannot create/destroy Box2D entities inside these callbacks.
 * @author Daniel Murphy
 */
public interface ContactListener {

    /**
     * Called when two fixtures begin to touch.
     *
     * @param contact contact info
     */
    void beginContact(Contact contact);

    /**
     * Called when two fixtures cease to touch.
     *
     * @param contact contact info
     */
    void endContact(Contact contact);

    /**
     * This is called after a contact is updated. This allows you to inspect a
     * contact before it goes to the solver. If you are careful, you can modify the
     * contact manifold (e.g. disable contact).
     * A copy of the old manifold is provided so that you can detect changes.
     * <p>
     * Note:
     * <ul>
     *     <li>This is called only for awake bodies.</li>
     *     <li>This is called even when the number of contact points is zero.</li>
     *     <li>This is not called for sensors.</li>
     *     <li>If you set the number of contact points to zero, you will not
     * get an EndContact callback. However, you may get a BeginContact callback the next step.</li>
     *     <li>The oldManifold parameter is pooled, so it will be the same object for every callback
     * for each thread.</li>
     * </ul>
     *
     * @param contact contact info
     * @param oldManifold the old manifold
     */
    void preSolve(Contact contact, Manifold oldManifold);

    /**
     * This lets you inspect a contact after the solver is finished. This is useful
     * for inspecting impulses.
     * <p>
     *     Note:
     *     <ul>
     *         <li>The contact manifold does not include time of impact impulses, which can be
     * arbitrarily large if the sub-step is small. Hence the impulse is provided explicitly
     * in a separate data structure.</li>
     *         <li>This is only called for contacts that are touching, solid, and awake.</li>
     *         <li>The impulse parameter is usually a pooled variable, so it will be modified after
     * this call.</li>
     *     </ul>
     * </p>
     *
     * @param contact contact info
     * @param impulse contact impulse
     */
    void postSolve(Contact contact, ContactImpulse impulse);
}
