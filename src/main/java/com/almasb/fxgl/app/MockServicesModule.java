/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.app;

import com.almasb.fxeventbus.EventBus;
import com.almasb.fxeventbus.FXEventBus;
import com.almasb.fxgl.concurrent.Executor;
import com.almasb.fxgl.concurrent.FXGLExecutor;
import com.almasb.fxgl.input.FXGLInput;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.logging.LoggerFactory;
import com.almasb.fxgl.logging.MockLoggerFactory;
import com.almasb.fxgl.util.FXGLPooler;
import com.almasb.fxgl.util.Pooler;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Module that binds services with their mock providers.
 * This is only needed for testing, as this allows us to test
 * each service in isolation (more or less).
 * Some services have actual "live" providers since they are required
 * to be functional by others, e.g. Pooler.
 * Almost all services are singleton only, so tests must reflect that.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class MockServicesModule extends AbstractModule {

    @Override
    protected void configure() {
        mockPooler();
        mockLoggerFactory();
        mockInput();
        mockExecutor();
        mockEventBus();
    }

    private void mockPooler() {
        bind(Integer.class).annotatedWith(Names.named("pooling.initialSize")).toInstance(128);
        bind(Pooler.class).to(FXGLPooler.class);
    }

    private void mockLoggerFactory() {
        bind(LoggerFactory.class).toInstance(MockLoggerFactory.INSTANCE);
    }

    private void mockInput() {
        bind(Input.class).to(FXGLInput.class);
    }

    private void mockExecutor() {
        bind(Executor.class).to(FXGLExecutor.class);
    }

    private void mockEventBus() {
        bind(EventBus.class).to(FXEventBus.class);
    }
}
