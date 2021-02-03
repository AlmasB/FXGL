/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

        VBox box = new VBox(5,
                new Text("Friction"),
                fieldFriction,
                new Text("Density"),
                fieldDensity,
                new Text("Restitution"),
                fieldRestitution
        );

        addUINode(new Rectangle(1280 - 1100, getAppHeight(), Color.LIGHTGREY), 1100, 0);
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

        entityBuilder()
                .at(x, y)
                .viewWithBBox("ghost_platform.png")
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
