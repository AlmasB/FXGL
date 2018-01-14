/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.physics.box2d.dynamics.joints.RevoluteJoint;
import com.almasb.fxgl.physics.box2d.dynamics.joints.RevoluteJointDef;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class JointSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("JointSample");
        settings.setVersion("0.1");






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

    private RevoluteJoint joint;

    @Override
    protected void initGame() {
        getGameWorld().addEntity(Entities.makeScreenBounds(50));

        PhysicsComponent physics = new PhysicsComponent();

        Entity block = Entities.builder()
                .at(200, 200)
                .viewFromNodeWithBBox(new Rectangle(50, 50))
                .with(physics)
                .buildAndAttach(getGameWorld());

        PhysicsComponent physics2 = new PhysicsComponent();
        physics2.setBodyType(BodyType.DYNAMIC);

        FixtureDef fd = new FixtureDef();
        fd.setDensity(1.0f);
        physics2.setFixtureDef(fd);

        Entity ball =Entities.builder()
                .at(200, 300)
                .bbox(new HitBox("main", BoundingShape.circle(15)))
                .viewFromNode(getAssetLoader().loadTexture("ball.png", 30, 30))
                .with(physics2)
                .buildAndAttach(getGameWorld());


        Line line = new Line();

        line.setStartX(block.getCenter().getX());
        line.setStartY(block.getCenter().getY());

        //line.startXProperty().bind(block.getPositionComponent().xProperty());
        //line.startYProperty().bind(block.getPositionComponent().yProperty());
        line.endXProperty().bind(ball.getPositionComponent().xProperty().add(15));
        line.endYProperty().bind(ball.getPositionComponent().yProperty().add(15));

        getGameScene().addGameView(new EntityView(line));

        RevoluteJointDef rDef = new RevoluteJointDef();
        rDef.bodyA = physics.getBody();
        rDef.bodyB = physics2.getBody();
        rDef.collideConnected = false;
        rDef.localAnchorA = new Vec2(0, 0);
        rDef.localAnchorB = new Vec2(0, 5);

        rDef.enableLimit = true;
        rDef.lowerAngle = (float) (FXGLMath.toRadians(-180.0f));
        rDef.upperAngle = (float) (FXGLMath.toRadians(180.0f));

        rDef.enableMotor = true;
        rDef.motorSpeed = (float) (FXGLMath.toRadians(30));
        rDef.maxMotorTorque = 15.0f;

        joint = (RevoluteJoint) getPhysicsWorld().getJBox2DWorld().createJoint(rDef);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (joint != null && FXGLMath.isClose(FXGLMath.abs(joint.getJointAngle()), FXGLMath.toRadians(180), 0.05f)) {
            joint.setMotorSpeed(-joint.getMotorSpeed());
        }
    }

    private Entity createPhysicsEntity() {
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
