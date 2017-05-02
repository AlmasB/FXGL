/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.pool.Pool;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.EntityWorldListener;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.TypeComponent;
import com.almasb.fxgl.service.Pooler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import com.almasb.fxgl.physics.box2d.callbacks.ContactImpulse;
import com.almasb.fxgl.physics.box2d.callbacks.ContactListener;
import com.almasb.fxgl.physics.box2d.collision.Manifold;
import com.almasb.fxgl.physics.box2d.collision.shapes.*;
import com.almasb.fxgl.physics.box2d.dynamics.*;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.Contact;
import com.almasb.fxgl.physics.box2d.particle.ParticleGroup;
import com.almasb.fxgl.physics.box2d.particle.ParticleGroupDef;

import java.util.Iterator;

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
public final class PhysicsWorld implements EntityWorldListener, ContactListener {

    private static final Logger log = FXGL.getLogger("FXGL.PhysicsWorld");

    private final double PIXELS_PER_METER;
    private final double METERS_PER_PIXELS;

    private World jboxWorld = new World(new Vec2(0, -10));

    private Array<Entity> entities = new Array<>(false, 128);

    private Array<CollisionHandler> collisionHandlers = new Array<>(false, 16);

    private Array<CollisionPair> collisions = new Array<>(false, 128);

    private int appHeight;

    private Pooler pooler = FXGL.getPooler();

    /**
     * Note: certain modifications to the jbox2d world directly may not be
     * recognized by FXGL.
     *
     * @return raw jbox2d physics world
     */
    public World getJBox2DWorld() {
        return jboxWorld;
    }

    public int getAppHeight() {
        return appHeight;
    }

    private boolean isCollidable(Entity e) {
        if (!e.isActive())
            return false;

        CollidableComponent collidable = e.getComponentUnsafe(CollidableComponent.class);

        return collidable != null && collidable.getValue();
    }

    private boolean areCollidable(Entity e1, Entity e2) {
        return isCollidable(e1) && isCollidable(e2);
    }

    private boolean needManualCheck(Entity e1, Entity e2) {
        // if no physics -> check manually
        PhysicsComponent p1 = e1.getComponentUnsafe(PhysicsComponent.class);
        if (p1 == null)
            return true;

        PhysicsComponent p2 = e2.getComponentUnsafe(PhysicsComponent.class);
        if (p2 == null)
            return true;

        // if one is kinematic and the other is static -> check manually
        return (p1.body.getType() == BodyType.KINEMATIC && p2.body.getType() == BodyType.STATIC)
                || (p2.body.getType() == BodyType.KINEMATIC && p1.body.getType() == BodyType.STATIC);
    }

    /**
     * @param e1 entity 1
     * @param e2 entity 2
     * @return collision handler for e1 and e2 based on their types or null if no such handler exists
     */
    private CollisionHandler getHandler(Entity e1, Entity e2) {
        if (!e1.isActive() || !e2.isActive())
            return null;

        Object type1 = e1.getComponentUnsafe(TypeComponent.class).getValue();
        Object type2 = e2.getComponentUnsafe(TypeComponent.class).getValue();

        for (CollisionHandler handler : collisionHandlers) {
            if (handler.equal(type1, type2)) {
                return handler;
            }
        }

        return null;
    }

    private CollisionPair getPair(Entity e1, Entity e2) {
        int index = getPairIndex(e1, e2);

        return index == -1 ? null : collisions.get(index);
    }

    private int getPairIndex(Entity e1, Entity e2) {
        for (int i = 0; i < collisions.size(); i++) {
            CollisionPair pair = collisions.get(i);
            if (pair.equal(e1, e2)) {
                return i;
            }
        }

        return -1;
    }

    @Inject
    protected PhysicsWorld(@Named("appHeight") int appHeight,
                           @Named("physics.ppm") double ppm) {
        this.appHeight = appHeight;

        PIXELS_PER_METER = ppm;
        METERS_PER_PIXELS = 1 / PIXELS_PER_METER;

        initCollisionPool();
        initContactListener();
        initParticles();

        log.debugf("Physics world initialized: appHeight=%d, physics.ppm=%.1f",
                appHeight, ppm);
    }

    private void initCollisionPool() {
        pooler.registerPool(CollisionPair.class, new Pool<CollisionPair>() {
            @Override
            protected CollisionPair newObject() {
                return new CollisionPair();
            }
        });
    }

    /**
     * Registers contact listener to JBox2D world so that collisions are
     * registered for subsequent notification.
     * Only collidable entities are checked.
     */
    private void initContactListener() {
        jboxWorld.setContactListener(this);
    }

    private void initParticles() {
        jboxWorld.setParticleGravityScale(0.4f);
        jboxWorld.setParticleDensity(1.2f);
        jboxWorld.setParticleRadius(toMeters(1));    // 0.5 for super realistic effect, but slow
    }

    private Array<Entity> delayedBodiesAdd = new Array<>(false, 16);
    private Array<Entity> delayedParticlesAdd = new Array<>(false, 16);
    private Array<Body> delayedBodiesRemove = new Array<>(false, 16);

    @Override
    public void onEntityAdded(Entity entity) {
        entities.add(entity);

        if (entity.hasComponent(PhysicsComponent.class)) {
            onPhysicsEntityAdded(entity);
        } else if (entity.hasComponent(PhysicsParticleComponent.class)) {
            onPhysicsParticleEntityAdded(entity);
        }
    }

    private void onPhysicsEntityAdded(Entity entity) {
        if (!jboxWorld.isLocked()) {
            createBody(entity);
        } else {
            delayedBodiesAdd.add(entity);
        }
    }

    private void onPhysicsParticleEntityAdded(Entity entity) {
        if (!jboxWorld.isLocked()) {
            createPhysicsParticles(entity);
        } else {
            delayedParticlesAdd.add(entity);
        }
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        entities.removeValue(entity, true);

        if (entity.hasComponent(PhysicsComponent.class)) {
            onPhysicsEntityRemoved(entity);
        }
    }

    private void onPhysicsEntityRemoved(Entity entity) {
        if (!jboxWorld.isLocked()) {
            destroyBody(entity);
        } else {
            delayedBodiesRemove.add(Entities.getPhysics(entity).getBody());
        }
    }

    @Override
    public void onWorldUpdate(double tpf) {
        jboxWorld.step((float) tpf, 8, 3);
        postStep();

        checkCollisions();
        notifyCollisions();
    }

    private void postStep() {
        for (Entity e : delayedBodiesAdd)
            createBody(e);

        delayedBodiesAdd.clear();

        for (Entity e : delayedParticlesAdd)
            createPhysicsParticles(e);

        delayedParticlesAdd.clear();

        for (Body body : delayedBodiesRemove)
            jboxWorld.destroyBody(body);

        delayedBodiesRemove.clear();
    }

    /**
     * Resets physics world.
     */
    @Override
    public void onWorldReset() {
        log.debug("Resetting physics world");

        entities.clear();
        collisions.clear();
        collisionHandlers.clear();
    }

    @Override
    public void beginContact(Contact contact) {
        Entity e1 = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity e2 = (Entity) contact.getFixtureB().getBody().getUserData();

        if (!areCollidable(e1, e2))
            return;

        CollisionHandler handler = getHandler(e1, e2);
        if (handler != null) {

            CollisionPair pair = getPair(e1, e2);

            // no collision registered, so add the pair
            if (pair == null) {
                pair = pooler.get(CollisionPair.class);
                pair.init(e1, e2, handler);

                // add pair to list of collisions so we still use it
                collisions.add(pair);

                HitBox boxA = (HitBox)contact.getFixtureA().getUserData();
                HitBox boxB = (HitBox)contact.getFixtureB().getUserData();

                handler.onHitBoxTrigger(pair.getA(), pair.getB(),
                        e1 == pair.getA() ? boxA : boxB,
                        e2 == pair.getB() ? boxB : boxA);

                pair.collisionBegin();
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Entity e1 = (Entity) contact.getFixtureA().getBody().getUserData();
        Entity e2 = (Entity) contact.getFixtureB().getBody().getUserData();

        if (!areCollidable(e1, e2))
            return;

        CollisionHandler handler = getHandler(e1, e2);
        if (handler != null) {

            int pairIndex = getPairIndex(e1, e2);

            // collision registered, so remove it and put pair back to pool
            if (pairIndex != -1) {
                CollisionPair pair = collisions.get(pairIndex);

                collisions.removeIndex(pairIndex);
                pair.collisionEnd();
                pooler.put(pair);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // no default implementation
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // no default implementation
    }

    private Array<Entity> collidables = new Array<>(false, 128);

    /**
     * Perform collision detection for all entities that have
     * setCollidable(true) and if at least one entity is not PhysicsEntity.
     * Subsequently fire collision handlers for all entities that have
     * setCollidable(true).
     */
    private void checkCollisions() {
        for (Entity e : entities) {
            if (isCollidable(e)) {
                collidables.add(e);
            }
        }

        for (int i = 0; i < collidables.size(); i++) {
            Entity e1 = collidables.get(i);

            for (int j = i + 1; j < collidables.size(); j++) {
                Entity e2 = collidables.get(j);

                CollisionHandler handler = getHandler(e1, e2);

                // if no handler registered, no need to check for this pair
                if (handler == null)
                    continue;

                // if no need for manual check, let jbox handle it
                if (!needManualCheck(e1, e2)) {
                    continue;
                }

                // check if colliding
                CollisionResult result = Entities.getBBox(e1).checkCollision(Entities.getBBox(e2));

                if (result.hasCollided()) {

                    collisionBeginFor(handler, e1, e2, result.getBoxA(), result.getBoxB());

                    // put result back to pool only if collided
                    pooler.put(result);
                } else {
                    collisionEndFor(e1, e2);
                }
            }
        }

        collidables.clear();
    }

    private void collisionBeginFor(CollisionHandler handler, Entity e1, Entity e2, HitBox a, HitBox b) {
        CollisionPair pair = getPair(e1, e2);

        // null means e1 and e2 were not colliding before
        // if not null, then ignore because e1 and e2 are still colliding
        if (pair == null) {
            pair = pooler.get(CollisionPair.class);
            pair.init(e1, e2, handler);

            // add pair to list of collisions so we still use it
            collisions.add(pair);

            handler.onHitBoxTrigger(pair.getA(), pair.getB(), a, b);
            pair.collisionBegin();
        }
    }

    private void collisionEndFor(Entity e1, Entity e2) {
        int pairIndex = getPairIndex(e1, e2);

        // if not -1, then collision registered, so end the collision
        // and remove it and put pair back to pool
        // if -1 then collision was not present before either
        if (pairIndex != -1) {
            CollisionPair pair = collisions.get(pairIndex);

            collisions.removeIndex(pairIndex);
            pair.collisionEnd();
            pooler.put(pair);
        }
    }

    /**
     * Fires all collision handlers' collision() callback based on currently registered collisions.
     */
    private void notifyCollisions() {
        for (Iterator<CollisionPair> it = collisions.iterator(); it.hasNext(); ) {
            CollisionPair pair = it.next();

            // if a pair no longer qualifies for collision then just remove it
            if (!pair.getA().isActive() || !pair.getB().isActive()
                    || !isCollidable(pair.getA()) || !isCollidable(pair.getB())) {

                it.remove();
                pooler.put(pair);
                continue;
            }

            pair.collision();
        }
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
        collisionHandlers.removeValue(handler, true);
    }

    /**
     * Set global world gravity.
     *
     * @param x x component
     * @param y y component
     */
    public void setGravity(double x, double y) {
        jboxWorld.setGravity(new Vec2(x, -y));
    }

    /**
     * Create physics body and attach to physics world.
     *
     * @param e physics entity
     */
    private void createBody(Entity e) {
        BoundingBoxComponent bbox = Entities.getBBox(e);
        PhysicsComponent physics = Entities.getPhysics(e);

        double w = bbox.getWidth();
        double h = bbox.getHeight();

        // if position is 0, 0 then probably not set, so set ourselves
        if (physics.bodyDef.getPosition().x == 0 && physics.bodyDef.getPosition().y == 0) {
            physics.bodyDef.getPosition().set(toMeters(bbox.getMinXWorld() + w / 2),
                    toMeters(appHeight - (bbox.getMinYWorld() + h / 2)));
        }

        if (physics.bodyDef.getAngle() == 0) {
            physics.bodyDef.setAngle((float) -Math.toRadians(Entities.getRotation(e).getValue()));
        }

        physics.body = jboxWorld.createBody(physics.bodyDef);

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
            Point2D boundsCenterWorld = new Point2D((bounds.getMinX() + bounds.getMaxX()) / 2, (bounds.getMinY() + bounds.getMaxY()) / 2);
            Point2D boundsCenterLocal = boundsCenterWorld.subtract(entityCenter);

            double w = bounds.getWidth();
            double h = bounds.getHeight();

            FixtureDef fd = physics.fixtureDef;

            Shape b2Shape;
            BoundingShape boundingShape = box.getShape();

            switch (boundingShape.type) {
                case CIRCLE:

                    CircleShape circleShape = new CircleShape();
                    circleShape.setRadius(toMeters(w / 2));
                    circleShape.m_p.set(toMeters(boundsCenterLocal.getX()), toMeters(boundsCenterLocal.getY()));

                    b2Shape = circleShape;
                    break;

                case POLYGON:
                    PolygonShape polygonShape = new PolygonShape();
                    polygonShape.setAsBox(toMeters(w / 2), toMeters(h / 2),
                            new Vec2(toMeters(boundsCenterLocal.getX()), toMeters(boundsCenterLocal.getY())), 0);
                    b2Shape = polygonShape;
                    break;

                case CHAIN:

                    if (physics.body.getType() != BodyType.STATIC) {
                        throw new IllegalArgumentException("BoundingShape.chain() can only be used with static objects");
                    }

                    Point2D[] points = (Point2D[]) boundingShape.data;

                    Vec2[] vertices = new Vec2[points.length];

                    for (int i = 0; i < vertices.length; i++) {
                        vertices[i] = toVector(points[i].subtract(boundsCenterLocal))
                                .subLocal(toVector(bbox.getCenterLocal()));
                    }

                    ChainShape chainShape = new ChainShape();
                    chainShape.createLoop(vertices, vertices.length);
                    b2Shape = chainShape;
                    break;

                case EDGE:
                default:
                    log.warning("Unsupported hit box shape");
                    throw new UnsupportedOperationException("Using unsupported shape: " + boundingShape.type);
            }

            // we use definitions from user, but override shape
            fd.setShape(b2Shape);

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
            if (bbox.hitBoxesProperty().get(0).getShape().type == ShapeType.POLYGON) {
                PolygonShape rectShape = new PolygonShape();
                rectShape.setAsBox(toMeters(width / 2), toMeters(height / 2));
                shape = rectShape;
            } else if (bbox.hitBoxesProperty().get(0).getShape().type == ShapeType.CIRCLE) {
                CircleShape circleShape = new CircleShape();
                circleShape.setRadius(toMeters(width / 2));
                shape = circleShape;
            } else {
                log.warning("Unknown hit box shape: " + bbox.hitBoxesProperty().get(0).getShape().type);
                throw new UnsupportedOperationException();
            }
        }

        if (shape == null) {
            PolygonShape rectShape = new PolygonShape();
            rectShape.setAsBox(toMeters(width / 2), toMeters(height / 2));
            shape = rectShape;
        }
        def.setShape(shape);

        ParticleGroup particleGroup = jboxWorld.createParticleGroup(def);

        Color color = e.getComponentUnsafe(PhysicsParticleComponent.class).getColor();
        e.addControl(new PhysicsParticleControl(particleGroup, color, this));
    }

    /**
     * Destroy body and remove from physics world.
     *
     * @param e physics entity
     */
    private void destroyBody(Entity e) {
        jboxWorld.destroyBody(Entities.getPhysics(e).body);
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
        jboxWorld.raycast(raycastCallback, toPoint(start), toPoint(end));

        Entity entity = null;
        Point2D point = null;

        if (raycastCallback.getFixture() != null)
            entity = (Entity) raycastCallback.getFixture().getBody().getUserData();

        if (raycastCallback.getPoint() != null)
            point = toPoint(raycastCallback.getPoint());

        if (entity == null && point == null)
            return RaycastResult.NONE;

        return new RaycastResult(entity, point);
    }

    /**
     * Converts pixels to meters
     *
     * @param pixels value in pixels
     * @return value in meters
     */
    public float toMeters(double pixels) {
        return (float) (pixels * METERS_PER_PIXELS);
    }

    /**
     * Converts meters to pixels
     *
     * @param meters value in meters
     * @return value in pixels
     */
    public float toPixels(double meters) {
        return (float) (meters * PIXELS_PER_METER);
    }

    /**
     * Converts a vector of type Point2D to vector of type Vec2
     *
     * @param v vector in pixels
     * @return vector in meters
     */
    public Vec2 toVector(Point2D v) {
        return new Vec2(toMeters(v.getX()), toMeters(-v.getY()));
    }

    /**
     * Converts a vector of type Vec2 to vector of type Point2D
     *
     * @param v vector in meters
     * @return vector in pixels
     */
    public Point2D toVector(Vec2 v) {
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
}
