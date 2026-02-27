/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import java.lang.annotation.*;

/**
 * Adds a new dependency to this component on the provided component.
 * When this component is added to an entity and the provided component is missing,
 * an exception will be thrown.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RequiredComponents.class)
public @interface Required {
    Class<? extends Component> value();
}