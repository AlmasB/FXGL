package com.almasb.fxgl.physics;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;

import javafx.geometry.Point2D;

public final class PhysicsEntity extends Entity {

    /*package-private*/ FixtureDef fixtureDef = new FixtureDef();
    /*package-private*/ BodyDef bodyDef = new BodyDef();

    /*package-private*/ Body body;
    /*package-private*/ Fixture fixture;

    public PhysicsEntity(EntityType type) {
        super(type);
    }

    public PhysicsEntity setFixtureDef(FixtureDef def) {
        fixtureDef = def;
        return this;
    }

    public PhysicsEntity setBodyDef(BodyDef def) {
        bodyDef = def;
        return this;
    }

    public PhysicsEntity setBodyType(BodyType type) {
        bodyDef.type = type;
        return this;
    }

    public Vec2 getBodyPosition() {
        return body.getPosition();
    }

    /**
     *
     * @param vector x and y in pixels
     * @return
     */
    public PhysicsEntity setLinearVelocity(Point2D vector) {
        return setBodyLinearVelocity(new Vec2(PhysicsManager.toMeters(vector.getX()), PhysicsManager.toMeters(-vector.getY())).mulLocal(60));
    }

    /**
     *
     * @param vector x and y in meters
     * @return
     */
    public PhysicsEntity setBodyLinearVelocity(Vec2 vector) {
        if (body == null)
            throw new IllegalStateException("PhysicsEntity has not been added to the world yet! Call addEntities(entity) first");

        body.setLinearVelocity(vector);
        return this;
    }
}
