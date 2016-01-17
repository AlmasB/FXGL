///*
// * The MIT License (MIT)
// *
// * FXGL - JavaFX Game Library
// *
// * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package sandbox;
//
//import com.almasb.fxgl.app.ApplicationMode;
//import com.almasb.fxgl.app.GameApplication;
//import com.almasb.fxgl.entity.EntityType;
//import com.almasb.fxgl.entity.EntityView;
//import com.almasb.fxgl.input.ActionType;
//import com.almasb.fxgl.input.Input;
//import com.almasb.fxgl.input.InputMapping;
//import com.almasb.fxgl.input.OnUserAction;
//import com.almasb.fxgl.physics.BreakablePhysicsEntity;
//import com.almasb.fxgl.physics.HitBox;
//import com.almasb.fxgl.physics.PhysicsEntity;
//import com.almasb.fxgl.settings.GameSettings;
//import javafx.geometry.BoundingBox;
//import javafx.scene.input.KeyCode;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//import org.jbox2d.dynamics.BodyDef;
//import org.jbox2d.dynamics.BodyType;
//
///**
// * This is an example of a basic FXGL game application.
// *
// * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
// *
// */
//public class BreakableTest extends GameApplication {
//
//    private enum Type implements EntityType {
//        BOUNDS, BREAKABLE
//    }
//
//    @Override
//    protected void initSettings(GameSettings settings) {
//        settings.setWidth(800);
//        settings.setHeight(600);
//        settings.setTitle("Breakable Test");
//        settings.setVersion("0.1developer");
//        settings.setFullScreen(false);
//        settings.setIntroEnabled(false);
//        settings.setMenuEnabled(false);
//        settings.setShowFPS(true);
//        settings.setApplicationMode(ApplicationMode.DEVELOPER);
//    }
//
//    @Override
//    protected void initInput() {
//        Input input = getInput();
//
//        input.addInputMapping(new InputMapping("Break", KeyCode.K));
//    }
//
//    @Override
//    protected void initAssets() {}
//
//    BreakablePhysicsEntity entity;
//
//    @Override
//    protected void initGame() {
//        getAudioPlayer().setGlobalSoundVolume(0);
//
//        initScreenBounds();
//
//        entity = new BreakablePhysicsEntity(Type.BREAKABLE);
//        entity.setPosition(100, 50);
//        //entity.setBodyType(BodyType.DYNAMIC);
//
//        BodyDef bd = new BodyDef();
//        //bd.setAngle((float)Math.toRadians(90));
//        bd.setType(BodyType.DYNAMIC);
//
//        entity.setBodyDef(bd);
//        entity.addHitBox(new HitBox("BODY", new BoundingBox(0, 0, 40, 40)));
//        entity.addHitBox(new HitBox("BODY2", new BoundingBox(40, 0, 40, 40)));
//
//        EntityView view = new EntityView(entity);
//        view.addNode(new Rectangle(40, 40, Color.BLUE));
//
//        Rectangle r2 = new Rectangle(40, 40, Color.BLUE);
//        r2.setTranslateX(40);
//
//        view.addNode(r2);
//
//        entity.setGenerateHitBoxesFromView(false);
//        entity.setSceneView(view);
//
//        getGameWorld().addEntities(entity);
//    }
//
//    private void initScreenBounds() {
//        PhysicsEntity top = new PhysicsEntity(Type.BOUNDS);
//        top.setPosition(0, 0 - 100);
//        top.setSceneView(new Rectangle(getWidth(), 100));
//
//        PhysicsEntity bot = new PhysicsEntity(Type.BOUNDS);
//        bot.setPosition(0, getHeight() - 100);
//        bot.setSceneView(new Rectangle(getWidth(), 100));
//
//        PhysicsEntity left = new PhysicsEntity(Type.BOUNDS);
//        left.setPosition(0 - 100 + 10, 0);
//        left.setSceneView(new Rectangle(100, getHeight()));
//
//        PhysicsEntity right = new PhysicsEntity(Type.BOUNDS);
//        right.setPosition(getWidth(), 0);
//        right.setSceneView(new Rectangle(100, getHeight()));
//
//        PhysicsEntity ground = new PhysicsEntity(Type.BOUNDS);
//        ground.setSceneView(new Rectangle(50, 50));
//        ground.setPosition(50, 300);
//
//        getGameWorld().addEntities(top, bot, left, right, ground);
//    }
//
//    @Override
//    protected void initPhysics() {}
//
//    @Override
//    protected void initUI() {}
//
//    @Override
//    protected void onUpdate() {}
//
//    @OnUserAction(name = "Break", type = ActionType.ON_ACTION_BEGIN)
//    public void breakE() {
//        entity.breakIntoPieces();
//    }
//
//
////    Body m_body1;
////    Vec2 m_velocity = new Vec2();
////    float m_angularVelocity;
////    PolygonShape m_shape1;
////    PolygonShape m_shape2;
////    Fixture m_piece1;
////    Fixture m_piece2;
////
////    boolean m_broke;
////    boolean m_break;
////
////    @Override
////    public void initTest(boolean argDeserialized) {
////
////        // Breakable dynamic body
////        {
////            BodyDef bd = new BodyDef();
////            bd.type = BodyType.DYNAMIC;
////            bd.position.set(0.0f, 40.0f);
////            bd.angle = 0.25f * MathUtils.PI;
////            m_body1 = getWorld().createBody(bd);
////
////            m_shape1 = new PolygonShape();
////            m_shape1.setAsBox(0.5f, 0.5f, new Vec2(-0.5f, 0.0f), 0.0f);
////            m_piece1 = m_body1.createFixture(m_shape1, 1.0f);
////
////            m_shape2 = new PolygonShape();
////            m_shape2.setAsBox(0.5f, 0.5f, new Vec2(0.5f, 0.0f), 0.0f);
////            m_piece2 = m_body1.createFixture(m_shape2, 1.0f);
////        }
////
////        m_break = false;
////        m_broke = false;
////    }
////
////    @Override
////    public void postSolve(Contact contact, ContactImpulse impulse) {
////        if (m_broke) {
////            // The body already broke.
////            return;
////        }
////
////        // Should the body break?
////        int count = contact.getManifold().pointCount;
////
////        float maxImpulse = 0.0f;
////        for (int i = 0; i < count; ++i) {
////            maxImpulse = MathUtils.max(maxImpulse, impulse.normalImpulses[i]);
////        }
////
////        if (maxImpulse > 40.0f) {
////            // Flag the body for breaking.
////            m_break = true;
////        }
////    }
////
////    void Break() {
////        // Create two bodies from one.
////        Body body1 = m_piece1.getBody();
////        Vec2 center = body1.getWorldCenter();
////
////        body1.destroyFixture(m_piece2);
////        m_piece2 = null;
////
////        BodyDef bd = new BodyDef();
////        bd.type = BodyType.DYNAMIC;
////        bd.position = body1.getPosition();
////        bd.angle = body1.getAngle();
////
////        Body body2 = getWorld().createBody(bd);
////        m_piece2 = body2.createFixture(m_shape2, 1.0f);
////
////        // Compute consistent velocities for new bodies based on
////        // cached velocity.
////        Vec2 center1 = body1.getWorldCenter();
////        Vec2 center2 = body2.getWorldCenter();
////
////        Vec2 velocity1 = m_velocity.add(Vec2.cross(m_angularVelocity, center1.sub(center)));
////        Vec2 velocity2 = m_velocity.add(Vec2.cross(m_angularVelocity, center2.sub(center)));
////
////        body1.setAngularVelocity(m_angularVelocity);
////        body1.setLinearVelocity(velocity1);
////
////        body2.setAngularVelocity(m_angularVelocity);
////        body2.setLinearVelocity(velocity2);
////    }
////
////    @Override
////    public void step(TestbedSettings settings) {
////        super.step(settings);
////
////        if (m_break) {
////            Break();
////            m_broke = true;
////            m_break = false;
////        }
////
////        // Cache velocities to improve movement on breakage.
////        if (m_broke == false) {
////            m_velocity.set(m_body1.getLinearVelocity());
////            m_angularVelocity = m_body1.getAngularVelocity();
////        }
////    }
//
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
