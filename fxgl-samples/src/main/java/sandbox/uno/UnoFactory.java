/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.uno;

import com.almasb.fxgl.entity.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
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
