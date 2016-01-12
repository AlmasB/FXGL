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

package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.effect.ParticleEmitter;
import com.almasb.fxgl.effect.ParticleEmitters;
import com.almasb.fxgl.effect.ParticleEntity;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.control.AbstractControl;
import com.almasb.fxgl.entity.control.LiftControl;
import com.almasb.fxgl.input.*;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsEntity;
import com.almasb.fxgl.search.Maze;
import com.almasb.fxgl.search.MazeCell;
import com.almasb.fxgl.settings.GameSettings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jbox2d.dynamics.BodyType;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Platformer extends GameApplication {

    private enum Type implements EntityType {
        PLAYER, PLATFORM
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(false);
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    private PhysicsEntity player;

    @Override
    protected void initInput() {
        getAudioPlayer().setGlobalSoundVolume(0);

        Input input = getInput();
        input.addInputMapping(new InputMapping("Move Right", KeyCode.D));
        input.addInputMapping(new InputMapping("Move Left", KeyCode.A));
        input.addInputMapping(new InputMapping("Jump", KeyCode.W));
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        initPlatform(0, 560, 1200, 40);
        initPlatform(300, 500, 120, 40);
        initPlayer();

        getGameScene().getViewport().setBounds(0, 0, 1200, (int)getHeight());
        getGameScene().getViewport().bindToEntity(player, getWidth() / 2, getHeight() / 2);
    }

    @Override
    protected void initPhysics() {
//        getPhysicsWorld().addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.PLATFORM) {
//            @Override
//            protected void onCollisionBegin(Entity player, Entity platform) {
//                if (player.getY() < platform.getY() && player.getCenter().getX() >= platform.getX()
//                        && player.getCenter().getX() <= platform.getX() + platform.getWidth()) {
//                    isJumping = false;
//                }
//            }
//        });
    }

    @Override
    protected void initUI() {
    }

    @Override
    protected void onUpdate() {
        //player.setLinearVelocity(0, player.getLinearVelocity().getY());
    }

    private void initPlatform(double x, double y, double w, double h) {
        PhysicsEntity platform = new PhysicsEntity(Type.PLATFORM);
        platform.setPosition(x, y);
        platform.setSceneView(new Rectangle(w, h, Color.GRAY));
        platform.setCollidable(true);

        getGameWorld().addEntity(platform);
    }

    private void initPlayer() {
        player = new PhysicsEntity(Type.PLAYER);
        player.setPosition(40, 40);
        player.setSceneView(new Rectangle(40, 60, Color.BLUE));
        player.setBodyType(BodyType.DYNAMIC);
        player.setCollidable(true);

        getGameWorld().addEntity(player);
    }

    private double speed = 3;

    @OnUserAction(name = "Move Right", type = ActionType.ON_ACTION)
    public void moveRight() {
        Point2D vel = player.getLinearVelocity().add(speed, 0);
        if (vel.getX() > speed)
            vel = new Point2D(speed, vel.getY());

        player.setLinearVelocity(vel);
    }

    @OnUserAction(name = "Move Left", type = ActionType.ON_ACTION)
    public void moveLeft() {
        Point2D vel = player.getLinearVelocity().add(-speed, 0);
        if (vel.getX() < -speed)
            vel = new Point2D(-speed, vel.getY());

        player.setLinearVelocity(vel);
    }

    @OnUserAction(name = "Jump", type = ActionType.ON_ACTION_BEGIN)
    public void jump() {
        shoot();
//        if (isInTheAir())
//            return;
//
//        player.setLinearVelocity(player.getLinearVelocity().getX(), -0.75 * speed);
    }

    private void shoot() {
        ParticleEntity fire = new ParticleEntity(Type.PLAYER);
        fire.setPosition(player.getCenter());
        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
        emitter.setEmissionRate(1);
        emitter.setSize(1, 9);
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(0.16));
        emitter.setVelocityFunction((i, x, y) -> new Point2D(0, Math.random() - 0.5));
        emitter.setGravityFunction(() -> new Point2D(-0.05, 0));

        fire.addControl(new FireControl());
        fire.setEmitter(emitter);

        getGameWorld().addEntity(fire);
    }

    private boolean isInTheAir() {
        return getGameWorld().getEntitiesInRange(
                new Rectangle2D(player.getX(), player.getY() + player.getHeight(), player.getWidth(), 5),
                Type.PLATFORM).isEmpty();
    }

    private class FireControl extends AbstractControl {

        @Override
        protected void initEntity(Entity entity) {

        }

        @Override
        public void onUpdate(Entity entity) {
            entity.translate(10, 0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
