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

package manual;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.asset.Assets;
import com.almasb.fxgl.asset.Texture;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.event.InputManager;
import com.almasb.fxgl.event.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsManager;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.util.ApplicationMode;
import javafx.geometry.BoundingBox;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.VerticalDirection;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SuperTextureTest extends GameApplication {

    private enum Type implements EntityType {
        PLAYER, ENEMY
    }

    private Entity player, enemy;

    private Assets assets;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setMenuEnabled(false);
        settings.setIntroEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initInput() {
        InputManager input = getInputManager();

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

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                player.translate(0, -5);
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                player.translate(0, 5);
            }
        }, KeyCode.S);
    }

    @Override
    protected void initAssets() throws Exception {
        assets = getAssetManager().cache();
        assets.logCached();
    }

    @Override
    protected void initGame() {
        player = new Entity(Type.PLAYER);
        player.setPosition(100, 100);

        Texture brick = assets.getTexture("brick.png");
        Texture brick2 = assets.getTexture("brick2.png");

        // 32x32
        // 64x64, 32x32
        player.setSceneView(brick.superTexture(brick2, HorizontalDirection.RIGHT)
                                .superTexture(brick2, VerticalDirection.UP));

        enemy = new Entity(Type.ENEMY);
        enemy.setPosition(200, 100);

        Rectangle enemyGraphics = new Rectangle(40, 40);
        enemyGraphics.setFill(Color.RED);
        enemy.setSceneView(enemyGraphics);

        // we need to set collidable to true
        // so that collision system can 'see' them
        player.setCollidable(true);
        enemy.setCollidable(true);

        player.setGenerateHitBoxesFromView(false);
        player.addHitBox(new HitBox("HEAD", new BoundingBox(0, 0, 32, 32)));
        player.addHitBox(new HitBox("ARM", new BoundingBox(64, 32, 32, 32)));
        player.addHitBox(new HitBox("BODY", new BoundingBox(0, 32, 64, 64)));

        enemy.addHitBox(new HitBox("MAIN_BODY", new BoundingBox(0, 0, 40, 40)));

        getGameWorld().addEntities(player, enemy);
    }

    @Override
    protected void initPhysics() {
        PhysicsManager physics = getPhysicsManager();
        physics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.ENEMY) {
            @Override
            protected void onHitBoxTrigger(Entity player, Entity enemy, HitBox playerBox, HitBox enemyBox) {
                System.out.println(playerBox.getName() + " X " + enemyBox.getName());
            }

            // the order of entities determined by
            // the order of their types passed into constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity enemy) {
                player.translate(-10, 0);
                enemy.translate(10, 0);
            }
        });
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void onUpdate() {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
