/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.test3d.snake;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SnakeFactory implements EntityFactory {

    @Spawns("food")
    public Entity newFood(SpawnData data) {
        Box box = new Box(1, 1, 1);
        box.setMaterial(new PhongMaterial(Color.YELLOW));

        return entityBuilder(data)
                .type(SnakeApp.SnakeType.FOOD)
                .bbox(new HitBox(BoundingShape.box3D(1, 1, 1)))
                .view(box)
                .collidable()
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        Box box = new Box(1, 1, 1);
        box.setMaterial(new PhongMaterial(Color.BLUE));

        return entityBuilder(data)
                .type(SnakeApp.SnakeType.SNAKE)
                .bbox(new HitBox(BoundingShape.box3D(1, 1, 1)))
                .view(box)
                .collidable()
                .with(new SnakeComponent())
                .build();
    }
}
