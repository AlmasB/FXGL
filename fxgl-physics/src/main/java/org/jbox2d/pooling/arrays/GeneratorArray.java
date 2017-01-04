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

package org.jbox2d.pooling.arrays;

import org.jbox2d.particle.VoronoiDiagram;

import java.util.HashMap;

public class GeneratorArray {

    private final HashMap<Integer, VoronoiDiagram.Generator[]> map =
            new HashMap<Integer, VoronoiDiagram.Generator[]>();

    public VoronoiDiagram.Generator[] get(int length) {
        assert (length > 0);

        if (!map.containsKey(length)) {
            map.put(length, getInitializedArray(length));
        }

        assert (map.get(length).length == length) : "Array not built of correct length";
        return map.get(length);
    }

    protected VoronoiDiagram.Generator[] getInitializedArray(int length) {
        final VoronoiDiagram.Generator[] ray = new VoronoiDiagram.Generator[length];
        for (int i = 0; i < ray.length; i++) {
            ray[i] = new VoronoiDiagram.Generator();
        }
        return ray;
    }
}
