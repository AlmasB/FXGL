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

package games.battletanks;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.component.IntegerComponent;
import com.almasb.fxgl.entity.component.ObjectComponent;
import com.almasb.fxgl.entity.control.AbstractControl;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Optional;
import java.util.Random;

/**
 * BattleTanks.
 * <p>
 *     Left Click - Spawn player tank.
 *     Right Click - Spawn enemy tank.
 *
 *     Each tank has 5 HP. Each hit decreases HP by 1.
 *     Each tank will shoot nearest enemy tank.
 *     If no tanks are within shooting range, tanks move towards center of the map.
 * </p>
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BattleTanksApp extends GameApplication {

    private static final int ATTACK_RANGE = 200;

    private enum Type implements EntityType {
        ENEMY_TANK, INFANTRY, AIR, BULLET,
        PLAYER_TANK
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("BattleTanks");
        settings.setVersion("0.1dev");
        settings.setShowFPS(false);
        settings.setMenuEnabled(false);
        settings.setIntroEnabled(false);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Spawn Player Tank") {
            @Override
            protected void onActionBegin() {
                spawnPlayerTank();
            }
        }, MouseButton.PRIMARY);

        input.addAction(new UserAction("Spawn Enemy Tank") {
            @Override
            protected void onActionBegin() {
                spawnEnemyTank();
            }
        }, MouseButton.SECONDARY);
    }

    @Override
    protected void initAssets() {}

    private Random random;

    @Override
    protected void initGame() {
        getAudioPlayer().setGlobalSoundVolume(0);

        random = new Random();

        Entity bg = Entity.noType();
        bg.setSceneView(new Rectangle(getWidth(), getHeight(), Color.rgb(0, 0, 10)));
        getGameWorld().addEntity(bg);

        for (int i = 0; i < 5; i++) {
            spawnEnemyTank();
            spawnPlayerTank();
        }
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physicsWorld = getPhysicsWorld();

        CollisionHandler handler = new CollisionHandler(Type.BULLET, Type.ENEMY_TANK) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity tank) {
                if (bullet.getComponent(OwnerComponent.class).get().getValue() == tank)
                    return;

                bullet.removeFromWorld();
                tank.getComponent(HPComponent.class).ifPresent(hp -> {
                    if (hp.getValue() > 0)
                        hp.setValue(hp.getValue() - 1);
                    else {
                        tank.removeFromWorld();
                    }
                });
            }
        };
        physicsWorld.addCollisionHandler(handler);
        physicsWorld.addCollisionHandler(handler.copyFor(Type.BULLET, Type.PLAYER_TANK));
    }

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate() {}

    private void spawnPlayerTank() {
        double x = random.nextDouble() * (getWidth() - 64);
        double y = getHeight() - 64;
        spawnTank(Type.PLAYER_TANK, "tank_player.png", x, y);
    }

    private void spawnEnemyTank() {
        double x = random.nextDouble() * (getWidth() - 64);
        double y = 0;
        spawnTank(Type.ENEMY_TANK, "tank_enemy.png", x, y);
    }

    private void spawnTank(EntityType type, String texture, double x, double y) {
        Entity tank = new Entity(type);
        tank.setPosition(x, y);
        tank.addComponent(new HPComponent(5));
        tank.setCollidable(true);
        tank.addControl(new TankControl());

        Pane pane = new Pane();
        pane.getChildren().add(getAssetLoader().loadTexture(texture));

        Rectangle health = new Rectangle(64, 5, Color.GREEN);
        health.setTranslateY(64);
        health.widthProperty().bind(
                tank.getComponent(HPComponent.class).get().valueProperty().multiply(12));
        pane.getChildren().add(health);

        tank.setSceneView(pane);

        getGameWorld().addEntity(tank);
    }

    private class TargetComponent extends ObjectComponent<Entity> {
    }

    private class HPComponent extends IntegerComponent {
        public HPComponent(int value) {
            super(value);
        }
    }

    private class AttackControl extends AbstractControl {
        private Entity target;
        private LocalTimer timer;

        private boolean active = true;

        @Override
        protected void initEntity(Entity entity) {
            timer = getService(ServiceType.LOCAL_TIMER);
        }

        @Override
        public void onUpdate(Entity entity) {
            if (!active)
                return;

            if (timer.elapsed(Duration.seconds(0.75))) {
                if (shootTarget())
                    timer.capture();
            }
        }

        private boolean shootTarget() {
            if (target != null) {
                if (!target.isAlive()) {
                    target = null;
                    return false;
                }

                if (target.distance(entity) <= ATTACK_RANGE) {
                    Entity bullet = new Entity(Type.BULLET);
                    bullet.setCollidable(true);
                    bullet.setPosition(entity.getCenter());
                    bullet.setSceneView(getAssetLoader().loadTexture("tank_bullet.png"));
                    bullet.addControl(new BulletControl(target.getCenter()
                            .subtract(entity.getCenter()).normalize().multiply(10)));

                    bullet.addComponent(new OwnerComponent(entity));

                    getGameWorld().addEntity(bullet);

                    entity.rotateToVector(bullet.getControl(BulletControl.class).get().velocity);
                    return true;
                }
            }

            return false;
        }

        public void setTarget(Entity target) {
            this.target = target;
        }
    }

    private class MoveControl extends AbstractControl {

        private Point2D moveTarget;
        private Point2D velocity = Point2D.ZERO;

        private boolean active = true;

        @Override
        protected void initEntity(Entity entity) {
            moveTarget = entity.getPosition();
        }

        @Override
        public void onUpdate(Entity entity) {
            if (!active)
                return;

            if (entity.getPosition().distance(moveTarget) < 10) {
                moveTarget = getRandomPoint();
                velocity = moveTarget.subtract(entity.getPosition())
                        .normalize().multiply(5);
            }

            entity.rotateToVector(velocity);
            entity.translate(velocity);
        }

        private Point2D getRandomPoint() {
            return new Point2D(random.nextDouble() * (getWidth() - 64),
                    random.nextDouble() * (getHeight() - 64));
        }
    }

    private class TankControl extends AbstractControl {
        private AttackControl attackControl;
        private MoveControl moveControl;
        private EntityType opponentType;

        private boolean moving = true;

        @Override
        protected void initEntity(Entity entity) {
            attackControl = new AttackControl();
            moveControl = new MoveControl();

            attackControl.active = false;

            entity.addControl(moveControl);
            entity.addControl(attackControl);

            if (entity.isType(Type.PLAYER_TANK)) {
                opponentType = Type.ENEMY_TANK;
            } else {
                opponentType = Type.PLAYER_TANK;
            }
        }

        @Override
        public void onUpdate(Entity entity) {
            Optional<Entity> target = getGameWorld().getClosestEntity(entity, opponentType);

            if (!target.isPresent()) {
                if (moving)
                    return;

                attackControl.active = false;
                moveControl.active = true;
                moving = true;
            }

            target.ifPresent(t -> {
                if (t.distance(entity) < ATTACK_RANGE) {
                    if (!moving)
                        return;

                    attackControl.setTarget(t);
                    attackControl.active = true;
                    moveControl.active = false;
                    moving = false;
                } else {
                    if (moving)
                        return;

                    attackControl.active = false;
                    moveControl.active = true;
                    moving = true;
                }
            });
        }
    }

    private class OwnerComponent extends ObjectComponent<Entity> {
        public OwnerComponent(Entity entity) {
            super(entity);
        }
    }

    private class BulletControl extends AbstractControl {

        private Point2D velocity;

        public BulletControl(Point2D velocity) {
            this.velocity = velocity;
        }

        @Override
        protected void initEntity(Entity entity) {
            entity.rotateToVector(velocity);
        }

        @Override
        public void onUpdate(Entity entity) {
            entity.translate(velocity);

            if (entity.getX() < 0 || entity.getX() > getWidth()
                    || entity.getY() < 0 || entity.getY() > getHeight())
                entity.removeFromWorld();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
