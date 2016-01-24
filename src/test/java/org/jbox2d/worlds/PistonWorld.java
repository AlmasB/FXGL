/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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

/*******************************************************************************
 * Copyright (c) 2013, Daniel Murphy
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.jbox2d.worlds;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

public class PistonWorld implements PerformanceTestWorld {
    public float timeStep = 1f / 60;
    public int velIters = 8;
    public int posIters = 3;

    public RevoluteJoint m_joint1;
    public PrismaticJoint m_joint2;
    public World world;

    public PistonWorld() {
    }

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
                cd.m_radius = .5f;
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
            rjd.motorSpeed = MathUtils.PI;
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
                rjd.motorSpeed = 1.0f * MathUtils.PI;
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
                cd.m_radius = 0.36f;
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
                float delta = MathUtils.PI / 3.0f;
                Vec2 vertices[] = new Vec2[6];
                for (int i = 0; i < 6; ++i) {
                    vertices[i] = new Vec2(0.3f * MathUtils.cos(angle), 0.3f * MathUtils.sin(angle));
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
