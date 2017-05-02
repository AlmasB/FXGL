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

import java.util.Iterator;

/** Interface used to select items within an iterator against a predicate.
 * @author Xoppa */
public interface Predicate<T> {

    /** @return true if the item matches the criteria and should be included in the iterator's items */
    boolean evaluate(T arg0);

    public class PredicateIterator<T> implements Iterator<T> {
        public Iterator<T> iterator;
        public Predicate<T> predicate;
        public boolean end = false;
        public boolean peeked = false;
        public T next = null;

        public PredicateIterator(final Iterable<T> iterable, final Predicate<T> predicate) {
            this(iterable.iterator(), predicate);
        }

        public PredicateIterator(final Iterator<T> iterator, final Predicate<T> predicate) {
            set(iterator, predicate);
        }

        public void set(final Iterable<T> iterable, final Predicate<T> predicate) {
            set(iterable.iterator(), predicate);
        }

        public void set(final Iterator<T> iterator, final Predicate<T> predicate) {
            this.iterator = iterator;
            this.predicate = predicate;
            end = peeked = false;
            next = null;
        }

        @Override
        public boolean hasNext() {
            if (end) return false;
            if (next != null) return true;
            peeked = true;
            while (iterator.hasNext()) {
                final T n = iterator.next();
                if (predicate.evaluate(n)) {
                    next = n;
                    return true;
                }
            }
            end = true;
            return false;
        }

        @Override
        public T next() {
            if (next == null && !hasNext()) return null;
            final T result = next;
            next = null;
            peeked = false;
            return result;
        }

        @Override
        public void remove() {
            if (peeked) throw new IllegalStateException("Cannot remove between a call to hasNext() and next().");
            iterator.remove();
        }
    }

    public static class PredicateIterable<T> implements Iterable<T> {
        public Iterable<T> iterable;
        public Predicate<T> predicate;
        public PredicateIterator<T> iterator = null;

        public PredicateIterable(Iterable<T> iterable, Predicate<T> predicate) {
            set(iterable, predicate);
        }

        public void set(Iterable<T> iterable, Predicate<T> predicate) {
            this.iterable = iterable;
            this.predicate = predicate;
        }

        /** Returns an iterator. Note that the same iterator instance is returned each time this method is called. Use the
         * {@link PredicateIterator} constructor for nested or multithreaded iteration. */
        @Override
        public Iterator<T> iterator() {
            if (iterator == null)
                iterator = new PredicateIterator<T>(iterable.iterator(), predicate);
            else
                iterator.set(iterable.iterator(), predicate);
            return iterator;
        }
    }
}
