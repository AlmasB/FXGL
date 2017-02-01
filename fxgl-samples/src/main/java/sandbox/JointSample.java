/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

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
                GameEntity box = createPhysicsEntity();

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
                GameEntity ball = createPhysicsEntity();

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

        GameEntity block = Entities.builder()
                .at(200, 200)
                .viewFromNodeWithBBox(new Rectangle(50, 50))
                .with(physics)
                .buildAndAttach(getGameWorld());

        PhysicsComponent physics2 = new PhysicsComponent();
        physics2.setBodyType(BodyType.DYNAMIC);
        physics2.setOnPhysicsInitialized(() -> {
            RevoluteJointDef rDef = new RevoluteJointDef();
            rDef.bodyA = physics.getBody();
            rDef.bodyB = physics2.getBody();
            rDef.collideConnected = false;
            rDef.localAnchorA = new Vec2(0, 0);
            rDef.localAnchorB = new Vec2(0, 5);

            rDef.enableLimit = true;
            rDef.lowerAngle = FXGLMath.degreesToRadians * -180.0f;
            rDef.upperAngle = FXGLMath.degreesToRadians * 180.0f;

            rDef.enableMotor = true;
            rDef.motorSpeed = FXGLMath.degreesToRadians * 30;
            rDef.maxMotorTorque = 15.0f;

            joint = (RevoluteJoint) getPhysicsWorld().getJBox2DWorld().createJoint(rDef);
        });

        FixtureDef fd = new FixtureDef();
        fd.setDensity(1.0f);
        physics2.setFixtureDef(fd);

        GameEntity ball =Entities.builder()
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

        getGameScene().addGameView(new EntityView(line, RenderLayer.BACKGROUND));
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void onUpdate(double tpf) {
        if (joint != null && FXGLMath.isEqual(FXGLMath.abs(joint.getJointAngle()), FXGLMath.degreesToRadians * 180.0f, 0.05f)) {
            joint.setMotorSpeed(-joint.getMotorSpeed());
        }
    }

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
