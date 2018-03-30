/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s09advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.extra.entity.components.RandomMoveComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use Perlin noise.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class NoiseSample extends GameApplication {

    private static final int X_MAX = 200;
    private static final int Y_MAX = 150;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("NoiseSample");
        settings.setVersion("0.1");



    }

    private Rectangle rect;

    @Override
    protected void initGame() {
        Entities.builder()
                .at(100, 100)
                .viewFromNodeWithBBox(new Rectangle(40, 40))
                .with(new RandomMoveComponent(100, 50, 350, new Rectangle2D(0, 0, X_MAX - 0, Y_MAX - 0)))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(50, 350)
                .viewFromNode(new Rectangle(15, 15, Color.DARKCYAN))
                .buildAndAttach(getGameWorld());
    }

    @Override
    protected void initUI() {
        rect = new Rectangle(100, 100);
        rect.setTranslateX(350);

        Line l1 = new Line(0, Y_MAX, 500, Y_MAX);
        Line l2 = new Line(X_MAX, 0, X_MAX, 600);

        getGameScene().addUINodes(rect, l1, l2);
    }

    private float t = 0;

    @Override
    protected void onUpdate(double tpf) {
        double n = FXGLMath.noise1D(t);

        // flickering rect
        rect.setFill(Color.color(1.0, n, n));

        t += tpf;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
