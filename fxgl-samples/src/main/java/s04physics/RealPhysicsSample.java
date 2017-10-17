/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s04physics;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

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
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Spawn Box") {
            @Override
            protected void onActionBegin() {
                Entity box = createPhysicsEntity();

                // 3. set hit box (-es) to specify bounding shape
                box.getBoundingBoxComponent()
                        .addHitBox(new HitBox("Left", BoundingShape.box(40, 40)));
                box.getBoundingBoxComponent()
                        .addHitBox(new HitBox("Right", new Point2D(40, 0), BoundingShape.box(40, 40)));

                box.getViewComponent().setView(new Rectangle(80, 40, Color.BLUE));

                getGameWorld().addEntity(box);
            }
        }, MouseButton.PRIMARY);

        input.addAction(new UserAction("Spawn Ball") {
            @Override
            protected void onActionBegin() {
                Entity ball = createPhysicsEntity();

                // 3. set hit box to specify bounding shape
                ball.getBoundingBoxComponent()
                        .addHitBox(new HitBox("Test", BoundingShape.circle(20)));
                ball.getViewComponent().setView(new Circle(20, Color.RED));

                getGameWorld().addEntity(ball);
            }
        }, MouseButton.SECONDARY);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntity(Entities.makeScreenBounds(50));
    }

    private Entity createPhysicsEntity() {
        // 1. create and configure physics component
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setFixtureDef(new FixtureDef().density(0.7f).restitution(0.3f));

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
