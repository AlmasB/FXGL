/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RigidBodyPhysicsSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("LMB") {
            private double x;
            private double y;

            @Override
            protected void onActionBegin() {
                x = getInput().getMouseXWorld();
                y = getInput().getMouseYWorld();
            }

            @Override
            protected void onActionEnd() {
                var endx = getInput().getMouseXWorld();
                var endy = getInput().getMouseYWorld();

                spawnBullet(x, y, endx - x, endy - y);
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        entityBuilder()
                .buildScreenBoundsAndAttach(40);

        for (int y = 9; y <= 20; y++) {
            for (int x = 30; x < 51; x++) {
                spawnVertical(x * 25, y * 35 - 10);
            }
        }

        for (int y = 9; y <= 20; y++) {
            for (int x = 10; x < 18; x++) {
                spawnHorizontal(x * 64 + 105, y * 35 - 10 - 5);
            }
        }
    }

    private void spawnBullet(double x, double y, double vx, double vy) {
        var physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().density(25.5f).restitution(0.5f));
        physics.setBodyType(BodyType.DYNAMIC);

        physics.setOnPhysicsInitialized(() -> {
            physics.setLinearVelocity(vx * 10, vy * 10);
        });

        entityBuilder()
                .at(x, y)
                .bbox(new HitBox(BoundingShape.circle(450 / 15.0 / 2.0)))
                .view(texture("ball.png", 450 / 15.0, 449 / 15.0))
                .with(physics)
                .with(new ExpireCleanComponent(Duration.seconds(5)).animateOpacity())
                .buildAndAttach();
    }

    private void spawnHorizontal(double x, double y) {
        var physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().density(4.5f).friction(1.0f).restitution(0.05f));
        physics.setBodyType(BodyType.DYNAMIC);

        var t = texture("brick.png").subTexture(new Rectangle2D(0, 0, 64, 5));

        entityBuilder()
                .at(x, y)
                .viewWithBBox(t)
                .with(physics)
                .buildAndAttach();
    }

    private void spawnVertical(double x, double y) {
        var physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().density(6.5f).friction(1.0f).restitution(0.05f));
        physics.setBodyType(BodyType.DYNAMIC);

        var t = texture("brick.png")
                .subTexture(new Rectangle2D(0, 0, 10, 30))
                .multiplyColor(Color.RED);

        entityBuilder()
                .at(x, y)
                .viewWithBBox(t)
                .with(physics)
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
