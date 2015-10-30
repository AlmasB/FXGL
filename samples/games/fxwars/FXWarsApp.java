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
package games.fxwars;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.effect.ExplosionEmitter;
import com.almasb.fxgl.effect.ParticleEntity;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.event.InputManager;
import com.almasb.fxgl.event.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsManager;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.Timer;
import com.almasb.fxgl.time.TimerManager;
import com.almasb.fxgl.util.ApplicationMode;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FXWarsApp extends GameApplication {

    private enum Type implements EntityType {
        PLAYER, WANDERER, SEEKER, BULLET,
        SHOCKWAVE,
        EXPLOSION
    }

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("FXWars");
        settings.setVersion("0.1dev");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        InputManager input = getInputManager();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                player.setRotation(-90);
                player.translate(-5, 0);
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                player.setRotation(90);
                player.translate(5, 0);
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                player.setRotation(0);
                player.translate(0, -5);
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                player.setRotation(180);
                player.translate(0, 5);
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Shoot") {
            private Timer timer = getTimerManager().newTimer();

            @Override
            protected void onAction() {
                if (timer.elapsed(Duration.seconds(0.33))) {
                    shoot();
                    timer.capture();
                }
            }
        }, MouseButton.PRIMARY);

        input.addAction(new UserAction("Shockwave") {
            @Override
            protected void onActionBegin() {
                spawnShockwave();
            }
        }, KeyCode.F);
    }

    @Override
    protected void initAssets() throws Exception {

    }

    @Override
    protected void initGame() {
        initPlayer();

        getTimerManager().runAtInterval(this::spawnWanderer, Duration.seconds(2));
        getTimerManager().runAtInterval(this::spawnSeeker, Duration.seconds(5));
    }

    @Override
    protected void initPhysics() {
        PhysicsManager physics = getPhysicsManager();

        physics.addCollisionHandler(new CollisionHandler(Type.BULLET, Type.WANDERER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                spawnExplosion(b.getCenter());

                a.removeFromWorld();
                b.removeFromWorld();
                addScoreKill();
            }
        });

        physics.addCollisionHandler(new CollisionHandler(Type.BULLET, Type.SEEKER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                spawnExplosion(b.getCenter());

                a.removeFromWorld();
                b.removeFromWorld();
                addScoreKill();
            }
        });

        physics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.WANDERER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                a.setPosition(getRandomPoint());
                b.removeFromWorld();
                deductScoreDeath();
            }
        });

        physics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.SEEKER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                a.setPosition(getRandomPoint());
                b.removeFromWorld();
                deductScoreDeath();
            }
        });

        physics.addCollisionHandler(new CollisionHandler(Type.SHOCKWAVE, Type.SEEKER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                b.translate(b.getPosition().
                        subtract(player.getPosition()).
                        normalize()
                        .multiply(100));
            }
        });

        physics.addCollisionHandler(new CollisionHandler(Type.SHOCKWAVE, Type.WANDERER) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                b.translate(b.getPosition().
                        subtract(player.getPosition()).
                        normalize()
                        .multiply(100));
            }
        });
    }

    private IntegerProperty score = new SimpleIntegerProperty(0);

    @Override
    protected void initUI() {
        Text scoreText = new Text();
        scoreText.setFont(Font.font(18));
        scoreText.setTranslateX(1100);
        scoreText.setTranslateY(50);
        scoreText.textProperty().bind(score.asString("Score: %d"));

        getGameScene().addUINode(scoreText);
    }

    @Override
    protected void onUpdate() {

    }

    private void initPlayer() {
        player = new Entity(Type.PLAYER);
        player.setPosition(getWidth() / 2, getHeight() / 2);
        player.setCollidable(true);

        Polygon triangle = new Polygon(0, 40, 20, 0, 40, 40);
        triangle.setStroke(Color.BLUE);
        triangle.setStrokeWidth(3);

        player.setSceneView(triangle);

        getGameWorld().addEntity(player);
    }

    private void spawnWanderer() {
        Entity wanderer = new Entity(Type.WANDERER);
        wanderer.setPosition(50, 50);
        wanderer.addControl(new WandererControl());
        wanderer.setCollidable(true);

        Rectangle rect = new Rectangle(40, 40);
        rect.setArcHeight(15);
        rect.setArcWidth(15);
        rect.setFill(Color.GREENYELLOW);

        wanderer.setSceneView(rect);

        getGameWorld().addEntity(wanderer);
    }

    private void spawnSeeker() {
        Entity seeker = new Entity(Type.SEEKER);
        seeker.setPosition(50, 50);
        seeker.addControl(new SeekerControl());
        seeker.setCollidable(true);

        Circle circle = new Circle(20);
        circle.setFill(Color.DARKRED);

        seeker.setSceneView(circle);

        getGameWorld().addEntity(seeker);
    }

    private void spawnShockwave() {
        Entity shock = new Entity(Type.SHOCKWAVE);
        shock.setPosition(player.getCenter());

        Circle circle = new Circle(75);
        circle.setFill(null);
        circle.setStroke(Color.BLUEVIOLET);

        shock.setSceneView(circle);
        shock.xProperty().bind(player.xProperty().subtract(55));
        shock.yProperty().bind(player.yProperty().subtract(55));
        shock.setExpireTime(Duration.seconds(5));
        shock.setCollidable(true);

        getGameWorld().addEntity(shock);
    }

    private void spawnExplosion(Point2D point) {
        ParticleEntity explosion = new ParticleEntity(Type.EXPLOSION);
        explosion.setPosition(point);
        explosion.setExpireTime(Duration.seconds(0.5));

        ExplosionEmitter emitter = new ExplosionEmitter();
        explosion.setEmitter(emitter);

        getGameWorld().addEntity(explosion);
    }

    private void shoot() {
        Entity bullet = new Entity(Type.BULLET);
        bullet.setPosition(player.getCenter());
        bullet.addControl(new BulletControl(getVectorToCursor(bullet.getPosition())));
        bullet.setCollidable(true);

        Rectangle rect = new Rectangle(20, 1);
        rect.setFill(Color.RED);

        bullet.setSceneView(rect);

        getGameWorld().addEntity(bullet);
    }

    private Point2D getVectorToCursor(Point2D point) {
        double x = getInputManager().getMouse().getGameX();
        double y = getInputManager().getMouse().getGameY();

        return new Point2D(x, y).subtract(point);
    }

    private boolean isWithinScene(Entity entity) {
        return entity.getX() >= 0 && entity.getY() >= 0
                && entity.getX() + entity.getWidth() <= getWidth()
                && entity.getY() + entity.getHeight() <= getHeight();
    }

    private Point2D getRandomPoint() {
        return new Point2D(Math.random() * getWidth(), Math.random() * getHeight());
    }

    private void addScoreKill() {
        score.set(score.get() + 100);
    }

    private void deductScoreDeath() {
        score.set(score.get() - 1000);
    }

    private class BulletControl implements Control {

        private Point2D velocity;

        public BulletControl(Point2D vector) {
            this.velocity = vector.normalize().multiply(10);
        }

        @Override
        public void onUpdate(Entity entity) {
            entity.setRotation(Math.toDegrees(Math.atan2(velocity.getY(), velocity.getX())));
            entity.translate(velocity);
            if (!isWithinScene(entity))
                entity.removeFromWorld();
        }
    }

    private class WandererControl implements Control {
        private Timer timer = getTimerManager().newTimer();
        private Point2D velocity = Point2D.ZERO;

        @Override
        public void onUpdate(Entity entity) {
            if (timer.elapsed(Duration.seconds(4))) {
                velocity = getRandomPoint().subtract(entity.getPosition())
                        .multiply(TimerManager.tpfSeconds() / 4);
                timer.capture();
            }

            entity.translate(velocity);
        }
    }

    private class SeekerControl implements Control {
        private Timer timer = getTimerManager().newTimer();
        private Point2D velocity = Point2D.ZERO;

        @Override
        public void onUpdate(Entity entity) {
            if (timer.elapsed(Duration.seconds(2))) {
                velocity = player.getPosition().subtract(entity.getPosition())
                        .normalize()
                        .multiply(5);
                timer.capture();
            }

            entity.translate(velocity);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
