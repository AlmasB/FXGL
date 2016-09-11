/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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
package s4physics;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import common.PlayerControl;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Shows how to rotation affects collisions.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PhysicsSample2 extends GameApplication {

    private enum Type {
        PLAYER, ENEMY
    }

    private GameEntity player, enemy;
    private PlayerControl playerControl;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("PhysicsSample2");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                playerControl.up();
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                playerControl.down();
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Rotate CCW") {
            @Override
            protected void onAction() {
                player.rotateBy(-5);
            }
        }, KeyCode.Q);

        input.addAction(new UserAction("Rotate CW") {
            @Override
            protected void onAction() {
                player.rotateBy(5);
            }
        }, KeyCode.E);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        playerControl = new PlayerControl();

        player = Entities.builder()
                .type(Type.PLAYER)
                .at(100, 100)
                // 1. define hit boxes manually
                .bbox(new HitBox("PLAYER_BODY", BoundingShape.box(250, 40)))
                .viewFromNode(new Rectangle(250, 40, Color.BLUE))
                .with(playerControl)
                .build();

        enemy = Entities.builder()
                .type(Type.ENEMY)
                .at(200, 100)
                // OR let the view generate it from view data
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.RED))
                .build();

        // 2. we need to add Collidable component and set its value to true
        // so that collision system can 'see' our entities
        player.addComponent(new CollidableComponent(true));
        enemy.addComponent(new CollidableComponent(true));

        getGameWorld().addEntities(player, enemy);
    }

    private boolean isColliding = false;

    @Override
    protected void initPhysics() {
        // 3. get physics world and register a collision handler
        // between Type.PLAYER and Type.ENEMY

        PhysicsWorld physics = getPhysicsWorld();

        physics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.ENEMY) {
            @Override
            protected void onCollision(Entity player, Entity enemy) {
                isColliding = true;
            }
        });
    }

    private Text text;

    @Override
    protected void initUI() {
        text = getUIFactory().newText("");
        text.setTranslateX(500);
        text.setTranslateY(50);
        text.setFill(Color.BLACK);

        getGameScene().addUINode(text);
    }

    @Override
    protected void onUpdate(double tpf) {
        text.setText(player.getRotation() + " deg " + (isColliding ? "Colliding" : ""));

        isColliding = false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
