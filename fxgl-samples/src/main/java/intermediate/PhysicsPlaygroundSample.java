/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PhysicsPlaygroundSample extends GameApplication {

    private FloatTextField fieldFriction;
    private FloatTextField fieldDensity;
    private FloatTextField fieldRestitution;

    private ChoiceBox<ShapeType> cb;

    private enum ShapeType {
        BOX, CIRCLE, TRIANGLE
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.SECONDARY, () -> {
            spawnBlock(getInput().getMouseXWorld(), getInput().getMouseYWorld());
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        entityBuilder()
                .buildScreenBoundsAndAttach(40);
    }

    @Override
    protected void initUI() {
        fieldFriction = new FloatTextField();
        fieldDensity = new FloatTextField();
        fieldRestitution = new FloatTextField();

        cb = new ChoiceBox<>(FXCollections.observableArrayList(ShapeType.BOX, ShapeType.CIRCLE, ShapeType.TRIANGLE));
        cb.setValue(ShapeType.BOX);

        VBox box = new VBox(5,
                new Text("Friction"),
                fieldFriction,
                new Text("Density"),
                fieldDensity,
                new Text("Restitution"),
                fieldRestitution,
                new Text("Shape type"),
                cb
        );

        addUINode(new Rectangle(1280 - 1100, getAppHeight() - 250, Color.LIGHTGREY), 1100, 0);
        addUINode(box, 1100, 0);
    }

    private void spawnBlock(double x, double y) {
        var p = new PhysicsComponent();
        p.setBodyType(BodyType.DYNAMIC);
        p.setFixtureDef(new FixtureDef()
                .friction(fieldFriction.getFloat())
                .density(fieldDensity.getFloat())
                .restitution(fieldRestitution.getFloat())
        );

        BoundingShape shape;
        Node view;

        switch (cb.getValue()) {
            case BOX:
                shape = BoundingShape.box(40, 40);
                break;
            case CIRCLE:
                shape = BoundingShape.circle(20);
                break;
            case TRIANGLE:
            default:
                shape = BoundingShape.polygon(new Point2D(0, 40), new Point2D(20, 0), new Point2D(40, 40));
                break;
        }

        switch (cb.getValue()) {
            case BOX:
                view = new Rectangle(40, 40, Color.BLUE);
                ((Rectangle) view).setStroke(Color.DARKBLUE);
                break;
            case CIRCLE:
                view = new Circle(20, 20, 20, Color.YELLOW);
                ((Circle) view).setStroke(Color.ORANGE);
                break;
            case TRIANGLE:
            default:
                view = new Polygon(0, 40, 20, 0, 40, 40);
                ((Polygon) view).setFill(Color.RED);
                ((Polygon) view).setStroke(Color.DARKRED);
                break;
        }

        entityBuilder()
                .at(x, y)
                .bbox(shape)
                .view(view)
                .with(p)
                .buildAndAttach();
    }

    private static class FloatTextField extends TextField {

        FloatTextField() {
            setText("0.2");
        }

        float getFloat() {
            return Float.parseFloat(getText());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
