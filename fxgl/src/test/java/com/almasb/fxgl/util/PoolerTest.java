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

package com.almasb.fxgl.util;

import com.almasb.fxgl.service.Pooler;
import com.almasb.fxgl.service.impl.pooler.FXGLPooler;
import com.almasb.fxgl.core.pool.Pool;
import com.almasb.fxgl.core.pool.Poolable;
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
