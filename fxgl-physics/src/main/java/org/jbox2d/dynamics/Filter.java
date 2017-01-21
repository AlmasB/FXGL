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

package org.jbox2d.dynamics;

/**
 * This holds contact filtering data.
 *
 * @author daniel
 */
public class Filter {
    /**
     * The collision category bits. Normally you would just set one bit.
     */
    public int categoryBits = 0x0001;

    /**
     * The collision mask bits. This states the categories that this
     * shape would accept for collision.
     */
    public int maskBits = 0xFFFF;

    /**
     * Collision groups allow a certain group of objects to never collide (negative)
     * or always collide (positive). Zero means no collision group. Non-zero group
     * filtering always wins against the mask bits.
     */
    public int groupIndex = 0;

    public void set(Filter argOther) {
        categoryBits = argOther.categoryBits;
        maskBits = argOther.maskBits;
        groupIndex = argOther.groupIndex;
    }
}
