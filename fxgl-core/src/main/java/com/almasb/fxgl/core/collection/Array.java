/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * Origin: libGDX.
 */

package com.almasb.fxgl.core.collection;

import com.almasb.fxgl.core.math.FXGLMath;

import java.util.*;

/**
 * A resizable, ordered or unordered array of objects.
 * If unordered, this class avoids a memory copy when removing elements
 * (the last element is moved to the removed element's position).
 *
 * @author Nathan Sweet
 */
public class Array<T> implements Iterable<T> {

    /**
     * Provides direct access to the underlying array.
     * If the Array's generic type is not Object, this field may only be accessed
     * if the {@link Array#Array(boolean, int, Class)} constructor was used.
     */
    private T[] items;

    private final boolean ordered;
    private int size;

    private ArrayIterable iterable;

    /**
     * Creates an ordered array with a capacity of 16.
     */
    public Array() {
        this(true, 16);
    }

    /**
     * Creates an ordered array with the specified capacity.
     *
     * @param capacity initial capacity
     */
    public Array(int capacity) {
        this(true, capacity);
    }

    /** @param ordered If false, methods that remove elements may change the order of other elements in the array, which avoids a
     *           memory copy.
     * @param capacity Any elements added beyond this will cause the backing array to be grown. */
    Array(boolean ordered, int capacity) {
        this.ordered = ordered;
        items = (T[]) new Object[capacity];
    }

    /** Creates a new array with {@link #items} of the specified type.
     * @param ordered If false, methods that remove elements may change the order of other elements in the array, which avoids a
     *           memory copy.
     * @param capacity Any elements added beyond this will cause the backing array to be grown. */
    Array(boolean ordered, int capacity, Class arrayType) {
        this.ordered = ordered;
        items = (T[]) newArray(arrayType, capacity);
    }

    /** Creates an ordered array with {@link #items} of the specified type and a capacity of 16. */
    public Array(Class arrayType) {
        this(true, 16, arrayType);
    }

    /** Creates a new array containing the elements in the specified array. The new array will have the same type of backing array
     * and will be ordered if the specified array is ordered. The capacity is set to the number of elements, so any subsequent
     * elements added will cause the backing array to be grown. */
    public Array(Array<? extends T> array) {
        this(array.ordered, array.size, array.items.getClass().getComponentType());
        size = array.size;
        System.arraycopy(array.items, 0, items, 0, size);
    }

    /** Creates a new ordered array containing the elements in the specified array. The new array will have the same type of
     * backing array. The capacity is set to the number of elements, so any subsequent elements added will cause the backing array
     * to be grown. */
    public Array(T[] array) {
        this(true, array, 0, array.length);
    }

    /** Creates a new array containing the elements in the specified array. The new array will have the same type of backing array.
     * The capacity is set to the number of elements, so any subsequent elements added will cause the backing array to be grown.
     * @param ordered If false, methods that remove elements may change the order of other elements in the array, which avoids a
     *           memory copy. */
    Array(boolean ordered, T[] array, int start, int count) {
        this(ordered, count, (Class) array.getClass().getComponentType());
        size = count;
        System.arraycopy(array, start, items, 0, size);
    }

    /**
     * Creates a new ordered array containing the elements of the given collection.
     * The order of elements placed in the array is dependent on the collection implementation.
     *
     * @param collection the collection to take elements from
     */
    public Array(Collection<T> collection) {
        this(collection.size());
        size = collection.size();
        collection.toArray(items);
    }

    /**
     * @return direct access to the underlying array
     */
    public T[] getItems() {
        return items;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isNotEmpty() {
        return size > 0;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void add(T value) {
        T[] newItems = this.items;

        if (size == newItems.length)
            newItems = resize(Math.max(8, (int) (size * 1.75f)));

        newItems[size++] = value;
    }

    public void addAll(Array<? extends T> array) {
        addAll(array, 0, array.size);
    }

    public void addAll(Array<? extends T> array, int start, int count) {
        if (start + count > array.size)
            throw new IllegalArgumentException("start + count must be <= size: " + start + " + " + count + " <= " + array.size);
        addAll((T[]) array.items, start, count);
    }

    public void addAll(T... array) {
        addAll(array, 0, array.length);
    }

    public void addAll(T[] array, int start, int count) {
        T[] items = this.items;
        int sizeNeeded = size + count;
        if (sizeNeeded > items.length)
            items = resize(Math.max(8, (int) (sizeNeeded * 1.75f)));

        System.arraycopy(array, start, items, size, count);
        size += count;
    }

    public T get(int index) {
        checkRange(index);

        return items[index];
    }

    public void set(int index, T value) {
        checkRange(index);

        items[index] = value;
    }

    public void insert(int index, T value) {
        if (index > size)
            throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + size);

        T[] items = this.items;
        if (size == items.length)
            items = resize(Math.max(8, (int) (size * 1.75f)));

        if (ordered)
            System.arraycopy(items, index, items, index + 1, size - index);
        else
            items[size] = items[index];

        size++;
        items[index] = value;
    }

    public void swap(int first, int second) {
        checkRange(first);
        checkRange(second);

        T[] items = this.items;
        T firstValue = items[first];
        items[first] = items[second];
        items[second] = firstValue;
    }

    /**
     * @param value May be null, the identity "==" comparison is used
     * @return true if array contains value, false if it doesn't
     */
    public boolean containsByIdentity(T value) {
        return contains(value, true);
    }

    /**
     * @param value May be null, the equality ".equals" comparison is used
     * @return true if array contains value, false if it doesn't
     */
    public boolean containsByEquality(T value) {
        return contains(value, false);
    }

    /**
     * @param value May be null
     * @param identity If true, == comparison will be used. If false, .equals() comparison will be used
     * @return true if array contains value, false if it doesn't
     */
    private boolean contains(T value, boolean identity) {
        T[] items = this.items;
        int i = size - 1;
        if (identity || value == null) {
            while (i >= 0)
                if (items[i--] == value)
                    return true;
        } else {
            while (i >= 0)
                if (value.equals(items[i--]))
                    return true;
        }
        return false;
    }

    /**
     * @param value May be null, the identity "==" comparison is used
     * @return An index of first occurrence of value in array or -1 if no such value exists
     */
    public int indexOfByIdentity(T value) {
        return indexOf(value, true);
    }

    /**
     * @param value May be null, the equality ".equals" comparison is used
     * @return An index of first occurrence of value in array or -1 if no such value exists
     */
    public int indexOfByEquality(T value) {
        return indexOf(value, false);
    }

    /**
     * @param value May be null
     * @param identity If true, == comparison will be used. If false, .equals() comparison will be used
     * @return An index of first occurrence of value in array or -1 if no such value exists
     */
    private int indexOf(T value, boolean identity) {
        T[] items = this.items;
        if (identity || value == null) {
            for (int i = 0, n = size; i < n; i++)
                if (items[i] == value)
                    return i;
        } else {
            for (int i = 0, n = size; i < n; i++)
                if (value.equals(items[i]))
                    return i;
        }
        return -1;
    }

    /**
     * @param value May be null, the identity "==" comparison is used
     * @return An index of last occurrence of value in array or -1 if no such value exists
     */
    public int lastIndexOfByIdentity(T value) {
        return lastIndexOf(value, true);
    }

    /**
     * @param value May be null, the equality ".equals" comparison is used
     * @return An index of last occurrence of value in array or -1 if no such value exists
     */
    public int lastIndexOfByEquality(T value) {
        return lastIndexOf(value, false);
    }

    /**
     * @param value May be null
     * @param identity If true, == comparison will be used. If false, .equals() comparison will be used
     * @return An index of last occurrence of value in array or -1 if no such value exists
     */
    private int lastIndexOf(T value, boolean identity) {
        T[] items = this.items;
        if (identity || value == null) {
            for (int i = size - 1; i >= 0; i--)
                if (items[i] == value)
                    return i;
        } else {
            for (int i = size - 1; i >= 0; i--)
                if (value.equals(items[i]))
                    return i;
        }
        return -1;
    }

    /**
     * Removes the first instance of the specified value in the array.
     *
     * @param value the value to compare against using identity (==)
     * @return true if value was found and removed, false otherwise
     */
    public boolean removeValueByIdentity(T value) {
        return removeValue(value, true);
    }

    /**
     * Removes the first instance of the specified value in the array.
     *
     * @param value the value to compare against using equality (.equals())
     * @return true if value was found and removed, false otherwise
     */
    public boolean removeValueByEquality(T value) {
        return removeValue(value, false);
    }

    /**
     * Removes the first instance of the specified value in the array.
     *
     * @param value May be null
     * @param identity If true, == comparison will be used. If false, .equals() comparison will be used
     * @return true if value was found and removed, false otherwise
     */
    private boolean removeValue(T value, boolean identity) {
        T[] items = this.items;
        if (identity || value == null) {
            for (int i = 0, n = size; i < n; i++) {
                if (items[i] == value) {
                    removeIndex(i);
                    return true;
                }
            }
        } else {
            for (int i = 0, n = size; i < n; i++) {
                if (value.equals(items[i])) {
                    removeIndex(i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes and returns the item at the specified index.
     *
     * @param index the index of the element to remove
     * @return removed element
     */
    public T removeIndex(int index) {
        checkRange(index);

        T[] items = this.items;
        T value = (T) items[index];
        size--;

        if (ordered)
            System.arraycopy(items, index + 1, items, index, size - index);
        else
            items[index] = items[size];

        items[size] = null;
        return value;
    }

    /**
     * Removes the items between the specified indices, inclusive.
     *
     * @param start start index
     * @param end end index
     */
    public void removeRange(int start, int end) {
        checkRange(end);

        if (start > end)
            throw new IndexOutOfBoundsException("start can't be > end: " + start + " > " + end);

        T[] items = this.items;
        int count = end - start + 1;

        if (ordered) {
            System.arraycopy(items, start + count, items, start, size - (start + count));
        } else {
            int lastIndex = this.size - 1;
            for (int i = 0; i < count; i++)
                items[start + i] = items[lastIndex - i];
        }

        size -= count;
    }

    /**
     * Removes from this array all of elements contained in the specified array using == to check.
     *
     * @param array given array
     * @return true if this array was modified
     */
    public boolean removeAllByIdentity(Array<? extends T> array) {
        return removeAll(array, true);
    }

    /**
     * Removes from this array all of elements contained in the specified array using .equals() to check.
     *
     * @param array given array
     * @return true if this array was modified
     */
    public boolean removeAllByEquality(Array<? extends T> array) {
        return removeAll(array, false);
    }

    /**
     * Removes from this array all of elements contained in the specified array.
     * Only the first occurrence of each element is removed.
     *
     * @param array given array
     * @param identity True to use ==, false to use .equals()
     * @return true if this array was modified
     */
    private boolean removeAll(Array<? extends T> array, boolean identity) {
        int size = this.size;
        int startSize = size;
        T[] items = this.items;

        if (identity) {
            for (int i = 0, n = array.size; i < n; i++) {
                T item = array.get(i);
                for (int ii = 0; ii < size; ii++) {
                    if (item == items[ii]) {
                        removeIndex(ii);
                        size--;
                        break;
                    }
                }
            }
        } else {
            for (int i = 0, n = array.size; i < n; i++) {
                T item = array.get(i);
                for (int ii = 0; ii < size; ii++) {
                    if (item.equals(items[ii])) {
                        removeIndex(ii);
                        size--;
                        break;
                    }
                }
            }
        }
        return size != startSize;
    }

    /**
     * Removes and returns the last item.
     *
     * @return last item
     */
    public T pop() {
        requireNotEmpty();

        --size;
        T item = items[size];
        items[size] = null;
        return item;
    }

    /**
     * @return the last item
     */
    public T last() {
        requireNotEmpty();

        return items[size - 1];
    }

    /**
     * @return the first item
     */
    public T first() {
        requireNotEmpty();

        return items[0];
    }

    /**
     * Removes all items.
     */
    public void clear() {
        T[] items = this.items;
        for (int i = 0, n = size; i < n; i++)
            items[i] = null;
        size = 0;
    }

    /**
     * Increases the size of the backing array to accommodate the specified number of additional items.
     * Useful before adding many items to avoid multiple backing array resizes.
     *
     * @param additionalCapacity extra capacity
     * @return items
     */
    public T[] ensureCapacity(int additionalCapacity) {
        int sizeNeeded = size + additionalCapacity;
        if (sizeNeeded > items.length)
            resize(Math.max(8, sizeNeeded));

        return items;
    }

    /**
     * Creates a new backing array with the specified size containing the current items.
     *
     * @param newSize new array size
     * @return items
     */
    protected T[] resize(int newSize) {
        T[] items = this.items;
        T[] newItems = (T[]) newArray(items.getClass().getComponentType(), newSize);
        System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
        this.items = newItems;
        return newItems;
    }

    /**
     * Reverses the items.
     */
    public void reverse() {
        T[] items = this.items;
        for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
            int ii = lastIndex - i;
            T temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /**
     * Shuffles the items.
     */
    public void shuffle() {
        T[] items = this.items;
        for (int i = size - 1; i >= 0; i--) {
            int ii = FXGLMath.random(0, i);
            T temp = items[i];
            items[i] = items[ii];
            items[ii] = temp;
        }
    }

    /**
     * Returns an iterator for the items in the array.
     * Remove is supported.
     * Note that the same iterator instance is returned each time this method is called.
     * Use the {@link ArrayIterator} constructor for nested or multithreaded iteration.
     *
     * @return iterator
     */
    @Override
    public Iterator<T> iterator() {
        if (iterable == null)
            iterable = new ArrayIterable(this);
        return iterable.iterator();
    }

    /**
     * @return a random item from the array
     */
    public T random() {
        requireNotEmpty();

        return items[FXGLMath.random(0, size - 1)];
    }

    /**
     * Returns the items as an array.
     * Note the array is typed, so the {@link #Array(Class)} constructor must have been used.
     * Otherwise use {@link #toArray(Class)} to specify the array type.
     *
     * @return Java array with items
     */
    public T[] toArray() {
        return (T[]) toArray(items.getClass().getComponentType());
    }

    public <V> V[] toArray(Class type) {
        V[] result = (V[]) newArray(type, size);
        System.arraycopy(items, 0, result, 0, size);
        return result;
    }

    /**
     * @return new list containing items from this Array
     */
    public List<T> toList() {
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(items[i]);
        }
        return list;
    }

    private void requireNotEmpty() {
        if (isEmpty())
            throw new IllegalStateException("Array is empty");
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     *
     * @param index to check
     */
    private void checkRange(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    @Override
    public int hashCode() {
        if (!ordered)
            return super.hashCode();

        Object[] items = this.items;
        int h = 1;
        for (int i = 0, n = size; i < n; i++) {
            h *= 31;
            Object item = items[i];
            if (item != null)
                h += item.hashCode();
        }
        return h;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;

        if (!ordered)
            return false;

        if (!(object instanceof Array))
            return false;

        Array array = (Array) object;
        if (!array.ordered)
            return false;

        int n = size;
        if (n != array.size)
            return false;

        Object[] items1 = this.items;
        Object[] items2 = array.items;
        for (int i = 0; i < n; i++) {
            Object o1 = items1[i];
            Object o2 = items2[i];
            if (!(o1 == null ? o2 == null : o1.equals(o2)))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (size == 0)
            return "[]";

        T[] items = this.items;
        StringBuilder buffer = new StringBuilder(32);
        buffer.append('[');
        buffer.append(items[0]);
        for (int i = 1; i < size; i++) {
            buffer.append(", ");
            buffer.append(items[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    /** @see #Array(Class) */
    public static <T> Array<T> of(Class<T> arrayType) {
        return new Array<T>(arrayType);
    }

    /** @see #Array(boolean, int, Class) */
    public static <T> Array<T> of(boolean ordered, int capacity, Class<T> arrayType) {
        return new Array<T>(ordered, capacity, arrayType);
    }

    /** @see #Array(Object[]) */
    public static <T> Array<T> with(T... array) {
        return new Array(array);
    }

    /**
     * Creates a new array with the specified component type and length.
     */
    @SuppressWarnings("PMD.UnnecessaryFullyQualifiedName")
    private static Object newArray(Class c, int size) {
        return java.lang.reflect.Array.newInstance(c, size);
    }

    public static class ArrayIterator<T> implements Iterator<T>, Iterable<T> {
        private final Array<T> array;
        private final boolean allowRemove;
        int index;
        boolean valid = true;

        public ArrayIterator(Array<T> array) {
            this(array, true);
        }

        public ArrayIterator(Array<T> array, boolean allowRemove) {
            this.array = array;
            this.allowRemove = allowRemove;
        }

        @Override
        public boolean hasNext() {
            if (!valid) {
                throw new IllegalArgumentException("#iterator() cannot be used nested.");
            }
            return index < array.size;
        }

        @Override
        public T next() {
            if (index >= array.size)
                throw new NoSuchElementException(String.valueOf(index));
            if (!valid) {
                throw new IllegalArgumentException("#iterator() cannot be used nested.");
            }
            return array.items[index++];
        }

        @Override
        public void remove() {
            if (!allowRemove)
                throw new IllegalArgumentException("Remove not allowed.");
            index--;
            array.removeIndex(index);
        }

        public void reset() {
            index = 0;
        }

        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }

    public static class ArrayIterable<T> implements Iterable<T> {
        private final Array<T> array;
        private final boolean allowRemove;
        private ArrayIterator iterator1, iterator2;

        public ArrayIterable(Array<T> array) {
            this(array, true);
        }

        public ArrayIterable(Array<T> array, boolean allowRemove) {
            this.array = array;
            this.allowRemove = allowRemove;
        }

        @Override
        public Iterator<T> iterator() {
            if (iterator1 == null) {
                iterator1 = new ArrayIterator(array, allowRemove);
                iterator2 = new ArrayIterator(array, allowRemove);
            }
            if (!iterator1.valid) {
                iterator1.index = 0;
                iterator1.valid = true;
                iterator2.valid = false;
                return iterator1;
            }
            iterator2.index = 0;
            iterator2.valid = true;
            iterator1.valid = false;
            return iterator2;
        }
    }

    private static final Array<?> EMPTY = new EmptyArray<>();

    @SuppressWarnings("unchecked")
    public static <T> Array<T> empty() {
        return (Array<T>) EMPTY;
    }

    private static class EmptyArray<T> extends Array<T> {

        EmptyArray() {
            super(0);
        }

        private void fail() {
            throw new UnsupportedOperationException("Unmodifiable");
        }

        @Override
        public void add(T value) {
            fail();
        }

        @Override
        public T[] getItems() {
            fail();
            return null;
        }

        @Override
        public void addAll(Array<? extends T> array) {
            fail();
        }

        @Override
        public void addAll(T... array) {
            fail();
        }

        @Override
        public void addAll(Array<? extends T> array, int start, int count) {
            fail();
        }

        @Override
        public void addAll(T[] array, int start, int count) {
            fail();
        }

        @Override
        public T[] ensureCapacity(int additionalCapacity) {
            fail();
            return null;
        }

        @Override
        protected T[] resize(int newSize) {
            fail();
            return null;
        }
    }
}
