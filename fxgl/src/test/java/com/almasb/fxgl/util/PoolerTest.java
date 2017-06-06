/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.util;

import com.almasb.fxgl.core.pool.Pool;
import com.almasb.fxgl.core.pool.Poolable;
import com.almasb.fxgl.service.Pooler;
import com.almasb.fxgl.service.impl.pooler.FXGLPooler;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PoolerTest {

    @Inject
    private Pooler pooler;

    @Before
    public void setUp() throws Exception {
        PoolableObject.objects = 0;

        Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(Integer.class).annotatedWith(Names.named("pooling.initialSize")).toInstance(128);
                        bind(Pooler.class).to(FXGLPooler.class);
                    }
                });

        injector.injectMembers(this);
    }

    @Test
    public void testGet() {
        PoolableObject obj = pooler.get(PoolableObject.class);

        assertThat(obj.field, is(0));
        assertThat(PoolableObject.objects, is(1));
    }

    @Test
    public void testPut() {
        PoolableObject obj = pooler.get(PoolableObject.class);
        obj.field = 99;

        pooler.put(obj);

        PoolableObject obj2 = pooler.get(PoolableObject.class);

        assertThat(obj2, is(obj));
        assertThat(obj.field, is(0));
    }

    @Test
    public void testRegisterPool() {
        pooler.registerPool(PoolableObject.class, new Pool<PoolableObject>() {
            @Override
            protected PoolableObject newObject() {
                return new PoolableObject(99);
            }
        });

        PoolableObject obj = pooler.get(PoolableObject.class);

        assertThat(obj.field, is(99));
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
