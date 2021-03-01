/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.physics.BoundingShape.box;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SnookerPhysicsSample extends GameApplication {

    private static final double TABLE_FRICTION = 0.7;

    /**
     * In kg/m^2.
     */
    private static final float BALL_DENSITY = 45f;

    /**
     * Between 0.0 and 1.0.
     */
    private static final float BALL_ELASTICITY = 0.3f;

    private static final float ANGULAR_VELOCITY_DECAY = 0.99f;

    /**
     * Ball velocity below this value will bring the ball to a full stop.
     */
    private static final double LINEAR_VELOCITY_THRESHOLD = 0.01;

    /**
     * Ball angular velocity below this value will stop the ball from rotating.
     */
    private static final double ANGULAR_VELOCITY_THRESHOLD = 0.01;

    /**
     * Any distance between cue and cue ball larger than this value is clamped to this.
     */
    private static final int MAX_CUE_FORCE_DISTANCE = 145;

    private static final int MAX_CUE_FORCE = 75000;

    private Entity cueBall;

    private Line cue;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Cue") {
            @Override
            protected void onActionBegin() {
                cue.setVisible(true);
            }

            @Override
            protected void onActionEnd() {
                cue.setVisible(false);

                var physics = cueBall.getComponent(PhysicsComponent.class);

                Point2D force = cueBall.getCenter().subtract(getInput().getMousePositionWorld());

                double forcePower = Math.min(force.magnitude(), MAX_CUE_FORCE_DISTANCE);
                double forceRatio = forcePower / MAX_CUE_FORCE_DISTANCE;

                physics.applyLinearImpulse(force.normalize().multiply(forceRatio * MAX_CUE_FORCE), cueBall.getCenter(), true);
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);
        getPhysicsWorld().setGravity(0, 0);

        spawnTable(200, 150);

        cueBall = spawnBall(220, getAppHeight() / 2 - 15, Color.WHITE);

        for (int x = 1; x <= 5; x++) {
            int numBalls = x;

            for (int y = 1; y <= numBalls; y++) {
                int offsetY = getAppHeight() / 2 - numBalls * 15;

                spawnBall(800 + x * 32, offsetY + (y-1) * 32, Color.RED);
            }
        }

        spawnBall(450, getAppHeight() / 2 - 100 - 15, Color.GREEN);
        spawnBall(450, getAppHeight() / 2 - 15, Color.BROWN);
        spawnBall(450, getAppHeight() / 2 + 100 - 15, Color.YELLOW);

        spawnBall(getAppWidth() / 2 - 15, getAppHeight() / 2 - 15, Color.BLUE);
        spawnBall(750, getAppHeight() / 2 - 15, Color.PINK);
        spawnBall(800 + 200, getAppHeight() / 2 - 15, Color.color(0.1, 0.1, 0.1));

        cue = new Line();
        cue.setVisible(false);
        cue.setStrokeWidth(2);
        cue.startXProperty().bind(cueBall.xProperty().add(15));
        cue.startYProperty().bind(cueBall.yProperty().add(15));
        cue.endXProperty().bind(getInput().mouseXWorldProperty());
        cue.endYProperty().bind(getInput().mouseYWorldProperty());

        addUINode(cue);
    }

    private Entity spawnTable(double x, double y) {
        var rect = new Rectangle(getAppWidth() - 200 - 200, getAppHeight() - 150 - 150, Color.LIGHTSEAGREEN);
        rect.setArcWidth(15);
        rect.setArcHeight(15);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(10);
        rect.setStrokeType(StrokeType.OUTSIDE);

        return entityBuilder()
                .at(x, y)
                .bbox(new HitBox(new Point2D(-40, 0), box(40, rect.getHeight())))
                .bbox(new HitBox(new Point2D(rect.getWidth(), 0), box(40, rect.getHeight())))
                .bbox(new HitBox(new Point2D(0, -40), box(rect.getWidth(), 40)))
                .bbox(new HitBox(new Point2D(0, rect.getHeight()), box(rect.getWidth(), 40)))
                .view(rect)
                .with(new PhysicsComponent())
                .buildAndAttach();
    }

    private Entity spawnBall(double x, double y, Color color) {
        var bd = new BodyDef();
        bd.setBullet(true);
        bd.setType(BodyType.DYNAMIC);

        var p = new PhysicsComponent();
        p.setBodyDef(bd);
        p.setFixtureDef(new FixtureDef().density(BALL_DENSITY).restitution(BALL_ELASTICITY));

        var shadow = new InnerShadow(3, Color.BLACK);
        shadow.setOffsetX(-3);
        shadow.setOffsetY(-3);

        var c = new Circle(15, 15, 15, color);
        c.setEffect(shadow);

        var shine = new Circle(3, 3, 3, Color.color(0.7, 0.7, 0.7, 0.7));
        shine.setTranslateX(5);
        shine.setTranslateY(5);

        return entityBuilder()
                .at(x, y)
                .bbox(new HitBox(BoundingShape.circle(15)))
                .view(c)
                .view(shine)
                .with(p)
                .with(new BallComponent())
                .buildAndAttach();
    }

    private static class BallComponent extends Component {
        private PhysicsComponent p;

        @Override
        public void onUpdate(double tpf) {
            // we need to apply table friction manually since the ball is effectively moving in 3D
            // but the physics engine only knows about 2 dimensions
            double frictionPerFrame = TABLE_FRICTION * tpf;

            var vx = p.getVelocityX();
            var vy = p.getVelocityY();

            if (FXGLMath.abs(vx) > LINEAR_VELOCITY_THRESHOLD) {
                vx -= vx * frictionPerFrame;
                p.setVelocityX(vx);
            } else {
                p.setVelocityX(0);
            }

            if (FXGLMath.abs(vy) > LINEAR_VELOCITY_THRESHOLD) {
                vy -= vy * frictionPerFrame;
                p.setVelocityY(vy);
            } else {
                p.setVelocityY(0);
            }

            var a = p.getBody().getAngularVelocity();

            if (FXGLMath.abs(a) > ANGULAR_VELOCITY_THRESHOLD) {
                p.getBody().setAngularVelocity(a * ANGULAR_VELOCITY_DECAY);
            } else {
                p.getBody().setAngularVelocity(0);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
