/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import dev.DeveloperWASDControl;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class DistanceSample extends GameApplication {

    private enum Type {
        PLAYER, ENEMY
    }

    private Text debug;

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        debug = getUIFactoryService().newText("", Color.BLACK, 24.0);

        entityBuilder()
                .type(Type.PLAYER)
                .at(100, 100)
                .viewWithBBox(new Rectangle(40, 45, Color.BLUE))
                // 2. make it collidable
                .collidable()
                // Note: in case you are copy-pasting, this class is in dev.DeveloperWASDControl
                // and enables WASD movement for testing
                .with(new DeveloperWASDControl())
                .buildAndAttach();

        entityBuilder()
                .type(Type.ENEMY)
                .at(200, 100)
                // 1. OR let the view generate it from view data
                .viewWithBBox(new Rectangle(60, 30, Color.RED))
                // 2. make it collidable
                .collidable()
                .buildAndAttach();
    }

    @Override
    protected void onUpdate(double tpf) {
        debug.setText(String.format(
                "%.0f",
                getGameWorld().getEntities().get(0).distanceBBox(getGameWorld().getEntities().get(1))
        ));
    }

    @Override
    protected void initUI() {
        addUINode(debug, 50, 50);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
