/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.joints;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.physics.box2d.dynamics.joints.DistanceJointDef;
import com.almasb.fxgl.physics.box2d.dynamics.joints.GearJointDef;
import com.almasb.fxgl.physics.box2d.dynamics.joints.RopeJoint;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DistanceJointSample extends GameApplication {

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

        onKeyDown(KeyCode.F, () -> {
            Entity box = createPhysicsEntity();

            box.getBoundingBoxComponent()
                    .addHitBox(new HitBox("Left", BoundingShape.box(40, 40)));
            box.getBoundingBoxComponent()
                    .addHitBox(new HitBox("Right", new Point2D(40, 0), BoundingShape.box(40, 40)));

            box.getViewComponent().addChild(texture("brick.png", 40, 40).superTexture(texture("brick.png", 40, 40), HorizontalDirection.RIGHT));
            box.setRotationOrigin(new Point2D(40, 20));
            box.setScaleX(random(0.1, 1.5));
            box.setScaleY(random(0.1, 1.5));

            getGameWorld().addEntity(box);
        });

        onBtnDown(MouseButton.SECONDARY, () -> {
            Entity ball = createPhysicsEntity();

            ball.getBoundingBoxComponent()
                    .addHitBox(new HitBox("Test", BoundingShape.circle(20)));
            ball.getViewComponent().addChild(texture("ball.png", 40, 40));
            ball.setRotationOrigin(new Point2D(20, 20));

            getGameWorld().addEntity(ball);

            joint = getPhysicsWorld().addRopeJoint(ball, theball);

            line = new Line();

            addUINode(line);

            line.startXProperty().bind(ball.xProperty().add(20));
            line.startYProperty().bind(ball.yProperty().add(20));
            line.endXProperty().bind(theball.xProperty().add(20));
            line.endYProperty().bind(theball.yProperty().add(20));
        });

        onKeyDown(KeyCode.G, () -> {
//            getPhysicsWorld().removeJoint(joint);
//
//            removeUINode(line);

        });
    }

    private Line line;

    private RopeJoint joint;
    private Entity theball;

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        entityBuilder()
                .buildScreenBoundsAndAttach(50);

        // platform

//        var platform = entityBuilder()
//                .at(400, 400)
//                .viewWithBBox(new Rectangle(500, 20, Color.BROWN))
//                .with(new PhysicsComponent())
//                .buildAndAttach();
//
//        Entity ball = createPhysicsEntity();
//        theball = ball;
//
//        ball.setPosition(400, 410);
//        ball.getBoundingBoxComponent()
//                .addHitBox(new HitBox("Test", BoundingShape.circle(20)));
//        ball.getViewComponent().addChild(texture("ball.png", 40, 40));
//        ball.setRotationOrigin(new Point2D(20, 20));
//
//        getGameWorld().addEntity(ball);
//
//        getPhysicsWorld().addPrismaticJoint(platform, ball, new Point2D(1, 0), 500 - 40);

        Entity first = null;

        for (int i = 0; i < 15; i++) {
            var b = createPhysicsEntity();
            b.setPosition(
                    270 + i * 50, 350
            );

            b.getBoundingBoxComponent()
                    .addHitBox(new HitBox("Test", BoundingShape.circle(20)));
            b.getViewComponent().addChild(texture("ball.png", 40, 40));
            b.setRotationOrigin(new Point2D(20, 20));



            if (first == null) {
                first = b;
                first.getComponent(PhysicsComponent.class).setBodyType(BodyType.STATIC);

                getGameWorld().addEntity(b);
            } else {

                if (i == 14) {
                    b.getComponent(PhysicsComponent.class).setBodyType(BodyType.STATIC);
                }

                getGameWorld().addEntity(b);




                var jointDef = new DistanceJointDef();
                jointDef.length = 1f;
                jointDef.dampingRatio = 0.5f;
                //jointDef.initialize();

                var distanceJoint = getPhysicsWorld().addJoint(first, b, jointDef);

                first = b;


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
                .view(texture("ball.png", 450 / 15.0, 449 / 15.0).multiplyColor(Color.RED))
                //.viewWithBBox(new Rectangle(random(30, 60), random(30, 60)))
                .with(physics)
                .with(new ExpireCleanComponent(Duration.seconds(5)).animateOpacity())
                .buildAndAttach();
    }

    private Entity createPhysicsEntity() {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        var bd = new BodyDef();
        bd.setFixedRotation(true);
        bd.setType(BodyType.DYNAMIC);

        //physics.setBodyDef(bd);

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
