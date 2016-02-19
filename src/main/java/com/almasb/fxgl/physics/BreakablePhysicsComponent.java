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

import com.almasb.ents.AbstractComponent;
import com.almasb.ents.Entity;
import com.almasb.ents.component.Required;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.ArrayList;
import java.util.List;

/**
 * API INCOMPLETE
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PhysicsComponent.class)
public class BreakablePhysicsComponent extends AbstractComponent {

    List<Fixture> fixtures = new ArrayList<>();

    public List<Entity> breakIntoPieces() {
        PhysicsComponent physicsComponent = getEntity().getComponentUnsafe(PhysicsComponent.class);

        Body body = physicsComponent.body;


        Vec2 linearVel = body.getLinearVelocity();
        float angularVel = body.getAngularVelocity();

        Vec2 worldCenter = body.getWorldCenter();

//        for (Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {
//            fixtures.add(f);
//        }

        fixtures.forEach(f -> {

            Body tmpBody = f.getBody();

            // TODO: do we always have polygon
            PolygonShape shape = (PolygonShape) f.getShape();


            tmpBody.destroyFixture(f);


            FixtureDef fd = new FixtureDef();


            // TODO: hardcoded values
            PolygonShape rectShape = new PolygonShape();
            rectShape.setAsBox(PhysicsWorld.toMeters(40 / 2), PhysicsWorld.toMeters(40 / 2));

            fd.setShape(rectShape);
            fd.setDensity(1);

            BodyDef bf = new BodyDef();
            bf.setType(BodyType.DYNAMIC);
            //bf.setPosition(tmpBody.getPosition().add(getCenter(shape.getVertices(), shape.getVertexCount())));
            bf.setPosition(tmpBody.getPosition().add(shape.centroid(new Transform())));
            bf.setAngle(tmpBody.getAngle());

            System.out.println(tmpBody.getAngle() + " angle");

            GameEntity entity = new GameEntity();

            PhysicsComponent p = new PhysicsComponent();
            p.setFixtureDef(fd);
            p.setBodyDef(bf);
            p.setOnPhysicsInitialized(() -> {
                Vec2 vel = linearVel.add(Vec2.cross(angularVel,
                        tmpBody.getWorldCenter().sub(p.body.getWorldCenter())));

                p.setBodyLinearVelocity(vel);
                p.setAngularVelocity(angularVel);
            });

            entity.addComponent(p);

            double radius = PhysicsWorld.toPixels(shape.getRadius());
            radius = 20;
            entity.getMainViewComponent().setView(new EntityView(new Rectangle(radius*2,
                    radius * 2, Color.RED)), true);

            System.out.println(radius);



            getEntity().getWorld().addEntity(entity);
        });

        getEntity().removeFromWorld();

        //        // Create two bodies from one.
//        Body body1 = m_piece1.getBody();
//        Vec2 center = body1.getWorldCenter();
//
//        body1.destroyFixture(m_piece2);
//        m_piece2 = null;
//
//        BodyDef bd = new BodyDef();
//        bd.type = BodyType.DYNAMIC;
//        bd.position = body1.getValue();
//        bd.angle = body1.getAngle();
//
//        Body body2 = getWorld().createBody(bd);
//        m_piece2 = body2.createFixture(m_shape2, 1.0f);
//
//        // Compute consistent velocities for new bodies based on
//        // cached velocity.
//        Vec2 center1 = body1.getWorldCenter();
//        Vec2 center2 = body2.getWorldCenter();
//
//        Vec2 velocity1 = m_velocity.add(Vec2.cross(m_angularVelocity, center1.sub(center)));
//        Vec2 velocity2 = m_velocity.add(Vec2.cross(m_angularVelocity, center2.sub(center)));
//
//        body1.setAngularVelocity(m_angularVelocity);
//        body1.setLinearVelocity(velocity1);
//
//        body2.setAngularVelocity(m_angularVelocity);
//        body2.setLinearVelocity(velocity2);






        return new ArrayList<>();
    }

    private Vec2 getCenter(Vec2[] vertices, int count) {
        float minX = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float minY =  Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;

        for (int i = 0; i < count; i++) {
            if (vertices[i].x < minX) {
                minX = vertices[i].x;
            }

            if (vertices[i].x > maxX) {
                maxX = vertices[i].x;
            }

            if (vertices[i].y < minY) {
                minY = vertices[i].y;
            }

            if (vertices[i].y > maxY) {
                maxY = vertices[i].y;
            }
        }

        return new Vec2((minX + maxX) / 2, (minY + maxY) / 2);
    }
}
