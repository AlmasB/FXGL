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

/** Implementation of Tony Hoare's quickselect algorithm. Running time is generally O(n), but worst case is O(n^2) Pivot choice is
 * median of three method, providing better performance than a random pivot for partially sorted data.
 * http://en.wikipedia.org/wiki/Quickselect
 * @author Jon Renner */
public class QuickSelect<T> {
    private T[] array;
    private Comparator<? super T> comp;

    public int select(T[] items, Comparator<T> comp, int n, int size) {
        this.array = items;
        this.comp = comp;
        return recursiveSelect(0, size - 1, n);
    }

    private int partition(int left, int right, int pivot) {
        T pivotValue = array[pivot];
        swap(right, pivot);
        int storage = left;
        for (int i = left; i < right; i++) {
            if (comp.compare(array[i], pivotValue) < 0) {
                swap(storage, i);
                storage++;
            }
        }
        swap(right, storage);
        return storage;
    }

    private int recursiveSelect(int left, int right, int k) {
        if (left == right) return left;
        int pivotIndex = medianOfThreePivot(left, right);
        int pivotNewIndex = partition(left, right, pivotIndex);
        int pivotDist = (pivotNewIndex - left) + 1;
        int result;
        if (pivotDist == k) {
            result = pivotNewIndex;
        } else if (k < pivotDist) {
            result = recursiveSelect(left, pivotNewIndex - 1, k);
        } else {
            result = recursiveSelect(pivotNewIndex + 1, right, k - pivotDist);
        }
        return result;
    }

    /** Median of Three has the potential to outperform a random pivot, especially for partially sorted arrays */
    private int medianOfThreePivot(int leftIdx, int rightIdx) {
        T left = array[leftIdx];
        int midIdx = (leftIdx + rightIdx) / 2;
        T mid = array[midIdx];
        T right = array[rightIdx];

        // spaghetti median of three algorithm
        // does at most 3 comparisons
        if (comp.compare(left, mid) > 0) {
            if (comp.compare(mid, right) > 0) {
                return midIdx;
            } else if (comp.compare(left, right) > 0) {
                return rightIdx;
            } else {
                return leftIdx;
            }
        } else {
            if (comp.compare(left, right) > 0) {
                return leftIdx;
            } else if (comp.compare(mid, right) > 0) {
                return rightIdx;
            } else {
                return midIdx;
            }
        }
    }

    private void swap(int left, int right) {
        T tmp = array[left];
        array[left] = array[right];
        array[right] = tmp;
    }
}
