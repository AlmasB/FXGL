/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.annotation;

import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.EntityFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a class will be used as the main entity factory.
 * The class will be automatically instantiated and attached
 * to the game world using {@link GameWorld#setEntityFactory(EntityFactory)}.
 * Note: if it is important that only a single instance of the factory
 * is created, then you need to annotate it with {@link com.google.inject.Singleton}.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SetEntityFactory {
}
