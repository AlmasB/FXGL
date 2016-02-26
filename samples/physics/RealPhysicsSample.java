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
package physics;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.BoundingBox;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * Sample that shows basic usage of the JBox2D physics engine.
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
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Spawn Box") {
            @Override
            protected void onActionBegin() {
                // 1. create game entity
                GameEntity entity = new GameEntity();
                entity.getPositionComponent().setValue(getInput().getGameXY());

                // 2. pass in "true" to allow graphics generate hit boxes based on view
                // this is a convenience method if rectangular bbox is sufficient
                entity.getMainViewComponent().setView(new EntityView(new Rectangle(40, 40, Color.BLUE)), true);

                PhysicsComponent physics = new PhysicsComponent();

                // 3. set various physics properties
                physics.setBodyType(BodyType.DYNAMIC);

                FixtureDef fd = new FixtureDef();
                fd.setDensity(0.5f);
                fd.setRestitution(0.3f);
                physics.setFixtureDef(fd);

                entity.addComponent(physics);

                entity.addControl(new ExpireCleanControl(Duration.seconds(3)));

                getGameWorld().addEntity(entity);
            }
        }, MouseButton.PRIMARY);

        input.addAction(new UserAction("Spawn Circle") {
            @Override
            protected void onActionBegin() {
                // 1. create game entity
                GameEntity entity = new GameEntity();
                entity.getPositionComponent().setValue(getInput().getGameXY());

                entity.getBoundingBoxComponent()
                        .addHitBox(new HitBox("Test", new BoundingBox(0, 0, 40, 40), BoundingShape.CIRCLE));

                // 2. pass in "true" to allow graphics generate hit boxes based on view
                // this is a convenience method if rectangular bbox is sufficient
                entity.getMainViewComponent().setView(new EntityView(new Circle(20, Color.RED)));

                PhysicsComponent physics = new PhysicsComponent();

                // 3. set various physics properties
                physics.setBodyType(BodyType.DYNAMIC);

                FixtureDef fd = new FixtureDef();
                fd.setDensity(0.5f);
                fd.setRestitution(0.3f);
                physics.setFixtureDef(fd);

                entity.addComponent(physics);

                getGameWorld().addEntity(entity);
            }
        }, MouseButton.SECONDARY);
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
    protected void onUpdate() {}

    public static void main(String[] args) {
        launch(args);
    }
}
