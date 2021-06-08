/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.physics.box2d.dynamics.joints.RevoluteJoint;
import com.almasb.fxgl.physics.box2d.dynamics.joints.RevoluteJointDef;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.core.math.FXGLMath.toRadians;
import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use Joints with PhysicsComponent.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class JointSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.PRIMARY, () -> {
            Entity box = createPhysicsEntity();

            box.getBoundingBoxComponent()
                    .addHitBox(new HitBox("Left", BoundingShape.box(40, 40)));
            box.getBoundingBoxComponent()
                    .addHitBox(new HitBox("Right", new Point2D(40, 0), BoundingShape.box(40, 40)));

            box.getViewComponent().addChild(texture("brick.png", 40, 40).superTexture(texture("brick.png", 40, 40), HorizontalDirection.RIGHT));
            box.setRotationOrigin(new Point2D(40, 20));

            getGameWorld().addEntity(box);
        });

        onBtnDown(MouseButton.SECONDARY, () -> {
            Entity ball = createPhysicsEntity();

            ball.getBoundingBoxComponent()
                    .addHitBox(new HitBox("Test", BoundingShape.circle(20)));
            ball.getViewComponent().addChild(texture("ball.png", 40, 40));
            ball.setRotationOrigin(new Point2D(20, 20));

            getGameWorld().addEntity(ball);
        });
    }

    private RevoluteJoint joint;

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        entityBuilder()
                .buildScreenBoundsAndAttach(50);

        PhysicsComponent physics = new PhysicsComponent();

        Entity block = entityBuilder()
                .at(300, 300)
                .viewWithBBox(new Rectangle(50, 50))
                .with(physics)
                .buildAndAttach();

        PhysicsComponent physics2 = new PhysicsComponent();
        physics2.setBodyType(BodyType.DYNAMIC);

        FixtureDef fd = new FixtureDef();
        fd.setDensity(1.0f);
        physics2.setFixtureDef(fd);

        Entity ball = entityBuilder()
                .at(300, 360)
                .bbox(new HitBox("main", BoundingShape.circle(15)))
                .view(texture("ball.png", 30, 30))
                .with(physics2)
                .buildAndAttach();

        Line line = new Line();

        line.setStartX(block.getCenter().getX());
        line.setStartY(block.getCenter().getY());

        //line.startXProperty().bind(block.getPositionComponent().xProperty());
        //line.startYProperty().bind(block.getPositionComponent().yProperty());
        line.endXProperty().bind(ball.xProperty().add(15));
        line.endYProperty().bind(ball.yProperty().add(15));

        getGameScene().addGameView(new GameView(line, -10));

        RevoluteJointDef rDef = new RevoluteJointDef();
        rDef.bodyA = physics.getBody();
        rDef.bodyB = physics2.getBody();
        rDef.collideConnected = false;
        rDef.localAnchorA = new Vec2(0, 0);
        rDef.localAnchorB = new Vec2(0, 5);

        rDef.enableLimit = true;
        rDef.lowerAngle = (float) (toRadians(-50.0f));
        rDef.upperAngle = (float) (toRadians(50.0f));

        rDef.enableMotor = true;
        rDef.motorSpeed = (float) (toRadians(60));
        rDef.maxMotorTorque = 15.0f;

        joint = (RevoluteJoint) getPhysicsWorld().getJBox2DWorld().createJoint(rDef);

        run(() -> {
            joint.setMotorSpeed(-joint.getMotorSpeed());
        }, Duration.seconds(1));
    }

    @Override
    protected void onUpdate(double tpf) {
//        if (joint != null && abs(abs(joint.getJointAngle()) - toRadians(90)) < 0.05f) {
//            joint.setMotorSpeed(-joint.getMotorSpeed());
//        }
    }

    private Entity createPhysicsEntity() {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setFixtureDef(new FixtureDef().density(0.1f).restitution(0.3f));

        return entityBuilder()
                .at(getInput().getMousePositionWorld())
                .with(physics)
                .build();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
