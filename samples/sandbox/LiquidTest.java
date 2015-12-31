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
package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.effect.ParticleEmitters;
import com.almasb.fxgl.effect.ParticleEntity;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsEntity;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.particle.*;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public class LiquidTest extends GameApplication {

    // TODO: easy way of creating screen bounds given rectangle2d?

    private enum Type implements EntityType {
        BOUNDS, CANVAS, WATER
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Liquid Test");
        settings.setVersion("0.1developer");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Spawn Box") {
            @Override
            protected void onActionBegin() {
                // 1. create physics entity
                PhysicsEntity box = new PhysicsEntity(Type.BOUNDS);
                box.setPosition(input.getMouse().getGameX(), input.getMouse().getGameY());

                // 2. set body type to dynamic for moving entities
                // not controlled by user
                box.setBodyType(BodyType.DYNAMIC);

                // 3. set various physics properties
                FixtureDef fd = new FixtureDef();
                fd.density = 0.5f;
                fd.restitution = 0.3f;
                box.setFixtureDef(fd);

                Rectangle rect = new Rectangle(50, 50);
                rect.setFill(Color.DARKRED);
                box.setSceneView(rect);

                //box.setRotation(35);

                //box.setOnPhysicsInitialized(() -> box.setAngularVelocity(35));

                //box.setOnPhysicsInitialized(() -> box.setLinearVelocity(new Point2D(100, 20).subtract(box.getCenter())));

                getGameWorld().addEntity(box);
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        getAudioPlayer().setGlobalSoundVolume(0);

        initScreenBounds();

        PhysicsEntity ground = new PhysicsEntity(Type.BOUNDS);
        ground.setSceneView(new Rectangle(200, 50));
        ground.setPosition(50, 300);

        canvas = new Canvas(800, 600);

        Entity canvasEntity = new Entity(Type.CANVAS);
        canvasEntity.setSceneView(canvas);

        getGameWorld().addEntities(ground);
        //getGameWorld().addEntity(canvasEntity);



        PhysicsWorld.PhysicsParticleEntity water = getPhysicsWorld().createLiquid(50, 10, 75, 150, Color.BLUE.brighter(), Type.WATER);

        PhysicsWorld.PhysicsParticleEntity water2 = getPhysicsWorld().createLiquid(200, 10, 35, 35, Color.DARKRED.brighter(), Type.WATER);


        //water.setExpireTime(Duration.seconds(10));

//        ParticleEntity water = new ParticleEntity(Type.WATER);
//        water.setEmitter(ParticleEmitters.newSparkEmitter());
//        water.setPosition(100, 100);

        getGameWorld().addEntities(water);
        getGameWorld().addEntities(water2);
    }

    private Canvas canvas;

    private void initScreenBounds() {
        PhysicsEntity top = new PhysicsEntity(Type.BOUNDS);
        top.setPosition(0, 0 - 100);
        top.setSceneView(new Rectangle(getWidth(), 100));

        PhysicsEntity bot = new PhysicsEntity(Type.BOUNDS);
        bot.setPosition(0, getHeight() - 100);
        bot.setSceneView(new Rectangle(getWidth(), 100));

        PhysicsEntity left = new PhysicsEntity(Type.BOUNDS);
        left.setPosition(0 - 100 + 10, 0);
        left.setSceneView(new Rectangle(100, getHeight()));

        PhysicsEntity right = new PhysicsEntity(Type.BOUNDS);
        right.setPosition(getWidth(), 0);
        right.setSceneView(new Rectangle(100, getHeight()));

        getGameWorld().addEntities(top, bot, left, right);
    }

    @Override
    protected void initPhysics() {
        initTest();


    }

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate() {
//        World world = getPhysicsWorld().getWorld();
//
//        ParticleSystem system = world.getParticleSystem();
//
//        int particleCount = system.getParticleCount();
//        if (particleCount != 0) {
//            float particleRadius = system.getParticleRadius();
//            Vec2[] positionBuffer = system.getParticlePositionBuffer();
//            ParticleColor[] colorBuffer = null;
//            if (system.m_colorBuffer.data != null) {
//                colorBuffer = system.getParticleColorBuffer();
//            }
//
//            drawParticles(positionBuffer, particleRadius, colorBuffer, particleCount);
//        }
    }

//    private void drawParticles(Vec2[] centers, float radiusMeters, ParticleColor[] colors, int count) {
//        double radius = PhysicsWorld.toPixels(radiusMeters);
//
//
//        GraphicsContext g = canvas.getGraphicsContext2D();
//
//        g.clearRect(0, 0, getWidth(), getHeight());
//
//        g.setFill(Color.BLUE);
//
//        for (int i = 0; i < count; i++) {
//            Point2D p = getPhysicsWorld().toPoint(centers[i]);
//
//            g.fillOval(p.getX() - radius, p.getY() - radius, radius * 2, radius * 2);
//        }
//    }

    public void initTest() {


//        World world = getPhysicsWorld().getWorld();
//
//        {
//            PolygonShape shape = new PolygonShape();
//            shape.setAsBox(PhysicsWorld.toMeters(15), PhysicsWorld.toMeters(70), getPhysicsWorld().toPoint(new Point2D(95, 50)), 0);
//
//
//            ParticleGroupDef pd = new ParticleGroupDef();
//            //pd.flags = ParticleType.b2_tensileParticle | ParticleType.b2_viscousParticle;
//            pd.flags = ParticleType.b2_waterParticle;
//            pd.shape = shape;
//            ParticleGroup particleGroup = world.createParticleGroup(pd);
//        }
//
//
//
//        {
//            PolygonShape shape = new PolygonShape();
//            shape.setAsBox(PhysicsWorld.toMeters(100), PhysicsWorld.toMeters(10), getPhysicsWorld().toPoint(new Point2D(135, 20)), 0);
//
//
//            ParticleGroupDef pd = new ParticleGroupDef();
//            //pd.flags = ParticleType.b2_tensileParticle | ParticleType.b2_viscousParticle;
//            pd.flags = ParticleType.b2_waterParticle;
//            pd.shape = shape;
//            world.createParticleGroup(pd);
//        }
//
//        {
//            PolygonShape shape = new PolygonShape();
//            shape.setAsBox(PhysicsWorld.toMeters(15), PhysicsWorld.toMeters(10), getPhysicsWorld().toPoint(new Point2D(130, 50)), 0);
//
//
//            ParticleGroupDef pd = new ParticleGroupDef();
//            //pd.flags = ParticleType.b2_tensileParticle | ParticleType.b2_viscousParticle;
//            pd.flags = ParticleType.b2_waterParticle;
//            pd.shape = shape;
//            world.createParticleGroup(pd);
//        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
