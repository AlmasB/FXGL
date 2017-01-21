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

/**
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.collection;

import java.util.Comparator;

/** This class is for selecting a ranked element (kth ordered statistic) from an unordered list in faster time than sorting the
 * whole array. Typical applications include finding the nearest enemy unit(s), and other operations which are likely to run as
 * often as every x frames. Certain values of k will result in a partial sorting of the Array.
 * <p>
 * The lowest ranking element starts at 1, not 0. 1 = first, 2 = second, 3 = third, etc. calling with a value of zero will result
 * in a {@link IllegalArgumentException}
 * </p>
 * <p>
 * This class uses very minimal extra memory, as it makes no copies of the array. The underlying algorithms used are a naive
 * single-pass for k=min and k=max, and Hoare's quickselect for values in between.
 * </p>
 * @author Jon Renner */
public class Select {
    private static Select instance;
    private QuickSelect quickSelect;

    /** Provided for convenience */
    public static Select instance() {
        if (instance == null) instance = new Select();
        return instance;
    }

    public <T> T select(T[] items, Comparator<T> comp, int kthLowest, int size) {
        int idx = selectIndex(items, comp, kthLowest, size);
        return items[idx];
    }

    public <T> int selectIndex(T[] items, Comparator<T> comp, int kthLowest, int size) {
        if (size < 1) {
            throw new IllegalArgumentException("cannot select from empty array (size < 1)");
        } else if (kthLowest > size) {
            throw new IllegalArgumentException("Kth rank is larger than size. k: " + kthLowest + ", size: " + size);
        }
        int idx;
        // naive partial selection sort almost certain to outperform quickselect where n is min or max
        if (kthLowest == 1) {
            // find min
            idx = fastMin(items, comp, size);
        } else if (kthLowest == size) {
            // find max
            idx = fastMax(items, comp, size);
        } else {
            // quickselect a better choice for cases of k between min and max
            if (quickSelect == null) quickSelect = new QuickSelect();
            idx = quickSelect.select(items, comp, kthLowest, size);
        }
        return idx;
    }

    /** Faster than quickselect for n = min */
    private <T> int fastMin(T[] items, Comparator<T> comp, int size) {
        int lowestIdx = 0;
        for (int i = 1; i < size; i++) {
            int comparison = comp.compare(items[i], items[lowestIdx]);
            if (comparison < 0) {
                lowestIdx = i;
            }
        }
        return lowestIdx;
    }

    /** Faster than quickselect for n = max */
    private <T> int fastMax(T[] items, Comparator<T> comp, int size) {
        int highestIdx = 0;
        for (int i = 1; i < size; i++) {
            int comparison = comp.compare(items[i], items[highestIdx]);
            if (comparison > 0) {
                highestIdx = i;
            }
        }
        return highestIdx;
    }
}
