/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.worlds;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.physics.box2d.collision.shapes.CircleShape;
import com.almasb.fxgl.physics.box2d.collision.shapes.PolygonShape;
import com.almasb.fxgl.physics.box2d.common.JBoxUtils;
import com.almasb.fxgl.physics.box2d.dynamics.*;
import com.almasb.fxgl.physics.box2d.dynamics.joints.PrismaticJoint;
import com.almasb.fxgl.physics.box2d.dynamics.joints.PrismaticJointDef;
import com.almasb.fxgl.physics.box2d.dynamics.joints.RevoluteJoint;
import com.almasb.fxgl.physics.box2d.dynamics.joints.RevoluteJointDef;

public class PistonWorld implements PerformanceTestWorld {
    public float timeStep = 1f / 60;
    public int velIters = 8;
    public int posIters = 3;

    public RevoluteJoint m_joint1;
    public PrismaticJoint m_joint2;
    public World world;

    @Override
    public void setupWorld(World world) {
        this.world = world;
        Body ground = null;
        {
            BodyDef bd = new BodyDef();
            ground = world.createBody(bd);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(5.0f, 100.0f);
            bd = new BodyDef();
            bd.setType(BodyType.STATIC);
            FixtureDef sides = new FixtureDef();
            sides.setShape(shape);
            sides.setDensity(0);
            sides.setFriction(0);
            sides.setRestitution(0.8f);
            sides.getFilter().categoryBits = 4;
            sides.getFilter().maskBits = 2;

            bd.getPosition().set(-10.01f, 50.0f);
            Body bod = world.createBody(bd);
            bod.createFixture(sides);
            bd.getPosition().set(10.01f, 50.0f);
            bod = world.createBody(bd);
            bod.createFixture(sides);
        }

        // turney
        {
            CircleShape cd;
            FixtureDef fd = new FixtureDef();
            BodyDef bd = new BodyDef();
            bd.setType(BodyType.DYNAMIC);
            int numPieces = 5;
            float radius = 4f;
            bd.setPosition(new Vec2(0.0f, 25.0f));
            Body body = world.createBody(bd);
            for (int i = 0; i < numPieces; i++) {
                cd = new CircleShape();
                cd.setRadius(.5f);
                fd.setShape(cd);
                fd.setDensity(25);
                fd.setFriction(0.1f);
                fd.setRestitution(0.9f);
                float xPos = radius * (float) Math.cos(2f * Math.PI * (i / (float) (numPieces)));
                float yPos = radius * (float) Math.sin(2f * Math.PI * (i / (float) (numPieces)));
                cd.m_p.set(xPos, yPos);

                body.createFixture(fd);
            }

            RevoluteJointDef rjd = new RevoluteJointDef();
            rjd.initialize(body, ground, body.getPosition());
            rjd.motorSpeed = JBoxUtils.PI;
            rjd.maxMotorTorque = 1000000.0f;
            rjd.enableMotor = true;
            world.createJoint(rjd);
        }

        {
            Body prevBody = ground;

            // Define crank.
            {
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(0.5f, 2.0f);

                BodyDef bd = new BodyDef();
                bd.setType(BodyType.DYNAMIC);
                bd.getPosition().set(0.0f, 7.0f);
                Body body = world.createBody(bd);
                body.createFixture(shape, 2.0f);

                RevoluteJointDef rjd = new RevoluteJointDef();
                rjd.initialize(prevBody, body, new Vec2(0.0f, 5.0f));
                rjd.motorSpeed = 1.0f * JBoxUtils.PI;
                rjd.maxMotorTorque = 20000;
                rjd.enableMotor = true;
                m_joint1 = (RevoluteJoint) world.createJoint(rjd);

                prevBody = body;
            }

            // Define follower.
            {
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(0.5f, 4.0f);

                BodyDef bd = new BodyDef();
                bd.setType(BodyType.DYNAMIC);
                bd.getPosition().set(0.0f, 13.0f);
                Body body = world.createBody(bd);
                body.createFixture(shape, 2.0f);

                RevoluteJointDef rjd = new RevoluteJointDef();
                rjd.initialize(prevBody, body, new Vec2(0.0f, 9.0f));
                rjd.enableMotor = false;
                world.createJoint(rjd);

                prevBody = body;
            }

            // Define piston
            {
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(7f, 2f);

                BodyDef bd = new BodyDef();
                bd.setType(BodyType.DYNAMIC);
                bd.getPosition().set(0.0f, 17.0f);
                Body body = world.createBody(bd);
                FixtureDef piston = new FixtureDef();
                piston.setShape(shape);
                piston.setDensity(2);
                piston.getFilter().categoryBits = 1;
                piston.getFilter().maskBits = 2;
                body.createFixture(piston);

                RevoluteJointDef rjd = new RevoluteJointDef();
                rjd.initialize(prevBody, body, new Vec2(0.0f, 17.0f));
                world.createJoint(rjd);

                PrismaticJointDef pjd = new PrismaticJointDef();
                pjd.initialize(ground, body, new Vec2(0.0f, 17.0f), new Vec2(0.0f, 1.0f));

                pjd.maxMotorForce = 1000.0f;
                pjd.enableMotor = true;

                m_joint2 = (PrismaticJoint) world.createJoint(pjd);
            }

            // Create a payload
            {
                PolygonShape sd = new PolygonShape();
                BodyDef bd = new BodyDef();
                bd.setType(BodyType.DYNAMIC);
                FixtureDef fixture = new FixtureDef();
                Body body;
                for (int i = 0; i < 100; ++i) {
                    sd.setAsBox(0.4f, 0.3f);
                    bd.getPosition().set(-1.0f, 23.0f + i);

                    bd.setBullet(false);
                    body = world.createBody(bd);
                    fixture.setShape(sd);
                    fixture.setDensity(0.1f);
                    fixture.getFilter().categoryBits = 2;
                    fixture.getFilter().maskBits = 1 | 4 | 2;
                    body.createFixture(fixture);
                }

                CircleShape cd = new CircleShape();
                cd.setRadius(0.36f);
                for (int i = 0; i < 100; ++i) {
                    bd.getPosition().set(1.0f, 23.0f + i);
                    bd.setBullet(false);
                    fixture.setShape(cd);
                    fixture.setDensity(2);
                    fixture.getFilter().categoryBits = 2;
                    fixture.getFilter().maskBits = 1 | 4 | 2;
                    body = world.createBody(bd);
                    body.createFixture(fixture);
                }

                float angle = 0.0f;
                float delta = JBoxUtils.PI / 3.0f;
                Vec2 vertices[] = new Vec2[6];
                for (int i = 0; i < 6; ++i) {
                    vertices[i] = new Vec2(0.3f * JBoxUtils.cos(angle), 0.3f * JBoxUtils.sin(angle));
                    angle += delta;
                }

                PolygonShape shape = new PolygonShape();
                shape.set(vertices, 6);

                for (int i = 0; i < 100; ++i) {
                    bd.getPosition().set(0f, 23.0f + i);
                    bd.setType(BodyType.DYNAMIC);
                    bd.setFixedRotation(true);
                    bd.setBullet(false);
                    fixture.setShape(shape);
                    fixture.setDensity(1);
                    fixture.getFilter().categoryBits = 2;
                    fixture.getFilter().maskBits = 1 | 4 | 2;
                    body = world.createBody(bd);
                    body.createFixture(fixture);
                }
            }
        }
    }

    @Override
    public void step() {
        world.step(timeStep, posIters, velIters);
    }
}
