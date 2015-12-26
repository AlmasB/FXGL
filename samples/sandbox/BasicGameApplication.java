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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.app.Executor;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.input.InputModifier;
import com.almasb.fxgl.gameplay.Achievement;
import com.almasb.fxgl.scene.IntroFactory;
import com.almasb.fxgl.scene.IntroScene;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.PhysicsEntity;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.app.ApplicationMode;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class BasicGameApplication extends GameApplication {

    private enum Type implements EntityType {
        PLAYER, ENEMY, BOX, CRATE
    }

    private Entity player, enemy;
    private PhysicsEntity box;

    private Text debug, debug2;
    IntegerProperty i = new SimpleIntegerProperty(2000);


    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Basic FXGL Application");
        settings.setVersion("0.1developer");
        settings.setFullScreen(false);
        settings.setIntroEnabled(true);
        settings.setMenuEnabled(true);
        settings.setShowFPS(true);
        //settings.setMenuStyle(MenuStyle.GTA5);
        //settings.setCSS("fxgl_gta5.css");
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initAchievements() {
        Achievement a = new Achievement("Score Master", "Score 20000 Points");

        getAchievementManager().registerAchievement(a);
    }

    @Override
    protected IntroFactory initIntroFactory() {
        return new IntroFactory() {
            @Override
            public IntroScene newIntro(ReadOnlyGameSettings settings) {
                return new IntroScene(settings) {
                    MediaPlayer mediaPlayer;
                    Sound sound;

                    {
                        Media media = new Media(getClass().getResource("/assets/video/testvideo.mp4").toExternalForm());
                        mediaPlayer = new MediaPlayer(media);
                        MediaView view = new MediaView(mediaPlayer);
                        view.setFitWidth(1280);
                        view.setFitHeight(720);

                        getRoot().getChildren().add(view);

                        mediaPlayer.setOnEndOfMedia(this::finishIntro);

                        sound = getAssetLoader().loadSound("intro.wav");
                    }

                    @Override
                    public void startIntro() {

                        mediaPlayer.setRate(1.5);
                        mediaPlayer.play();
                        sound.setRate(0.5);

                        getAudioPlayer().playSound(sound);
                    }
                };
            }
        };
    }

    Executor executor;

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                //enemy.rotateBy(-5);
                player.translate(-1, 0);
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                //enemy.rotateBy(5);
                player.translate(10, 0);
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onActionBegin() {
                //enemy.setRotation(0);
                player.translate(0, -1);
                //i.set(i.get() + 10000);
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                //enemy.setRotation(90);
                player.translate(0, 1);
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Rotate Left") {
            @Override
            protected void onAction() {
                enemy.rotateBy(-5);
                //player.translate(-1, 0);
            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("Rotate Right") {
            @Override
            protected void onAction() {
                enemy.rotateBy(5);
                //player.translate(1, 0);
            }
        }, KeyCode.RIGHT);

        input.addAction(new UserAction("Rotate Up") {
            @Override
            protected void onActionBegin() {
                getAudioPlayer().playSound(getAssetLoader().loadSound("intro.wav"));

                //executor.submit(() -> doWork());
                //doWork();

                //getSceneManager().setNewResolution(1920, 1080);
            }
        }, KeyCode.UP);

        input.addAction(new UserAction("Rotate Down") {
            @Override
            protected void onActionBegin() {
                getAssetLoader().clearCache();

                getNotificationService().pushNotification("You got an achievement!");
                getNotificationService().pushNotification("You have won the game!");
                getNotificationService().pushNotification("Just a test of the notification system!");
            }
        }, KeyCode.DOWN);

        input.addAction(new UserAction("Spawn") {
            @Override
            protected void onActionBegin() {
                PhysicsEntity b = new PhysicsEntity(Type.CRATE);
                Rectangle r = new Rectangle(40, 40);
                r.setFill(Color.BLUE);
                b.setSceneView(r);
                b.setBodyType(BodyType.DYNAMIC);
                b.setPosition(input.getMouse().getGameXY());
                //b.addHitBox(new HitBox("HEAD", new BoundingBox(0, 0, 40, 40)));

                FixtureDef fd = new FixtureDef();
                fd.setDensity(0.05f);
                fd.setRestitution(1.0f);

                b.setFixtureDef(fd);
                b.setExpireTime(Duration.seconds(10));

                getGameWorld().addEntity(b);

//                b.rotationProperty().addListener((obs, old, newValue) -> {
//                    System.out.println(newValue.doubleValue() - old.doubleValue());
//                });

//                b.addControl(new Control() {
//
//                    @Override
//                    public void onUpdate(Entity entity) {
//                        System.out.println(entity.getRotation());
//                    }
//                });

                //b.setOnPhysicsInitialized(() -> b.setAngularVelocity(5));

            }
        }, MouseButton.PRIMARY, InputModifier.ALT);

        input.addAction(new UserAction("Spawn2") {
            @Override
            protected void onActionBegin() {
                Entity e = new Entity(Type.BOX);
                e.setPosition(input.getMouse().getGameX(), input.getMouse().getGameY());


                Pane pane = new Pane();
                pane.getChildren().addAll(new Rectangle(100, 5), new Circle(5, Color.BLUE));

                e.setSceneView(pane);

                e.rotateToVector(e.getPosition().subtract(0, 0));

                getGameWorld().addEntity(e);
                countProperty.set(countProperty.get() + 1);
            }
        }, MouseButton.SECONDARY);

        input.addAction(new UserAction("Cinematic") {
            @Override
            protected void onActionBegin() {
                Viewport viewport = getGameScene().getViewport();

                Point2D[] points = {
                        new Point2D(1500, 200)
//                        new Point2D(800, -200),
//                        new Point2D(230, 0),
//                        Point2D.ZERO
                };

                Timeline timeline = new Timeline();

                for (Point2D p : points) {
                    KeyValue kv = new KeyValue(viewport.xProperty(), p.getX());
                    KeyValue kv2 = new KeyValue(viewport.yProperty(), p.getY());

                    KeyFrame frame = new KeyFrame(Duration.seconds(3), kv, kv2);
                    timeline.getKeyFrames().add(frame);
                }

                timeline.play();
            }
        }, KeyCode.K);
    }

    public IntegerProperty countProperty = new SimpleIntegerProperty(0);

    @Override
    protected void initAssets() {
        //getAssetLoader().cache();
    }

    private void doWork() {
        try {
            log.finer("Started work");
            Thread.sleep(1000);
            log.finer("Finished work");
        } catch (InterruptedException e) {
            throw new IllegalArgumentException("gg");
        }
    }

    @Override
    protected void initGame() {
        getAchievementManager().getAchievementByName("Score Master")
                .achievedProperty().bind(i.greaterThanOrEqualTo(20000));

        EntityView.turnOnDebugBBox(Color.RED);

        executor = getService(ServiceType.EXECUTOR);



        player = new Entity(Type.PLAYER);
        //Circle graphics = new Circle(40);
        player.setSceneView(getAssetLoader().loadTexture("brick.png"));

        //player.addHitBox(new HitBox("HEAD", new BoundingBox(0, 0, 80, 80)));
        player.setPosition(100, 100);

        enemy = new Entity(Type.ENEMY);
        Rectangle enemyGraphics = new Rectangle(200, 40);
        enemyGraphics.setFill(Color.RED);
        enemy.setSceneView(enemyGraphics, new RenderLayer() {
            @Override
            public String name() {
                return "ENEMY";
            }

            @Override
            public int index() {
                return 1000;
            }
        });

        //enemy.addHitBox(new HitBox("HEAD", new BoundingBox(0, 0, 200, 40)));
        enemy.setPosition(2000, 100);


        // we need to set collidable to true
        // so that collision system can 'see' them
        player.setCollidable(true);
        enemy.setCollidable(true);

        getGameWorld().addEntities(player, enemy);

        box = new PhysicsEntity(Type.ENEMY);
        box.setSceneView(new Rectangle(500, 100));
        box.setPosition(0, 500);
        //box.addHitBox(new HitBox("HEAD", new BoundingBox(0, 0, 500, 100)));


        getGameWorld().addEntity(box);

        getGameScene().addGameView(new PlayerView());

        addStuff();
    }

    private void addStuff() {
//        Entity e = new Entity(Type.BOX);
//        e.setPosition(400, 400);
//        e.setSceneView(getAssetManager().loadTexture("brick.png"));
//
//        Entity e2 = new Entity(Type.BOX);
//        e2.setPosition(300, 400);
//        e2.setSceneView(getAssetManager().loadTexture("brick.png").toGrayscale());
//
//        getGameWorld().addEntities(e, e2);
    }

    private class PlayerView extends EntityView {

        public PlayerView() {
            super(player);

            Text text = new Text();
            text.textProperty().bind(player.xProperty().asString().concat(player.yProperty().asString()));

            addNode(text);
            setTranslateX(300);
            setTranslateY(300);
        }

    }

    @Override
    protected void initPhysics() {
//        PhysicsWorld physics = getPhysicsWorld();
//        physics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.ENEMY) {
//            // the order of entities determined by
//            // the order of their types passed into constructor
//            @Override
//            protected void onCollisionBegin(Entity player, Entity enemy) {
//                debug2.setText("collision");
//            }
//
//            @Override
//            protected void onCollisionEnd(Entity player, Entity enemy) {
//                debug2.setText("");
//            }
//        });
    }

    @Override
    protected void initUI() {
        debug = new Text();
        debug.setTranslateX(50);
        debug.setTranslateY(200);
        debug.setFont(Font.font(18));
        debug.setWrappingWidth(400);

        debug2 = new Text();
        debug2.setTranslateY(50);

        Parent ui = getAssetLoader().loadFXML("test_ui.fxml", new FXGLController(this));


        getGameScene().addUINodes(debug, debug2, ui);
    }

    @Override
    protected void onUpdate() {
        //debug.setText(enemy.getView().getBoundsInParent().toString() + " " + enemy.getX());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
