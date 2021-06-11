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
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.physics.box2d.dynamics.joints.ConstantVolumeJointDef;
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
 * Shows how to use RevoluteJoints with PhysicsComponent.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RevoluteJointSample extends GameApplication {

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

//        onBtnDown(MouseButton.PRIMARY, () -> {
//            Entity box = createPhysicsEntity();
//
//            box.getBoundingBoxComponent()
//                    .addHitBox(new HitBox("Left", BoundingShape.box(40, 40)));
//            box.getBoundingBoxComponent()
//                    .addHitBox(new HitBox("Right", new Point2D(40, 0), BoundingShape.box(40, 40)));
//
//            box.getViewComponent().addChild(texture("brick.png", 40, 40).superTexture(texture("brick.png", 40, 40), HorizontalDirection.RIGHT));
//            box.setRotationOrigin(new Point2D(40, 20));
//
//            getGameWorld().addEntity(box);
//        });

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
        physics.setFixtureDef(new FixtureDef().density(1.1f));
        physics.setBodyType(BodyType.DYNAMIC);

        Entity block = entityBuilder()
                .at(600, 100)
                .viewWithBBox(new Rectangle(80, 50))
                .with(physics)
                .buildAndAttach();

        PhysicsComponent physics2 = new PhysicsComponent();
        physics2.setBodyType(BodyType.DYNAMIC);

        FixtureDef fd = new FixtureDef();
        fd.setDensity(1.0f);
        physics2.setFixtureDef(fd);

        Entity ball1 = entityBuilder()
                .at(600, 360)
                .bbox(new HitBox("main", BoundingShape.circle(15)))
                .view(texture("ball.png", 30, 30))
                .with(physics2)
                .buildAndAttach();

        PhysicsComponent physics3 = new PhysicsComponent();
        physics3.setBodyType(BodyType.DYNAMIC);

        physics3.setFixtureDef(fd);

        Entity ball2 = entityBuilder()
                .at(700, 360)
                .bbox(new HitBox("main", BoundingShape.circle(15)))
                .view(texture("ball.png", 30, 30))
                .with(physics3)
                .buildAndAttach();

        Line line = new Line();

        line.setStartX(block.getCenter().getX());
        line.setStartY(block.getCenter().getY());

        //line.startXProperty().bind(block.getPositionComponent().xProperty());
        //line.startYProperty().bind(block.getPositionComponent().yProperty());
        line.endXProperty().bind(ball1.xProperty().add(15));
        line.endYProperty().bind(ball1.yProperty().add(15));

        //getGameScene().addGameView(new GameView(line, -10));

        RevoluteJointDef rDef = new RevoluteJointDef();

        //rDef.initialize(physics.getBody(), physics2.getBody(), getPhysicsWorld().toPoint(new Point2D(block.getRightX() - 50, block.getBottomY() - 50)));

        rDef.setBodyA(physics.getBody());
        rDef.setBodyB(physics2.getBody());
        rDef.localAnchorA = getPhysicsWorld().toPoint(new Point2D(block.getRightX(), block.getBottomY())).subLocal(physics.getBody().getWorldCenter());
        rDef.localAnchorB = new Vec2(0, 0);

//        rDef.enableLimit = true;
//        rDef.lowerAngle = (float) (toRadians(-50.0f));
//        rDef.upperAngle = (float) (toRadians(50.0f));

        //rDef.enableMotor = true;
        //rDef.motorSpeed = (float) (toRadians(60));
        //rDef.maxMotorTorque = 1.0f;

        joint = getPhysicsWorld().getJBox2DWorld().createJoint(rDef);

        //rDef.initialize(physics.getBody(), physics3.getBody(), getPhysicsWorld().toPoint(new Point2D(block.getX(), block.getBottomY() - 50)));
        //getPhysicsWorld().getJBox2DWorld().createJoint(rDef);


        physics2.getBody().setAngularDamping(1f);
        physics3.getBody().setAngularDamping(1f);




        rDef.setBodyA(physics.getBody());
        rDef.setBodyB(physics3.getBody());
        rDef.localAnchorA = getPhysicsWorld().toPoint(new Point2D(block.getX(), block.getBottomY())).subLocal(physics.getBody().getWorldCenter());
        rDef.localAnchorB = new Vec2(0, 0);

        joint = getPhysicsWorld().getJBox2DWorld().createJoint(rDef);

        run(() -> {

            //joint.setMotorSpeed((float) toRadians(60));
            //joint.setMotorSpeed(-joint.getMotorSpeed());
        }, Duration.seconds(1));

        // platform

        entityBuilder()
                .at(400, 400)
                .viewWithBBox(new Rectangle(500, 20, Color.BROWN))
                .with(new PhysicsComponent())
                .buildAndAttach();


        RevoluteJoint j = getPhysicsWorld().addJoint(ball1, ball2, new RevoluteJointDef());

        //new ConstantVolumeJointDef().createJoint();
    }

    @Override
    protected void onUpdate(double tpf) {
//        if (joint != null && abs(abs(joint.getJointAngle()) - toRadians(90)) < 0.05f) {
//            joint.setMotorSpeed(-joint.getMotorSpeed());
//        }
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
