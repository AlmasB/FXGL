/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate.levels.text;

import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.KeepOnScreenComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TextLevelEntityFactory implements EntityFactory {

    @Spawns("r,rect,type1")
    public Entity newRectangle(SpawnData data) {
        return entityBuilder(data)
                .view(new Rectangle(30, 30))
                .build();
    }

    @Spawns("")
    public Entity newEmpty(SpawnData data) {
        return entityBuilder(data)
                .view(new Rectangle(30, 30, Color.RED))
                .build();
    }

    @Spawns("c,circle,type2,type3")
    public Entity newCircle(SpawnData data) {
        return entityBuilder(data)
                .view(new Circle(15, 15, 15))
                .build();
    }

    @Spawns("Wall,Player,Coin")
    public Entity newObject(SpawnData data) {
        return entityBuilder(data)
                .with(new KeepOnScreenComponent().onlyHorizontally())
                .with(new ExpireCleanComponent(Duration.seconds(3)))
                .build();
    }
}
