/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */


package basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.collision.shapes.MassData;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to change mass within a body directly
 * and the affect this has on general physics.
 *
 * demonstrated using a seesaw, you can see
 * how objects of different masses interact with
 * their environment differently.
 *
 * @author Issy Kelly @IssyKelly
 */
public class MassSample extends GameApplication {

    int mass = 1;
    Text uiText;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        // click to add a new ball of the shown (on screen) mass
        getInput().addAction(new UserAction("LMB") {
            private double x;
            private double y;

            @Override
            protected void onActionBegin() {
                x = getInput().getMouseXWorld();
                y = getInput().getMouseYWorld();
                newBall(mass, x, y);
            }
        }, MouseButton.PRIMARY);

        // press E to increase the mass of new balls by 1
        onKeyDown(KeyCode.E, () -> {
            mass += 1;
            uiText.setText(mass + " mass");
        });

        // press P to increase the mass of new balls by 10
        onKeyDown(KeyCode.P, () -> {
            mass += 10;
            uiText.setText(mass + " mass");
        });

        // press Q to decrease the mass of new balls by 1
        onKeyDown(KeyCode.Q, () -> {
            mass -= 1;
            uiText.setText(mass + " mass");
        });

        // press O to decrease the mass of new balls by 10
        onKeyDown(KeyCode.O, () -> {
            mass -= 10;
            uiText.setText(mass + " mass");
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        uiText = new Text(mass + " mass");
        Font uifont = new Font(40);
        uiText.setFont(uifont);
        FXGL.addUINode(uiText, 600, 100);

        //floor
        Entity floor = entityBuilder()
                .at(0, 700)
                .viewWithBBox(new Rectangle(1450, 10, Color.BLACK))
                .with(new PhysicsComponent())
                .buildAndAttach();

        // balls for right hand seesaw
        newBall(100, 1050, 300);
        newBall(10, 700,300);

        // balls for left hand side seesaw
        newBall(20, 170, 300);
        newBall(10, 300,300);

        newSeesaw(250,600, 1);
        newSeesaw(900,550, 1.4);
    }

    public void newSeesaw(int x, int y, double size){
        int circum = (int) (10 * size);
        int rectx = (int) (400 * size);

        Entity circle = entityBuilder()
                .at(x, y)
                .viewWithBBox(new Circle(circum, Color.BLACK))
                .with(new PhysicsComponent())
                .buildAndAttach();

        circle.getComponent(PhysicsComponent.class)
                .setBodyType(BodyType.STATIC);

        PhysicsComponent plankPhysics = new PhysicsComponent();
        plankPhysics.setBodyType(BodyType.DYNAMIC);
        FixtureDef fd = new FixtureDef();
        fd.setDensity(5.0f);
        plankPhysics.setFixtureDef(fd);

        Entity plank = entityBuilder()
                .at(x, y)
                .viewWithBBox(new Rectangle(rectx, 20, Color.BROWN))
                .with(plankPhysics)
                .buildAndAttach();

        Point2D anchor = new Point2D((circum/2), (circum/2));
        Point2D anchor2 = new Point2D((rectx/2), (20/2));

        getPhysicsWorld().addRevoluteJoint(
                circle,
                plank,
                anchor,
                anchor2
        );
    }

    public void newBall(int mass, double x, double y){
        var physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().density(25.5f).restitution(0.5f));
        physics.setBodyType(BodyType.DYNAMIC);

        MassData massValue = new MassData();
        // the code to directly change the mass of a body
        massValue.mass = mass;
        physics.setOnPhysicsInitialized(() -> physics.getBody().setMassData(massValue));

        entityBuilder()
                .at(x, y)
                .bbox(new HitBox(BoundingShape.circle(450 / 15.0 / 2.0)))
                .view(texture("ball.png", 450 / 15.0, 449 / 15.0).multiplyColor(Color.BLUE))
                .with(physics)
                .buildAndAttach();
    }

    static void main(String[] args) {
        launch(args);
    }
}