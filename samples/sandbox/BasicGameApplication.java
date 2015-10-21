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

import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.event.InputManager;
import com.almasb.fxgl.event.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsEntity;
import com.almasb.fxgl.physics.PhysicsManager;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.util.ApplicationMode;

import javafx.geometry.BoundingBox;
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

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Basic FXGL Application");
        settings.setVersion("0.1developer");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(true);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initInput() {
        InputManager input = getInputManager();

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
                player.translate(1, 0);
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                //enemy.setRotation(0);
                player.translate(0, -1);
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
            protected void onAction() {
                enemy.setRotation(0);
                //player.translate(0, -1);
            }
        }, KeyCode.UP);

        input.addAction(new UserAction("Rotate Down") {
            @Override
            protected void onAction() {
                enemy.setRotation(90);
                //player.translate(0, 1);
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
                b.setPosition(input.getMouse().getGameX(), input.getMouse().getGameY());
                //b.addHitBox(new HitBox("HEAD", new BoundingBox(0, 0, 40, 40)));

                FixtureDef fd = new FixtureDef();
                fd.density = 0.05f;

                b.setFixtureDef(fd);

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

                b.setOnPhysicsInitialized(() -> b.setAngularVelocity(5));
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initAssets() throws Exception {}

    @Override
    protected void initGame() {
        player = new Entity(Type.PLAYER);
        Circle graphics = new Circle(40);
        player.setSceneView(graphics);

        //player.addHitBox(new HitBox("HEAD", new BoundingBox(0, 0, 80, 80)));
        player.setPosition(100, 100);



        enemy = new Entity(Type.ENEMY);
        Rectangle enemyGraphics = new Rectangle(200, 40);
        enemyGraphics.setFill(Color.RED);
        enemy.setSceneView(enemyGraphics);

        //enemy.addHitBox(new HitBox("HEAD", new BoundingBox(0, 0, 200, 40)));
        enemy.setPosition(200, 100);



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

        getGameScene().addGameNode(new PlayerView());
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
        PhysicsManager physics = getPhysicsManager();
        physics.addCollisionHandler(new CollisionHandler(Type.PLAYER, Type.ENEMY) {
            // the order of entities determined by
            // the order of their types passed into constructor
            @Override
            protected void onCollisionBegin(Entity player, Entity enemy) {
                debug2.setText("collision");
            }

            @Override
            protected void onCollisionEnd(Entity player, Entity enemy) {
                debug2.setText("");
            }
        });
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


        getGameScene().addUINodes(debug, debug2);
    }

    @Override
    protected void onUpdate() {
        //debug.setText(enemy.getView().getBoundsInParent().toString() + " " + enemy.getX());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
