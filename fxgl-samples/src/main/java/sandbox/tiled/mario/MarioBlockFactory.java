/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiled.mario;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.component.CollidableComponent;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MarioBlockFactory implements EntityFactory {

    @Spawns("block")
    public Entity newBlock(SpawnData data) {
        return Entities.builder(MarioType.class)
                .from(data)
                .viewFromNodeWithBBox(new Rectangle(70, 70))
                .with(new CollidableComponent(true))
                .with(new CrusherControl(650, new Entity()))
                .build();
    }
}
