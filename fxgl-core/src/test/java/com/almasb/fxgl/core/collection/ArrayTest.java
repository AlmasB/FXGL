package com.almasb.fxgl.core.collection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayTest
{
    @Test
    public void constructor_should_instantiate() {
        Array<Integer> array = new Array();
        array = new Array(Integer.class);
    }

    @Test
    public void array_has_correctEmptyStatus() {
        Array<Integer> array = new Array();
        assertAll(
                () -> assertTrue(array.isEmpty()),
                () -> assertFalse(array.isNotEmpty())
        );

        array.add(1);
        assertAll(
                () -> assertFalse(array.isEmpty()),
                () -> assertTrue(array.isNotEmpty())
        );
    }

    @Test
    public void array_has_correctSize() {
        Array<Integer> array = new Array<>();

        assertEquals(array.size(), 0);

        array.add(5);
        assertEquals(array.size(), 1);

        array.removeIndex(0);
        assertEquals(array.size(), 0);
    }

    @Test
    public void orderedConstructor_shouldBe_ordered() {
        Array array = new Array(true, 16, Integer.class);
        assertTrue(array.isOrdered());

        array = new Array(false, 16, Integer.class);
        assertFalse(array.isOrdered());
    }

    @Test
    public void addAll_should_addGivenElements() {
        Integer[] ints = new Integer[] { 1, 2, 3, 4, 5, 6, 7 };
        Array<Integer> array = new Array<>(Integer.class);

        array.addAll(ints);

        for(Integer integer : ints) {
            assertTrue(array.containsByEquality(integer));
        }

        Array<Integer> secondArray = new Array<>(Integer.class);
        Integer[] moreInts = new Integer[] {8, 9};
        secondArray.addAll(moreInts);

        array.addAll(secondArray);

        for(Integer integer : ints) {
            assertTrue(array.containsByEquality(integer));
        }

        for(Integer integer : moreInts) {
            assertTrue(array.containsByEquality(integer));
        }
    }

    @Test
    public void addAllStartPlusCount_should_beLessThanSize() {
        Array<String> array = new Array<>(String.class);

        Array<String> arrayToAdd = new Array<>(String.class);
        arrayToAdd.addAll("Hello", "World");

        assertThrows(IllegalArgumentException.class,
                () -> array.addAll(arrayToAdd, 2,5)
        );
    }

    @Test
    public void getItems_should_returnItems() {
        String[] strings = new String[] { "Hello", "World" };

        Array<String> array = new Array<>(false, 16, String.class);

        array.addAll(strings);

        String[] returnItems = array.getItems();

        for(int i = 0; i < strings.length; i++) {
            assertTrue(strings[i].equals(returnItems[i]));
        }
    }

    @Test
    public void addItem_should_resize() {
        Array<String> array = new Array<>(false, 1, String.class);
        array.insert(0, "String One");
        array.insert(1, "String Two");
        assertEquals(array.size(), 2);

        String[] strings = new String[] {"Hello", "World"};
        array = new Array<>(false, 1, String.class);

        array.add(strings[0]);
        array.add(strings[1]);
        assertEquals(array.size(), 2);

        Array<String> anotherArray = new Array<>(false, 1, String.class);
        anotherArray.addAll(array);
        assertEquals(anotherArray.size(), 2);
    }

    @Test
    public void index_should_returnItem() {
        Array<String> array = new Array<>(String.class);
        String[] strings = new String[] {"Hello", "World", "Java"};
        array.addAll(strings);

        assertEquals(array.get(1), "World");
    }

    @Test
    public void setIndex_should_replaceCorrectItem() {
        Array<String> array = new Array<>(String.class);
        String[] strings = new String[] {"Hello", "World", "Java"};
        array.addAll(strings);

        array.set(2, "Kotlin");

        assertAll(
                () -> assertTrue(array.containsByEquality("Hello")),
                () -> assertTrue(array.containsByEquality("World")),
                () -> assertTrue(array.containsByEquality("Kotlin"))
        );
    }

    @Test
    public void insertAtIndex_should_insert() {
        Array<String> array = new Array<>(false, 1, String.class);
        String[] strings = new String[] {"Hello", "World", "Java"};
        array.addAll(strings);

        assertThrows(IndexOutOfBoundsException.class,
                () -> array.insert(5, "Invalid Index")
        );

        array.insert(2, "Kotlin");

        assertAll(
                () -> assertEquals(array.get(2), "Kotlin"),
                () -> assertEquals(array.size(), 4),
                () -> assertTrue(array.containsByEquality("Hello")),
                () -> assertTrue(array.containsByEquality("World")),
                () -> assertTrue(array.containsByEquality("Kotlin"))
        );
    }

    @Test
    public void swap_should_swapTwoElements() {
        Array<String> array = new Array<>(String.class);
        array.addAll("Hello", "World");

        assertAll(
                () -> assertThrows(IndexOutOfBoundsException.class, () -> array.swap(1,3)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> array.swap(6,1))
        );

        array.swap(0,1);
        assertAll(
                () -> assertEquals(array.get(0), "World"),
                () -> assertEquals(array.get(1), "Hello")
        );
    }

    @Test
    public void containsByIdentity_should_comparePrimitives() {
        Array<Integer> array = new Array<>(Integer.class);

        array.add(1);
        assertTrue(array.containsByIdentity(1));
    }

    @Test
    public void containsElementDoesNotExist_should_returnFalse() {
        Array<Integer> array = new Array<>(Integer.class);

        assertAll(
                () -> assertFalse(array.containsByEquality(5)),
                () -> assertFalse(array.containsByIdentity(5))
        );

        array.add(5);

        assertAll(
                () -> assertTrue(array.containsByEquality(5)),
                () -> assertTrue(array.containsByIdentity(5))
        );
    }
}