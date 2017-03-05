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
package org.jbox2d.dynamics.contacts;

import org.jbox2d.dynamics.Body;

/**
 * A contact edge is used to connect bodies and contacts together in a contact graph where each body
 * is a node and each contact is an edge. A contact edge belongs to a doubly linked list maintained
 * in each attached body. Each contact has two contact nodes, one for each attached body.
 *
 * @author daniel
 */
public class ContactEdge {

    /**
     * provides quick access to the other body attached.
     */
    public Body other = null;

    /**
     * the contact
     */
    public Contact contact = null;

    /**
     * the previous contact edge in the body's contact list
     */
    public ContactEdge prev = null;

    /**
     * the next contact edge in the body's contact list
     */
    public ContactEdge next = null;
}
