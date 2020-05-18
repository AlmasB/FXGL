/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced.platformer;

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

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

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

        return entityBuilder(data)
                .from(data)
                .bbox(new HitBox("head", new Point2D(110, 50), BoundingShape.box(70, 70)))
                .bbox(new HitBox("body", new Point2D(110, 120), BoundingShape.box(40, 130)))
                .bbox(new HitBox("legs", new Point2D(275 / 2 - 25, 125*2), BoundingShape.box(40, 10)))
                .scaleOrigin(275 / 2, 125*2)
                .collidable()
                .with(new StateComponent())
                .with(physics)
                .with(new RobotComponent())
                .build();
    }

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        return entityBuilder(data)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .build();
    }
}
