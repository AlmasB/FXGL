/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.pool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PoolerTest {

    @BeforeEach
    public void setUp() throws Exception {
        PoolableObject.objects = 0;
    }

    @Test
    public void testGet() {
        PoolableObject obj = Pools.obtain(PoolableObject.class);

        assertThat(obj.field, is(0));
        assertThat(PoolableObject.objects, is(1));
    }

    @Test
    public void testPut() {
        PoolableObject obj = Pools.obtain(PoolableObject.class);
        obj.field = 99;

        Pools.free(obj);

        PoolableObject obj2 = Pools.obtain(PoolableObject.class);

        assertThat(obj2, is(obj));
        assertThat(obj.field, is(0));

        // pools ignore objects which were never in the pool
        Pools.free("");
    }

    @Test
    public void testRegisterPool() {
        Pools.set(PoolableObject.class, new Pool<PoolableObject>() {
            @Override
            protected PoolableObject newObject() {
                return new PoolableObject(99);
            }
        });

        PoolableObject obj = Pools.obtain(PoolableObject.class);

        assertThat(obj.field, is(99));
    }

    @Test
    public void testPoolClear() {
        Pool<Stateful> pool = new Pool<>(1) {
            @Override
            protected Stateful newObject() {
                return new Stateful(44);
            }
        };

        Stateful obj = pool.obtain();
        obj.field = 10;

        pool.clear();

        assertThat(pool.obtain().field, is(44));
    }

    @Test
    public void testPoolNonPoolable() {
        Pool<String> pool = new Pool<>(1, 2) {
            @Override
            protected String newObject() {
                return "Hi";
            }
        };

        pool.free("");
        pool.free("");
        pool.free("");
    }

    public static class Stateful implements Poolable {

        int field = 0;

        public Stateful(int value) {
            field = value;
        }

        @Override
        public void reset() { }
    }

    public static class PoolableObject implements Poolable {

        private static int objects = 0;
        private int field = 0;

        public PoolableObject() {
            objects++;
        }

        public PoolableObject(int field) {
            this.field = field;
        }

        @Override
        public void reset() {
            field = 0;
        }
    }
}
