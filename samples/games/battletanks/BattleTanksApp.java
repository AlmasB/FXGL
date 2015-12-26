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
import com.almasb.fxgl.asset.Texture;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.component.IntegerComponent;
import com.almasb.fxgl.entity.component.ObjectComponent;
import com.almasb.fxgl.entity.control.AbstractControl;
import com.almasb.fxgl.entity.control.ProjectileControl;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.search.AStarGrid;
import com.almasb.fxgl.search.AStarNode;
import com.almasb.fxgl.search.NodeState;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;
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
    private static final int TANK_MOVE_SPEED = 40;
    private static final int TANK_SIZE = 40;
    private static final int TANK_HP = 5;
    private static final int BULLET_MOVE_SPEED = 10;

    private enum Type implements EntityType {
        ENEMY_TANK, PLAYER_TANK, BULLET
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
    private AStarGrid grid;

    @Override
    protected void initGame() {
        getAudioPlayer().setGlobalSoundVolume(0);

        random = new Random();
        grid = new AStarGrid((int)getWidth() / TANK_MOVE_SPEED, (int)getHeight() / TANK_MOVE_SPEED);

        Entity bg = Entity.noType();
        bg.setSceneView(new Rectangle(getWidth(), getHeight(), Color.rgb(0, 0, 10)));
        getGameWorld().addEntity(bg);

        getMasterTimer().runAtInterval(() -> {
            spawnPlayerTank();
            spawnEnemyTank();
        }, Duration.seconds(3));
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physicsWorld = getPhysicsWorld();

        CollisionHandler handler = new CollisionHandler(Type.BULLET, Type.ENEMY_TANK) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity tank) {
                if (bullet.getComponent(OwnerComponent.class).get().getValue().isType(tank.getEntityType()))
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
    protected void onUpdate() {
        getGameWorld().getEntities(Type.BULLET)
                .stream()
                .filter(e -> e.isOutside(0, 0, getWidth(), getHeight()))
                .forEach(Entity::removeFromWorld);
    }

    private void updateGrid(Entity self) {
        grid.setStateForAllNodes(NodeState.WALKABLE);

        getGameWorld().getEntities(Type.PLAYER_TANK, Type.ENEMY_TANK).forEach(e -> {
            if (e == self)
                return;

            int sx = toGrid(e.getX());
            int sy = toGrid(e.getY());

            for (int y = sy; y < sy + TANK_SIZE / TANK_MOVE_SPEED; y++) {
                for (int x = sx; x < sx + TANK_SIZE / TANK_MOVE_SPEED; x++) {
                    if (grid.isWithin(x, y))
                        grid.setNodeState(x, y, NodeState.NOT_WALKABLE);
                }
            }
        });
    }

    private boolean isBusy(int x, int y) {
        return getGameWorld().getEntities(Type.ENEMY_TANK, Type.PLAYER_TANK)
                .stream()
                .filter(e -> toGrid(e.getX()) == x && toGrid(e.getY()) == y)
                .findAny()
                .isPresent();
    }

    private void spawnPlayerTank() {
        int count = 0;
        int x, y = grid.getHeight() - 1;
        do {
            x = random.nextInt(grid.getWidth());

            if (count == 10)
                break;
            count++;
        } while (isBusy(x, y));

        spawnTank(Type.PLAYER_TANK, "tank_player.png", x, y);
    }

    private void spawnEnemyTank() {
        int count = 0;
        int x, y = 0;
        do {
            x = random.nextInt(grid.getWidth());

            if (count == 10)
                break;
            count++;
        } while (isBusy(x, y));

        spawnTank(Type.ENEMY_TANK, "tank_enemy.png", x, y);
    }

    private void spawnTank(EntityType type, String textureName, int x, int y) {
        Entity tank = new Entity(type);
        tank.setPosition(x * TANK_SIZE, y * TANK_SIZE);
        tank.addComponent(new HPComponent(TANK_HP));
        tank.setCollidable(true);
        tank.addControl(new TankControl());

        Pane pane = new Pane();

        Texture texture = getAssetLoader().loadTexture(textureName);
        texture.setFitWidth(TANK_SIZE);
        texture.setFitHeight(TANK_SIZE);
        pane.getChildren().add(texture);

        Rectangle health = new Rectangle(TANK_SIZE, 5, Color.YELLOW);
        health.setTranslateY(TANK_SIZE);
        health.widthProperty().bind(
                tank.getComponent(HPComponent.class).get().valueProperty().multiply(TANK_SIZE / TANK_HP));
        pane.getChildren().add(health);

        tank.setSceneView(pane);

        getGameWorld().addEntity(tank);
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
                    bullet.setPosition(entity.getCenter().subtract(8, 8));
                    bullet.setSceneView(getAssetLoader().loadTexture("tank_bullet.png"));
                    bullet.addControl(new ProjectileControl(target.getCenter().subtract(entity.getCenter()),
                            BULLET_MOVE_SPEED));

                    bullet.addComponent(new OwnerComponent(entity));

                    getGameWorld().addEntity(bullet);

                    entity.rotateToVector(bullet.getControl(ProjectileControl.class).get().getDirection());

                    return true;
                }
            }

            return false;
        }

        public void setTarget(Entity target) {
            this.target = target;
        }
    }

    private int toGame(double value) {
        return (int)value * TANK_MOVE_SPEED;
    }

    private int toGrid(double value) {
        return (int)value / TANK_MOVE_SPEED;
    }

    private class RandomMoveControl extends AbstractControl {

        private int targetX, targetY, toX, toY;
        private Point2D velocity = Point2D.ZERO;

        private boolean active = true;

        @Override
        protected void initEntity(Entity entity) {
            targetX = toGrid(entity.getX());
            targetY = toGrid(entity.getY());
            toX = targetX;
            toY = targetY;
        }

        @Override
        public void onUpdate(Entity entity) {
            if (!active)
                return;

            if (Math.abs(entity.getX() - toGame(toX)) >= 5
                    || Math.abs(entity.getY() - toGame(toY)) >= 5) {
                entity.translate(velocity);
                return;
            }

            if (Math.abs(entity.getX() - toGame(targetX)) < 5
                    && Math.abs(entity.getY() - toGame(targetY)) < 5) {

                Point2D randomPoint = getRandomPoint();
                targetX = (int)randomPoint.getX();
                targetY = (int)randomPoint.getY();
            }

            updateGrid(entity);
            List<AStarNode> path = grid.getPath(toGrid(entity.getX()), toGrid(entity.getY()),
                    targetX, targetY);

            if (!path.isEmpty()) {
                toX = path.get(0).getX();
                toY = path.get(0).getY();

                velocity = new Point2D(toX - toGrid(entity.getX()), toY - toGrid(entity.getY()))
                    .multiply(5);

                entity.rotateToVector(velocity);
            }
        }

        private Point2D getRandomPoint() {
            return new Point2D(random.nextInt(grid.getWidth()),
                    random.nextInt(grid.getHeight()));
        }
    }

    private class TankControl extends AbstractControl {
        private AttackControl attackControl;
        private RandomMoveControl moveControl;
        private EntityType opponentType;

        private boolean moving = true;

        @Override
        protected void initEntity(Entity entity) {
            attackControl = new AttackControl();
            moveControl = new RandomMoveControl();

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
                    attackControl.setTarget(t);

                    if (!moving)
                        return;

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

//    private class BulletControl extends AbstractControl {
//
//        private Point2D velocity;
//
//        public BulletControl(Point2D velocity) {
//            this.velocity = velocity;
//        }
//
//        @Override
//        protected void initEntity(Entity entity) {
//            entity.rotateToVector(velocity);
//        }
//
//        @Override
//        public void onUpdate(Entity entity) {
//            entity.translate(velocity);
//
//            if (entity.isOutside(0, 0, getWidth(), getHeight()))
//                entity.removeFromWorld();
//        }
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
