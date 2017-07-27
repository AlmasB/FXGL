package com.almasb.fxgl.ecs.component

import com.almasb.fxgl.ecs.Component

/**
 * Marks an entity that cannot be removed from the game world.
 * To remove such entity, this component must be removed first.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class IrremovableComponent : Component() {
}