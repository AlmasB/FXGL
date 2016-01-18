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
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
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
//import com.almasb.ents.Entity;
//import com.almasb.fxgl.app.ApplicationMode;
//import com.almasb.fxgl.app.GameApplication;
//import com.almasb.fxgl.audio.Sound;
//import com.almasb.fxgl.input.Input;
//import com.almasb.fxgl.input.InputModifier;
//import com.almasb.fxgl.input.UserAction;
//import com.almasb.fxgl.physics.CollisionHandler;
//import com.almasb.fxgl.physics.HitBox;
//import com.almasb.fxgl.scene.IntroFactory;
//import com.almasb.fxgl.scene.IntroScene;
//import com.almasb.fxgl.scene.Viewport;
//import com.almasb.fxgl.settings.GameSettings;
//import com.almasb.fxgl.settings.ReadOnlyGameSettings;
//import javafx.animation.KeyFrame;
//import javafx.animation.KeyValue;
//import javafx.animation.Timeline;
//import javafx.beans.property.IntegerProperty;
//import javafx.beans.property.SimpleIntegerProperty;
//import javafx.geometry.BoundingBox;
//import javafx.geometry.Point2D;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.MouseButton;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Pane;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//import javafx.scene.media.MediaView;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Circle;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//import javafx.util.Duration;
//import org.jbox2d.dynamics.BodyType;
//import org.jbox2d.dynamics.FixtureDef;
//
//public class BasicGameApplication extends GameApplication {
//
//    private enum Type {
//        PLAYER, ENEMY, BOX, CRATE
//    }
//
//    private Entity player, enemy;
//    private PhysicsEntity box;
//
//    private Text debug, debug2;
//    IntegerProperty i = new SimpleIntegerProperty(2000);
//
//
//    @Override
//    protected void initSettings(GameSettings settings) {
//        settings.setWidth(1280);
//        settings.setHeight(720);
//        settings.setTitle("Basic FXGL Application");
//        settings.setVersion("0.1developer");
//        settings.setFullScreen(false);
//        settings.setIntroEnabled(false);
//        settings.setMenuEnabled(false);
//        settings.setShowFPS(true);
//        //settings.setMenuStyle(MenuStyle.GTA5);
//        //settings.setCSS("fxgl_gta5.css");
//        settings.setApplicationMode(ApplicationMode.DEBUG);
//    }
//
//    @Override
//    protected IntroFactory initIntroFactory() {
//        return new IntroFactory() {
//            @Override
//            public IntroScene newIntro(ReadOnlyGameSettings settings) {
//                return new IntroScene(settings) {
//                    MediaPlayer mediaPlayer;
//                    Sound sound;
//
//                    {
//                        Media media = new Media(getClass().getResource("/assets/video/testvideo.mp4").toExternalForm());
//                        mediaPlayer = new MediaPlayer(media);
//                        MediaView view = new MediaView(mediaPlayer);
//                        view.setFitWidth(1280);
//                        view.setFitHeight(720);
//
//                        getRoot().getChildren().add(view);
//
//                        mediaPlayer.setOnEndOfMedia(this::finishIntro);
//
//                        sound = getAssetLoader().loadSound("intro.wav");
//                    }
//
//                    @Override
//                    public void startIntro() {
//
//                        mediaPlayer.setRate(1.5);
//                        mediaPlayer.play();
//                        sound.setRate(0.5);
//
//                        getAudioPlayer().playSound(sound);
//                    }
//                };
//            }
//        };
//    }
//
//    @Override
//    protected void initInput() {
//        Input input = getInput();
//
//        input.addAction(new UserAction("Move Left") {
//            @Override
//            protected void onAction() {
//                //enemy.rotateBy(-5);
//                player.translate(-5, 0);
//            }
//        }, KeyCode.A);
//
//        input.addAction(new UserAction("Move Right") {
//            @Override
//            protected void onAction() {
//                //enemy.rotateBy(5);
//                player.translate(5, 0);
//            }
//        }, KeyCode.D);
//
//        input.addAction(new UserAction("Move Up") {
//            @Override
//            protected void onAction() {
//                //enemy.setRotation(0);
//                player.translate(0, -5);
//                //i.set(i.get() + 10000);
//            }
//        }, KeyCode.W);
//
//        input.addAction(new UserAction("Move Down") {
//            @Override
//            protected void onAction() {
//                //enemy.setRotation(90);
//                player.translate(0, 5);
//            }
//        }, KeyCode.S);
//
//        input.addAction(new UserAction("Rotate Left") {
//            @Override
//            protected void onAction() {
//                enemy.rotateBy(-5);
//                //player.translate(-1, 0);
//            }
//        }, KeyCode.LEFT);
//
//        input.addAction(new UserAction("Rotate Right") {
//            @Override
//            protected void onAction() {
//                enemy.rotateBy(5);
//                //player.translate(1, 0);
//            }
//        }, KeyCode.RIGHT);
//
//        input.addAction(new UserAction("Spawn") {
//            @Override
//            protected void onActionBegin() {
//                PhysicsEntity b = new PhysicsEntity(Type.CRATE);
//
//                b.setRotation(90);
//
//                b.setSceneView(new HBox(new Rectangle(40, 40, Color.BLUE), new Rectangle(40, 40, Color.RED)));
//                b.setBodyType(BodyType.DYNAMIC);
//                b.setPosition(input.getMouse().getGameXY());
//
//                b.setGenerateHitBoxesFromView(false);
//
//                b.addHitBox(new HitBox("LEFT", new BoundingBox(0, 0, 40, 40)));
//                b.addHitBox(new HitBox("RIGHT", new BoundingBox(40, 0, 40, 40)));
//
//                FixtureDef fd = new FixtureDef();
//                fd.setDensity(0.05f);
//                fd.setRestitution(0.6f);
//
//                b.setCollidable(true);
//                b.setFixtureDef(fd);
//                //b.setExpireTime(Duration.seconds(10));
//
//                getGameWorld().addEntity(b);
//
////                b.rotationProperty().addListener((obs, old, newValue) -> {
////                    System.out.println(newValue.doubleValue() - old.doubleValue());
////                });
//
////                b.addControl(new Control() {
////
////                    @Override
////                    public void onWorldUpdate(Entity entity) {
////                        System.out.println(entity.getRotation());
////                    }
////                });
//
//                //b.setOnPhysicsInitialized(() -> b.setAngularVelocity(5));
//
//            }
//        }, MouseButton.PRIMARY, InputModifier.ALT);
//
//        input.addAction(new UserAction("Spawn2") {
//            @Override
//            protected void onActionBegin() {
//                Entity e = new Entity(Type.BOX);
//                e.setPosition(input.getMouse().getGameX(), input.getMouse().getGameY());
//
//
//                Pane pane = new Pane();
//                pane.getChildren().addAll(new Rectangle(100, 5), new Circle(5, Color.BLUE));
//
//                e.setSceneView(pane);
//
//                e.rotateToVector(e.getPosition().subtract(0, 0));
//
//                getGameWorld().addEntity(e);
//                countProperty.set(countProperty.get() + 1);
//            }
//        }, MouseButton.SECONDARY);
//
//        input.addAction(new UserAction("Cinematic") {
//            @Override
//            protected void onActionBegin() {
//                Viewport viewport = getGameScene().getViewport();
//
//                Point2D[] points = {
//                        new Point2D(1500, 200)
////                        new Point2D(800, -200),
////                        new Point2D(230, 0),
////                        Point2D.ZERO
//                };
//
//                Timeline timeline = new Timeline();
//
//                for (Point2D p : points) {
//                    KeyValue kv = new KeyValue(viewport.xProperty(), p.getX());
//                    KeyValue kv2 = new KeyValue(viewport.yProperty(), p.getY());
//
//                    KeyFrame frame = new KeyFrame(Duration.seconds(3), kv, kv2);
//                    timeline.getKeyFrames().add(frame);
//                }
//
//                timeline.play();
//            }
//        }, KeyCode.K);
//    }
//
//    public IntegerProperty countProperty = new SimpleIntegerProperty(0);
//
//    @Override
//    protected void initAssets() {}
//
//    @Override
//    protected void initGame() {
//        getAudioPlayer().setGlobalSoundVolume(0);
//
//
//        PhysicsEntity ground = new PhysicsEntity(Type.BOX);
//        ground.setPosition(-50, 500);
//        ground.setSceneView(new Rectangle(1200, 100));
//        ground.setCollidable(true);
//
//        getGameWorld().addEntity(ground);
//
//        //EntityView.turnOnDebugBBox(Color.RED);
//
//        //getInput().setRegisterInput(false);
//
//        player = new Entity(Type.PLAYER);
//        player.setPosition(250, 250);
//        player.setSceneView(new Rectangle(40, 40));
//
//        getGameWorld().addEntity(player);
//
//        getMasterTimer().runOnceAfter(() -> {
//            //getDisplay().showInputBox("Test message", input -> input.matches("^[\\pL\\pN]+$"), log::info);
//
//
//
//            //getInput().mockButtonPress(MouseButton.PRIMARY, 50, 50, InputModifier.ALT);
//        }, Duration.seconds(2));
//
////        getMasterTimer().runOnceAfter(() -> {
////            getInput().mockButtonRelease(MouseButton.PRIMARY, InputModifier.ALT);
////        }, Duration.seconds(3));
//    }
//
//    @Override
//    protected void initPhysics() {
//        getPhysicsWorld().addCollisionHandler(new CollisionHandler(Type.CRATE, Type.BOX) {
//            @Override
//            protected void onHitBoxTrigger(Entity crate, Entity ground, HitBox boxA, HitBox boxB) {
//                log.info(boxA.getName() + " hit " + boxB.getName());
//            }
//
//            @Override
//            protected void onCollisionBegin(Entity a, Entity b) {
//                log.info("Touch");
//            }
//        });
//    }
//
//    @Override
//    protected void initUI() {
//        debug = new Text();
//        debug.setTranslateX(50);
//        debug.setTranslateY(200);
//        debug.setFont(Font.font(18));
//        debug.setWrappingWidth(400);
//
//        debug2 = new Text();
//        debug2.setTranslateY(50);
//
//        getGameScene().addUINodes(debug, debug2);
//    }
//
//    @Override
//    protected void onWorldUpdate() {}
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
