/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced.platformer;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.state.StateComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RobotFactory implements EntityFactory {

    @Spawns("robot")
    public Entity newRobot(SpawnData data) {
        BodyDef bd = new BodyDef();
        bd.setFixedRotation(true);
        bd.setType(BodyType.DYNAMIC);

        PhysicsComponent physics = new PhysicsComponent();

        // friction 0 to avoid sticking to walls
        physics.setFixtureDef(new FixtureDef().friction(0).density(0.25f));
        physics.setBodyDef(bd);
        physics.addGroundSensor(new HitBox("GROUND_SENSOR", new Point2D(275 / 2 - 3, 260 - 5), BoundingShape.box(6, 10)));

        return FXGL.entityBuilder()
                .from(data)
                .bbox(new HitBox("main", new Point2D(275 / 2 - 105/2, 275 / 2 - 210/2), BoundingShape.box(105, 210)))
                .bbox(new HitBox("lower", new Point2D(275 / 2 - 15, 125*2), BoundingShape.box(30, 10)))
                .scaleOrigin(275 / 2, 125*2)
                .collidable()
                .with(new StateComponent())
                .with(physics)
                .with(new RobotComponent())
                .build();
    }
}
