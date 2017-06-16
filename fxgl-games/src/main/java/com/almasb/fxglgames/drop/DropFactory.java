/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.drop;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.OffscreenCleanControl;

/**
 * Responsible for creating new entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@SetEntityFactory
public class DropFactory implements EntityFactory {

    @Spawns("Droplet")
    public Entity newDroplet(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(DropType.DROPLET)
                .viewFromTextureWithBBox("drop/droplet.png")
                .with(new CollidableComponent(true))
                .with(new DropletControl(), new OffscreenCleanControl())
                .build();
    }

    @Spawns("Bucket")
    public Entity newBucket(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(DropType.BUCKET)
                .viewFromTextureWithBBox("drop/bucket.png")
                .with(new CollidableComponent(true))
                .with(new BucketControl())
                .build();
    }
}
