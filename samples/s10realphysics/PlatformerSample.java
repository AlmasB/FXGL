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

package s10realphysics;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * Sample that shows how to use ChainShape for platforms.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PlatformerSample extends GameApplication {

    private GameEntity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("PlatformerSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Left") {
            @Override
            protected void onActionBegin() {
                player.getComponentUnsafe(PhysicsComponent.class).setLinearVelocity(new Point2D(-200, 0));
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Right") {
            @Override
            protected void onActionBegin() {
                player.getComponentUnsafe(PhysicsComponent.class).setLinearVelocity(new Point2D(200, 0));
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                double dx = player.getComponentUnsafe(PhysicsComponent.class).getLinearVelocity().getX();

                player.getComponentUnsafe(PhysicsComponent.class).setLinearVelocity(new Point2D(dx, -100));
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Grow") {
            @Override
            protected void onActionBegin() {
                double x = player.getX();
                double y = player.getY();

                player.removeFromWorld();

                player = createPlayer(x, y, 60, 80);
            }
        }, KeyCode.SPACE);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        createPlatforms();
        player = createPlayer(100, 100, 40, 60);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    private void createPlatforms() {
        Entities.builder()
                .at(0, 500)
                .viewFromNode(new Rectangle(120, 100, Color.GRAY))
                .bbox(new HitBox("Main", BoundingShape.chain(
                        new Point2D(0, 0),
                        new Point2D(120, 0),
                        new Point2D(120, 100),
                        new Point2D(0, 100)
                )))
                .with(new PhysicsComponent())
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(180, 500)
                .viewFromNode(new Rectangle(400, 100, Color.GRAY))
                .bbox(new HitBox("Main", BoundingShape.chain(
                        new Point2D(0, 0),
                        new Point2D(400, 0),
                        new Point2D(400, 100),
                        new Point2D(0, 100)
                )))
                .with(new PhysicsComponent())
                .buildAndAttach(getGameWorld());
    }

    private GameEntity createPlayer(double x, double y, double width, double height) {
        PhysicsComponent physics = new PhysicsComponent();

        physics.setBodyType(BodyType.DYNAMIC);

        return Entities.builder()
                .at(x, y)
                .viewFromNodeWithBBox(new Rectangle(width, height, Color.BLUE))
                .with(physics)
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
