/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how Tiled objects and tiles can be flipped and rotated.
 *
 * @author Adam Bocco (adambocco) (adam.bocco@gmail.com)
 */
public class TiledFlippingSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("TileFlippingExample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new TileFlippingEntityFactory());

        List<Entity> entities = setLevelFromMap("tmx/flipped_tiles.tmx").getEntities();

        // Rotate all of the objects (index 0 is tile layer)
        Entity f = entities.get(1);
        Entity x = entities.get(2);
        Entity g = entities.get(3);
        Entity l = entities.get(4);

        f.rotateBy(10.0);
        x.rotateBy(20.0);
        g.rotateBy(30.0);
        l.rotateBy(40.0);
    }

    public static void main(String[] args) {
        launch(args);
    }


    public static class TileFlippingEntityFactory implements EntityFactory {

        @Spawns("f,x,g,l")
        public Entity newLetter(SpawnData data) {
            return entityBuilder(data)
                    .build();
        }
    }
}
