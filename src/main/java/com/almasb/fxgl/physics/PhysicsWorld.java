/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.physics;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.effect.Particle;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.TypeComponent;
import com.almasb.fxgl.event.UpdateEvent;
import com.almasb.fxgl.gameplay.GameWorldListener;
import com.almasb.fxgl.logging.FXGLLoggerOld;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.particle.ParticleGroup;
import org.jbox2d.particle.ParticleGroupDef;
import org.jbox2d.particle.ParticleSystem;

import java.util.*;
import com.almasb.fxgl.logging.Logger;
import java.util.stream.Collectors;

/**
 * Manages physics entities, collision handling and performs the physics tick.
 * <p>
 * Contains several static and instance methods
 * to convert pixels coordinates to meters and vice versa.
 * <p>
 * Collision handling unifies how they are processed.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
public final class PhysicsWorld implements GameWorldListener {

    private static final Logger log = FXGLLoggerOld.getLogger("FXGL.PhysicsWorld");

    private static final double PIXELS_PER_METER = FXGL.getDouble("physics.ppm");
    private static final double METERS_PER_PIXELS = 1 / PIXELS_PER_METER;

    private World physicsWorld = new World(new Vec2(0, -10));

    private ParticleSystem particleSystem = physicsWorld.getParticleSystem();

    private List<Entity> entities = new ArrayList<>();

    private List<CollisionHandler> collisionHandlers = new ArrayList<>();

    private Map<CollisionPair, Long> collisions = new HashMap<>();

    private LongProperty tick = new SimpleLongProperty(0);

    private double appHeight;

    /**
     * Note: certain modifications to the jbox2d world directly may not be
     * recognized by FXGL.
     *
     * @return raw jbox2d physics world
     */
    public World getJBox2DWorld() {
        return physicsWorld;
    }

    private boolean isCollidable(Entity e) {
        return e.getComponent(CollidableComponent.class)
                .map(CollidableComponent::getValue)
                .orElse(false);
    }

    private boolean areCollidable(Entity e1, Entity e2) {
        return isCollidable(e1) && isCollidable(e2);
    }

    private boolean supportsPhysics(Entity e) {
        return e.hasComponent(PhysicsComponent.class);
    }

    private int getHandlerIndex(Entity e1, Entity e2) {
        return collisionHandlers.indexOf(new Pair<>(e1.getComponentUnsafe(TypeComponent.class).getValue(),
                e2.getComponentUnsafe(TypeComponent.class).getValue()));
    }

    @Inject
    protected PhysicsWorld(@Named("appHeight") double appHeight) {
        this.appHeight = appHeight;

        initContactListener();
        initParticles();

        log.finer("Physics world initialized");
    }

    /**
     * Registers contact listener to JBox2D world so that collisions are
     * registered for subsequent notification.
     * Only collidable entities are checked.
     */
    private void initContactListener() {
        physicsWorld.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Entity e1 = (Entity) contact.getFixtureA().getBody().getUserData();
                Entity e2 = (Entity) contact.getFixtureB().getBody().getUserData();

                if (!areCollidable(e1, e2))
                    return;

                int index = getHandlerIndex(e1, e2);
                if (index != -1) {
                    CollisionPair pair = new CollisionPair(e1, e2, collisionHandlers.get(index));

                    if (!collisions.containsKey(pair)) {
                        collisions.put(pair, tick.get());

                        HitBox boxA = (HitBox)contact.getFixtureA().getUserData();
                        HitBox boxB = (HitBox)contact.getFixtureB().getUserData();

                        pair.getHandler().onHitBoxTrigger(pair.getA(), pair.getB(),
                                e1 == pair.getA() ? boxA : boxB,
                                e2 == pair.getB() ? boxB : boxA);
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
                Entity e1 = (Entity) contact.getFixtureA().getBody().getUserData();
                Entity e2 = (Entity) contact.getFixtureB().getBody().getUserData();

                if (!areCollidable(e1, e2))
                    return;

                int index = getHandlerIndex(e1, e2);
                if (index != -1) {
                    CollisionPair pair = new CollisionPair(e1, e2, collisionHandlers.get(index));

                    if (collisions.containsKey(pair)) {
                        collisions.put(pair, -1L);
                    }
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }

    private void initParticles() {
        physicsWorld.setParticleGravityScale(0.4f);
        physicsWorld.setParticleDensity(1.2f);
        physicsWorld.setParticleRadius(toMeters(1));    // 0.5 for super realistic effect, but slow
    }

    @Override
    public void onWorldUpdate(double tpf) {

    }

    @Override
    public void onWorldReset() {

    }

    @Override
    public void onEntityAdded(Entity entity) {
        addEntity(entity);
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        removeEntity(entity);
    }

    /**
     * Physics tick.
     *
     * @param event update event
     */
    public void update(UpdateEvent event) {
        tick.set(event.tick());
        physicsWorld.step((float) event.tpf(), 8, 3);

        checkCollisions();
        notifyCollisionHandlers();
    }

    /**
     * Perform collision detection for all entities that have
     * setCollidable(true) and if at least one entity is not PhysicsEntity.
     * Subsequently fire collision handlers for all entities that have
     * setCollidable(true).
     */
    private void checkCollisions() {
        List<Entity> collidables = entities.stream()
                .filter(this::isCollidable)
                .collect(Collectors.toList());

        for (int i = 0; i < collidables.size(); i++) {
            Entity e1 = collidables.get(i);

            for (int j = i + 1; j < collidables.size(); j++) {
                Entity e2 = collidables.get(j);

                // if both are physics objects, let JBox2D handle collision checks
                // unless one is kinematic and the other is static
                if (supportsPhysics(e1) && supportsPhysics(e2)) {
                    PhysicsComponent p1 = e1.getComponentUnsafe(PhysicsComponent.class);
                    PhysicsComponent p2 = e2.getComponentUnsafe(PhysicsComponent.class);

                    boolean skip = true;
                    if ((p1.body.getType() == BodyType.KINEMATIC && p2.body.getType() == BodyType.STATIC)
                            || (p2.body.getType() == BodyType.KINEMATIC && p1.body.getType() == BodyType.STATIC)) {
                        skip = false;
                    }
                    if (skip)
                        continue;
                }

                int index = getHandlerIndex(e1, e2);
                if (index != -1) {
                    CollisionPair pair = new CollisionPair(e1, e2, collisionHandlers.get(index));

                    CollisionResult result = e1.getComponentUnsafe(BoundingBoxComponent.class)
                            .checkCollision(e2.getComponentUnsafe(BoundingBoxComponent.class));

                    if (result.hasCollided()) {
                        if (!collisions.containsKey(pair)) {
                            collisions.put(pair, tick.get());
                            pair.getHandler().onHitBoxTrigger(pair.getA(), pair.getB(), result.getBoxA(), result.getBoxB());
                        }
                    } else {
                        if (collisions.containsKey(pair)) {
                            collisions.put(pair, -1L);
                        }
                    }
                }
            }
        }
    }

    /**
     * Fires collisions handlers' callbacks based on currently registered collisions.
     */
    private void notifyCollisionHandlers() {
        List<CollisionPair> toRemove = new ArrayList<>();
        collisions.forEach((pair, cachedTick) -> {
            if (!pair.getA().isActive() || !pair.getB().isActive()
                    || !isCollidable(pair.getA()) || !isCollidable(pair.getB())) {
                toRemove.add(pair);
                return;
            }

            if (cachedTick == -1L) {
                pair.getHandler().onCollisionEnd(pair.getA(), pair.getB());
                toRemove.add(pair);
            } else if (tick.get() == cachedTick) {
                pair.getHandler().onCollisionBegin(pair.getA(), pair.getB());
            } else if (tick.get() > cachedTick) {
                pair.getHandler().onCollision(pair.getA(), pair.getB());
            }
        });

        toRemove.forEach(collisions::remove);
    }

    /**
     * Registers a collision handler.
     * The order in which the types are passed to this method
     * decides the order of objects being passed into the collision handler
     * <p>
     * <pre>
     * Example:
     * PhysicsWorld physics = ...
     * physics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.ENEMY) {
     *      public void onCollisionBegin(Entity a, Entity b) {
     *          // called when entities start touching
     *      }
     *      public void onCollision(Entity a, Entity b) {
     *          // called when entities are touching
     *      }
     *      public void onCollisionEnd(Entity a, Entity b) {
     *          // called when entities are separated and no longer touching
     *      }
     * });
     *
     * </pre>
     *
     * @param handler collision handler
     */
    public void addCollisionHandler(CollisionHandler handler) {
        collisionHandlers.add(handler);
    }

    /**
     * Removes a collision handler
     *
     * @param handler collision handler to remove
     */
    public void removeCollisionHandler(CollisionHandler handler) {
        collisionHandlers.remove(handler);
    }

    private void addEntity(Entity entity) {
        entities.add(entity);

        if (entity.hasComponent(PhysicsComponent.class)) {
            createBody(entity);
        } else if (entity.hasComponent(PhysicsParticleComponent.class)) {
            createPhysicsParticles(entity);
        }
    }

    private void removeEntity(Entity entity) {
        entities.remove(entity);

        if (entity.hasComponent(PhysicsComponent.class)) {
            destroyBody(entity);
        }
    }

    /**
     * Set global world gravity.
     *
     * @param x x component
     * @param y y component
     */
    public void setGravity(double x, double y) {
        physicsWorld.setGravity(new Vec2().addLocal((float) x, -(float) y));
    }

    /**
     * Create physics body and attach to physics world.
     *
     * @param e physics entity
     */
    private void createBody(Entity e) {
        BoundingBoxComponent bbox = Entities.getBBox(e);
        PhysicsComponent physics = Entities.getPhysics(e);

        double w = bbox.getWidth(),
                h = bbox.getHeight();

        // if position is 0, 0 then probably not set, so set ourselves
        if (physics.bodyDef.getPosition().x == 0 && physics.bodyDef.getPosition().y == 0) {
            physics.bodyDef.getPosition().set(toMeters(bbox.getMinXWorld() + w / 2),
                    toMeters(appHeight - (bbox.getMinYWorld() + h / 2)));
        }

        if (physics.bodyDef.getAngle() == 0) {
            physics.bodyDef.setAngle((float) -Math.toRadians(Entities.getRotation(e).getValue()));
        }

        physics.body = physicsWorld.createBody(physics.bodyDef);

        createFixtures(e);

        physics.body.setUserData(e);
        physics.onInitPhysics();

        e.addControl(new PhysicsControl(appHeight));
    }

    private void createFixtures(Entity e) {
        BoundingBoxComponent bbox = Entities.getBBox(e);
        PhysicsComponent physics = Entities.getPhysics(e);
        PositionComponent position = Entities.getPosition(e);

        Point2D entityCenter = bbox.getCenterWorld();

        for (HitBox box : bbox.hitBoxesProperty()) {
            Bounds bounds = box.translate(position.getX(), position.getY());

            // take world center bounds and subtract from entity center (all in pixels) to get local center
            Point2D boundsCenter = new Point2D((bounds.getMinX() + bounds.getMaxX()) / 2, (bounds.getMinY() + bounds.getMaxY()) / 2);
            Point2D boundsCenterLocal = boundsCenter.subtract(entityCenter);

            double w = bounds.getWidth();
            double h = bounds.getHeight();

            FixtureDef fd = physics.fixtureDef;

            Shape shape = null;

            if (box.getShape() == BoundingShape.BOX) {
                PolygonShape rectShape = new PolygonShape();
                rectShape.setAsBox(toMeters(w / 2), toMeters(h / 2),
                        new Vec2(toMeters(boundsCenterLocal.getX()), toMeters(boundsCenterLocal.getY())), 0);
                shape = rectShape;
            } else if (box.getShape() == BoundingShape.CIRCLE) {
                CircleShape circleShape = new CircleShape();
                circleShape.setRadius(toMeters(w / 2));
                circleShape.m_p.set(toMeters(boundsCenterLocal.getX()), toMeters(boundsCenterLocal.getY()));
                shape = circleShape;
            } else {
                log.warning("Unknown hit box shape: " + box.getShape());
            }

            // we use definitions from user, but override shape
            fd.setShape(shape);

            Fixture fixture = physics.body.createFixture(fd);
            fixture.setUserData(box);
        }
    }

    private void createPhysicsParticles(Entity e) {
        double x = Entities.getPosition(e).getX();
        double y = Entities.getPosition(e).getY();
        double width = Entities.getBBox(e).getWidth();
        double height = Entities.getBBox(e).getHeight();

        ParticleGroupDef def = e.getComponentUnsafe(PhysicsParticleComponent.class).getDefinition();
        def.setPosition(toMeters(x + width / 2), toMeters(appHeight - (y + height / 2)));

        Shape shape = null;

        BoundingBoxComponent bbox = Entities.getBBox(e);
        if (!bbox.hitBoxesProperty().isEmpty()) {
            if (bbox.hitBoxesProperty().get(0).getShape() == BoundingShape.BOX) {
                PolygonShape rectShape = new PolygonShape();
                rectShape.setAsBox(toMeters(width / 2), toMeters(height / 2));
                shape = rectShape;
            } else if (bbox.hitBoxesProperty().get(0).getShape() == BoundingShape.CIRCLE) {
                CircleShape circleShape = new CircleShape();
                circleShape.setRadius(toMeters(width / 2));
                shape = circleShape;
            } else {
                log.warning("Unknown hit box shape: " + bbox.hitBoxesProperty().get(0).getShape());
            }
        }

        if (shape == null) {
            PolygonShape rectShape = new PolygonShape();
            rectShape.setAsBox(toMeters(width / 2), toMeters(height / 2));
            shape = rectShape;
        }
        def.setShape(shape);

        ParticleGroup particleGroup = physicsWorld.createParticleGroup(def);

        Color color = e.getComponentUnsafe(PhysicsParticleComponent.class).getColor();
        e.addControl(new PhysicsParticleControl(particleGroup, color));
    }

    /**
     * Destroy body and remove from physics world.
     *
     * @param e physics entity
     */
    private void destroyBody(Entity e) {
        physicsWorld.destroyBody(Entities.getPhysics(e).body);
    }

    private EdgeCallback raycastCallback = new EdgeCallback();

    /**
     * Performs a ray cast from start point to end point.
     *
     * @param start start point
     * @param end end point
     * @return ray cast result
     */
    public RaycastResult raycast(Point2D start, Point2D end) {
        raycastCallback.reset();
        physicsWorld.raycast(raycastCallback, toPoint(start), toPoint(end));

        Entity entity = null;
        Point2D point = null;

        if (raycastCallback.fixture != null)
            entity = (Entity) raycastCallback.fixture.getBody().getUserData();

        if (raycastCallback.point != null)
            point = toPoint(raycastCallback.point);

        return new RaycastResult(Optional.ofNullable(entity), Optional.ofNullable(point));
    }

    /**
     * Converts pixels to meters
     *
     * @param pixels value in pixels
     * @return value in meters
     */
    public static float toMeters(double pixels) {
        return (float) (pixels * METERS_PER_PIXELS);
    }

    /**
     * Converts meters to pixels
     *
     * @param meters value in meters
     * @return value in pixels
     */
    public static float toPixels(double meters) {
        return (float) (meters * PIXELS_PER_METER);
    }

    /**
     * Converts a vector of type Point2D to vector of type Vec2
     *
     * @param v vector in pixels
     * @return vector in meters
     */
    public static Vec2 toVector(Point2D v) {
        return new Vec2(toMeters(v.getX()), toMeters(-v.getY()));
    }

    /**
     * Converts a vector of type Vec2 to vector of type Point2D
     *
     * @param v vector in meters
     * @return vector in pixels
     */
    public static Point2D toVector(Vec2 v) {
        return new Point2D(toPixels(v.x), toPixels(-v.y));
    }

    /**
     * Converts a point in pixel space to a point in physics space.
     *
     * @param p point in pixel space
     * @return point in physics space
     */
    public Vec2 toPoint(Point2D p) {
        return new Vec2(toMeters(p.getX()), toMeters(appHeight - p.getY()));
    }

    /**
     * Converts a point in physics space to a point in pixel space.
     *
     * @param p point in physics space
     * @return point in pixel space
     */
    public Point2D toPoint(Vec2 p) {
        return new Point2D(toPixels(p.x), toPixels(toMeters(appHeight) - p.y));
    }

    private static class EdgeCallback implements RayCastCallback {
        Fixture fixture;
        Vec2 point;
        //Vec2 normal;
        float bestFraction = 1.0f;

        @Override
        public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
            Entity e = (Entity) fixture.getBody().getUserData();
            if (e.getComponentUnsafe(PhysicsComponent.class).isRaycastIgnored())
                return 1;

            if (fraction < bestFraction) {
                this.fixture = fixture;
                this.point = point.clone();
                //this.normal = normal.clone();
                bestFraction = fraction;
            }

            return bestFraction;
        }

        void reset() {
            fixture = null;
            point = null;
            bestFraction = 1.0f;
        }
    }

    /**
     * The difference between physics and normal particle entity is that
     * the former is managed (controlled) by the physics world, the latter
     * by the particle emitters.
     */
    public final class PhysicsParticleControl extends ParticleControl {
        private ParticleGroup group;
        private double radiusMeters, radiusPixels;
        private Color color;

        private PhysicsParticleControl(ParticleGroup group, Color color) {
            this.group = group;
            this.color = color;

            radiusMeters = particleSystem.getParticleRadius();
            radiusPixels = toPixels(radiusMeters);
        }

        @Override
        public void onUpdate(Entity entity, double tpf) {
            this.particles.clear();

            Vec2[] centers = particleSystem.getParticlePositionBuffer();

            for (int i = group.getBufferIndex(); i < group.getBufferIndex() + group.getParticleCount(); i++) {
                Vec2 center = centers[i];

                double x = toPixels(center.x - radiusMeters);
                double y = toPixels(toMeters(appHeight) - center.y - radiusMeters);

                this.particles.add(new PhysicsParticle(new Point2D(x, y), radiusPixels, color));
            }
        }

        @Override
        public void onRemoved(Entity entity) {
            physicsWorld.destroyParticlesInGroup(group);
            super.onRemoved(entity);
        }
    }

    private class PhysicsParticle extends Particle {
        PhysicsParticle(Point2D position, double radius, Paint color) {
            super(position, Point2D.ZERO, Point2D.ZERO, radius, Point2D.ZERO,
                    Duration.seconds(10), color, BlendMode.SRC_OVER);

        }
    }
}
