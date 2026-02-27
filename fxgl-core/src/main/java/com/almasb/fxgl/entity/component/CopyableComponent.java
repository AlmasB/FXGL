/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.fxgl.core.Copyable;

/**
 * Marks a component as copyable.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface CopyableComponent<T extends Component> extends Copyable<T> {
}
