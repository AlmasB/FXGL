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

package games.spaceinvaders;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.asset.Texture;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.gameplay.Achievement;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.KeyCode;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SpaceInvadersApp extends GameApplication {

    public enum Type implements EntityType {
        PLAYER, ENEMY, PLAYER_BULLET, ENEMY_BULLET
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Space Invaders");
        settings.setVersion("0.1dev");
        settings.setWidth(600);
        settings.setHeight(800);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(false);
    }

    @Override
    protected void initAchievements() {
        Achievement a = new Achievement("Hitman", "Destroy 5 enemies");

        getAchievementManager().registerAchievement(a);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                player.translate(-5, 0);
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                player.translate(5, 0);
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                shoot();
            }
        }, KeyCode.F);
    }

    @Override
    protected void initAssets() {

    }

    private Entity player;
    private IntegerProperty enemiesDestroyed;

    @Override
    protected void initGame() {
        enemiesDestroyed = new SimpleIntegerProperty(0);

        getAchievementManager().getAchievementByName("Hitman")
                .achievedProperty().bind(enemiesDestroyed.greaterThanOrEqualTo(5));

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 8; x++) {
                spawnEnemy(x * (40 + 20), y * (40 + 20));
            }
        }

        spawnPlayer();
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physicsWorld = getPhysicsWorld();

        physicsWorld.addCollisionHandler(new CollisionHandler(Type.ENEMY_BULLET, Type.PLAYER) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity player) {
                bullet.removeFromWorld();
                log.info("Player got hit");
            }
        });

        physicsWorld.addCollisionHandler(new CollisionHandler(Type.PLAYER_BULLET, Type.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                bullet.removeFromWorld();
                enemy.removeFromWorld();
                enemiesDestroyed.set(enemiesDestroyed.get() + 1);
            }
        });
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void onUpdate() {

    }

    private void spawnEnemy(double x, double y) {
        Entity enemy = new Entity(Type.ENEMY);
        enemy.setPosition(x, y);

        Texture texture = getAssetLoader().loadTexture("tank_enemy.png");
        texture.setFitWidth(40);
        texture.setFitHeight(40);

        enemy.setSceneView(texture);
        enemy.setCollidable(true);
        enemy.setRotation(90);
        enemy.addControl(new EnemyControl());

        getGameWorld().addEntity(enemy);
    }

    private void spawnPlayer() {
        player = new Entity(Type.PLAYER);
        player.setPosition(getWidth() / 2 - 20, getHeight() - 40);

        Texture texture = getAssetLoader().loadTexture("tank_player.png");
        texture.setFitWidth(40);
        texture.setFitHeight(40);

        player.setSceneView(texture);
        player.setCollidable(true);
        player.setRotation(-90);

        getGameWorld().addEntity(player);
    }

    private void shoot() {
        Entity bullet = new Entity(Type.PLAYER_BULLET);
        bullet.setPosition(player.getCenter().add(0, player.getHeight() / 2));
        bullet.setCollidable(true);
        bullet.setSceneView(getAssetLoader().loadTexture("tank_bullet.png"));
        bullet.addControl(new BulletControl());

        getGameWorld().addEntity(bullet);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
