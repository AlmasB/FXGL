/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
/*
 * JBox2D - A Java Port of Erin Catto's Box2D
 * 
 * JBox2D homepage: http://jbox2d.sourceforge.net/
 * Box2D homepage: http://www.box2d.org
 * 
 * This software is provided 'as-is', without any express or implied
 * warranty.  In no event will the authors be held liable for any damages
 * arising from the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 * claim that you wrote the original software. If you use this software
 * in a product, an acknowledgment in the product documentation would be
 * appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */
package com.almasb.fxgl.physics.box2d.collision;

/**
 * Contact ids to facilitate warm starting. Note: the ContactFeatures class is just embedded in here
 */
public class ContactID implements Comparable<ContactID> {

    public enum Type {
        VERTEX, FACE
    }

    public byte indexA;
    public byte indexB;
    public byte typeA;
    public byte typeB;

    public int getKey() {
        return ((int) indexA) << 24 | ((int) indexB) << 16 | ((int) typeA) << 8 | ((int) typeB);
    }

    public boolean isEqual(final ContactID cid) {
        return getKey() == cid.getKey();
    }

    public ContactID() {
    }

    public ContactID(final ContactID c) {
        set(c);
    }

    public void set(final ContactID c) {
        indexA = c.indexA;
        indexB = c.indexB;
        typeA = c.typeA;
        typeB = c.typeB;
    }

    public void flip() {
        byte tempA = indexA;
        indexA = indexB;
        indexB = tempA;
        tempA = typeA;
        typeA = typeB;
        typeB = tempA;
    }

    /**
     * zeros out the data
     */
    public void zero() {
        indexA = 0;
        indexB = 0;
        typeA = 0;
        typeB = 0;
    }

    @Override
    public int compareTo(ContactID o) {
        return getKey() - o.getKey();
    }
}
