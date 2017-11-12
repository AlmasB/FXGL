/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import com.almasb.fxgl.entity.SetEntityFactory;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class UnoFactory implements EntityFactory {

    @Spawns("Card")
    public Entity newCard(SpawnData data) {
        return Entities.builder()
                .from(data)
                .viewFromNode(new CardView(data.get("card")))
                .with(new CardComponent(data.get("card")))
                .build();
    }
}
