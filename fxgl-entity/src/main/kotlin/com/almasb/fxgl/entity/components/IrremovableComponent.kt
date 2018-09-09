/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.entity.component.Component

/**
 * Marks an entity that cannot be removed from the game world.
 * To remove such entity, this component must be removed first.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class IrremovableComponent : Component() {
}