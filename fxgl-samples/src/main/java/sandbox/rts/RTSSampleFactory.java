/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.rts;

import com.almasb.fxgl.annotation.SetEntityFactory;
import com.almasb.fxgl.annotation.Spawns;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.CollidableComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SetEntityFactory
public class RTSSampleFactory implements EntityFactory {

    @Spawns("TownHall")
    public Entity newTownHall(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(RTSSampleType.TOWN_HALL)
                .viewFromNodeWithBBox(new Circle(40, Color.RED))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("Worker")
    public Entity newWorker(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(RTSSampleType.WORKER)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.color(FXGLMath.random(), FXGLMath.random(), FXGLMath.random())))
                .with(new CollidableComponent(true), new BackpackComponent())
                .with(new FSMControl<>(WorkerState.class, WorkerState.IDLE))
                .build();
    }

    @Spawns("GoldMine")
    public Entity newGoldMine(SpawnData data) {
        return Entities.builder()
                .from(data)
                .type(RTSSampleType.GOLD_MINE)
                .viewFromNodeWithBBox(new Circle(20, Color.GOLD))
                .with(new CollidableComponent(true), new GoldMineComponent())
                .build();
    }
}
