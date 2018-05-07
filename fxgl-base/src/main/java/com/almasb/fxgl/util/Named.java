/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.util;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation that provides information about argument's name.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Named {

    /**
     * The name of the annotated argument.
     * @return the name of the annotated argument
     */
    String value();
}
