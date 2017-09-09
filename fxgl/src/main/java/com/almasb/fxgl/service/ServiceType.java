/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.service;

import com.almasb.fxgl.app.FXGLExceptionHandler;
import com.almasb.fxgl.service.impl.display.FXGLDisplay;
import com.almasb.fxgl.service.impl.executor.FXGLExecutor;
import com.almasb.fxgl.service.impl.net.FXGLNet;
import com.google.inject.Scope;
import com.google.inject.Scopes;

/**
 * Marks a service type.
 * A service is a single aspect of FXGL that is accessible globally
 * and has a single instance per application.
 * A service only knows about EventBus and does not know about other services
 * or other things like game world, game scene, etc.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface ServiceType<T> {

    /**
     * @return service interface/class
     */
    Class<T> service();

    /**
     * @return service implementation/provider
     */
    Class<? extends T> serviceProvider();

    /**
     * @return service scope
     */
    default Scope scope() {
        return Scopes.SINGLETON;
    }


}
