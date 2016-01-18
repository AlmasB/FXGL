///*
// * The MIT License (MIT)
// *
// * FXGL - JavaFX Game Library
// *
// * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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
//package sandbox;
//
//import com.almasb.fxgl.app.ApplicationMode;
//import com.almasb.fxgl.app.GameApplication;
//import com.almasb.fxgl.effect.ParticleEmitter;
//import com.almasb.fxgl.effect.ParticleEmitters;
//import com.almasb.fxgl.effect.ParticleEntity;
//import com.almasb.fxgl.entity.Entity;
//import com.almasb.fxgl.entity.EntityType;
//import com.almasb.fxgl.entity.control.AbstractControl;
//import com.almasb.fxgl.input.*;
//import com.almasb.fxgl.physics.CollisionHandler;
//import com.almasb.fxgl.settings.GameSettings;
//import com.almasb.fxgl.ui.UIFactory;
//import javafx.geometry.Point2D;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.MouseButton;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Text;
//import javafx.util.Duration;
//
//public class ParticleTest extends GameApplication {
//
//    private enum Type implements EntityType {
//        PARTICLES, LETTER, BOX
//    }
//
//    @Override
//    protected void initSettings(GameSettings settings) {
//        settings.setWidth(800);
//        settings.setHeight(600);
//        settings.setTitle("Basic FXGL Application");
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
//        // 2. add input mappings (action name -> trigger name)
//        input.addInputMapping(new InputMapping("Move Left", KeyCode.A));
//        input.addInputMapping(new InputMapping("Move Right", KeyCode.D));
//        input.addInputMapping(new InputMapping("Move Up", KeyCode.W));
//        input.addInputMapping(new InputMapping("Move Down", KeyCode.S));
//        input.addInputMapping(new InputMapping("Shoot", MouseButton.PRIMARY));
//
//        input.addAction(new UserAction("Spawn Explosion") {
//            @Override
//            protected void onActionBegin() {
//                ParticleEntity smoke = new ParticleEntity(Type.PARTICLES);
//                smoke.setPosition(input.getMouse().getGameX(), input.getMouse().getGameY());
//
//                ParticleEmitter emitter = ParticleEmitters.newSmokeEmitter();
//                emitter.setVelocityFunction((i, x, y) -> new Point2D(-5, 0));
//                emitter.setEmissionRate(0.16);
//                emitter.setSize(1, 1);
//
//                smoke.setEmitter(emitter);
//
//                getGameWorld().addEntities(smoke);
//            }
//        }, MouseButton.PRIMARY);
//
//        input.addAction(new UserAction("Spawn Explosion2") {
//            @Override
//            protected void onActionBegin() {
//                spawnSparks(input.getMouse().getGameX(), input.getMouse().getGameY());
//            }
//        }, MouseButton.SECONDARY);
//    }
//
//    private class LeftControl extends AbstractControl {
//
//        @Override
//        protected void initEntity(Entity entity) {
//
//        }
//
//        @Override
//        public void onWorldUpdate(Entity entity) {
//            entity.setX(entity.getX() - Math.random() * 13);
//
//            if (entity.isOutside(0, 0, getWidth(), getHeight())) {
//                entity.removeFromWorld();
//            }
//        }
//    }
//
//    @Override
//    protected void initAssets() {}
//
//    @Override
//    protected void initGame() {
//        getAudioPlayer().setGlobalSoundVolume(0);
//
//        Entity bg = Entity.noType();
//        bg.setSceneView(new Rectangle(getWidth(), getHeight(), Color.rgb(10, 10, 10)));
//
//        getGameWorld().addEntity(bg);
//
////        for (int y = 0; y < 30; y++) {
////            for (int x = 0; x < 20; x++) {
////                PhysicsEntity rect = new PhysicsEntity(Type.PARTICLES);
////                rect.setPosition(x * 4 + 100, y * 2 + 100);
////                rect.setSceneView(new Rectangle(4, 2, Color.RED));
////                rect.setBodyType(BodyType.DYNAMIC);
////
////                rect.setOnPhysicsInitialized(() ->
////                        rect.setLinearVelocity(Math.random()*5 - 2.5, Math.random()*5 - 2.5));
////
////                getGameWorld().addEntity(rect);
////            }
////        }
//        Text text = UIFactory.newText("F", 72);
//
//        Entity e = new Entity(Type.LETTER);
//        e.setPosition(400, 300);
//        e.setSceneView(text);
//        e.setCollidable(true);
//
//        getGameWorld().addEntity(e);
//
//        player = new Entity(Type.BOX);
//        player.setPosition(170, 100);
//        player.setCollidable(true);
//
//        Rectangle graphics = new Rectangle(200, 200, Color.rgb(10, 10, 10));
//        player.setSceneView(graphics);
//
//        getGameWorld().addEntity(player);
//    }
//
//    Entity player;
//
//    @Override
//    protected void initPhysics() {
//        getPhysicsWorld().addCollisionHandler(new CollisionHandler(Type.BOX, Type.LETTER) {
//            @Override
//            protected void onCollisionBegin(Entity box, Entity letter) {
//                for (int i = 0; i < 7; i++) {
//                    Point2D p = letter.getPosition().subtract(0, 50).add(Math.random()*50, Math.random()*50);
//
//                    spawnSparks(p.getX(), p.getY());
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void initUI() {
//    }
//
//    @Override
//    protected void onWorldUpdate() {}
//
//    private void spawnSparks(double x, double y) {
//        ParticleEntity smoke = new ParticleEntity(Type.PARTICLES);
//        smoke.setPosition(x, y);
//
//        ParticleEmitter emitter = ParticleEmitters.newSmokeEmitter();
//        emitter.setVelocityFunction((i, dx, dy) -> new Point2D(-Math.random(), Math.random() - 0.5));
//        emitter.setEmissionRate(0.16);
//        emitter.setSize(2, 4);
//
//        smoke.addControl(new LeftControl());
//        smoke.setEmitter(emitter);
//        smoke.setExpireTime(Duration.seconds(5));
//
//        getGameWorld().addEntities(smoke);
//    }
//
//    @OnUserAction(name = "Move Left", type = ActionType.ON_ACTION)
//    public void moveLeft() {
//        player.translate(-1, 0);
//    }
//
//    @OnUserAction(name = "Move Right", type = ActionType.ON_ACTION)
//    public void moveRight() {
//        player.translate(1, 0);
//    }
//
//    @OnUserAction(name = "Move Up", type = ActionType.ON_ACTION)
//    public void moveUp() {
//        player.translate(0, -1);
//    }
//
//    @OnUserAction(name = "Move Down", type = ActionType.ON_ACTION)
//    public void moveDown() {
//        player.translate(0, 1);
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
