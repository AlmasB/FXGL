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
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PolygonHitBox;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * Sample that shows basic usage of the JBox2D physics engine
 * via PhysicsComponent.
 * Left click will spawn a box, right - ball.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RealPhysicsSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("RealPhysicsSample");
        settings.setVersion("0.1developer");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Spawn Box") {
            @Override
            protected void onActionBegin() {
                GameEntity box = createPhysicsEntity();

                // 3. use true flag to generate bbox from the view
                // bbox shape is rectangular
                box.getMainViewComponent().setView(new Rectangle(40, 40, Color.BLUE), true);

                getGameWorld().addEntity(box);
            }
        }, MouseButton.PRIMARY);

        input.addAction(new UserAction("Spawn Ball") {
            @Override
            protected void onActionBegin() {
                GameEntity ball = createPhysicsEntity();

                // 3. OR set hit box manually to specify bounding shape
                ball.getBoundingBoxComponent()
                        .addHitBox(new HitBox("Test", new BoundingBox(0, 0, 40, 40), BoundingShape.CIRCLE));
                ball.getMainViewComponent().setView(new Circle(20, Color.RED));

                getGameWorld().addEntity(ball);
            }
        }, MouseButton.SECONDARY);

        input.addAction(new UserAction("Spawn Polygon") {
            @Override
            protected void onActionBegin() {
                GameEntity polygon = createPhysicsEntity();

                int size = 40;

                // 3. OR set hit box manually to specify bounding shape
                polygon.getBoundingBoxComponent()
                        //.addHitBox(new HitBox("Test", new BoundingBox(0, 0, 60, 60), BoundingShape.BOX));
                        .addHitBox(new PolygonHitBox(
                                new Point2D(0, 0),
                                new Point2D(size, 0),
                                new Point2D(size, size),
                                new Point2D(size * 2, size),
                                new Point2D(size * 2, size * 2),
                                new Point2D(0, size * 2)
                        ));

                EntityView view = new EntityView();
                view.addNode(new Line(0, 0, size, 0));
                view.addNode(new Line(size, 0, size, size));
                view.addNode(new Line(size, size, size * 2, size));
                view.addNode(new Line(size * 2, size, size * 2, size * 2));
                view.addNode(new Line(size * 2, size * 2, 0, size * 2));
                view.addNode(new Line(0, size * 2, 0, 0));

                polygon.getMainViewComponent().setView(view);

                getGameWorld().addEntity(polygon);
            }
        }, KeyCode.F);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        getGameWorld().addEntity(Entities.makeScreenBounds(50));
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    private GameEntity createPhysicsEntity() {
        // 1. create and configure physics component
        PhysicsComponent physics = new PhysicsComponent();

        physics.setBodyType(BodyType.DYNAMIC);

        FixtureDef fd = new FixtureDef();
        fd.setDensity(0.7f);
        fd.setRestitution(0.3f);
        physics.setFixtureDef(fd);

        return Entities.builder()
                .at(getInput().getMousePositionWorld())
                // 2. add physics component
                .with(physics)
                .build();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
