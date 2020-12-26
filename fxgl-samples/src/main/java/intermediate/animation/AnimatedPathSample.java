/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate.animation;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.addUINode;
import static com.almasb.fxgl.dsl.FXGL.animationBuilder;

/**
 * Shows how to animate entities/nodes along a JavaFX Shape.
 */
public class AnimatedPathSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        Rectangle node = new Rectangle(40, 40);

        addUINode(node, 100, 100);

        // create shape along which we'll animate our node
        Shape shape = new Circle(80);
        shape.setTranslateX(200);
        shape.setTranslateY(200);
        shape = Shape.subtract(new Rectangle(200, 200), shape);

        animationBuilder()
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .duration(Duration.seconds(5.0))
                .translate(node)
                .alongPath(shape)
                .buildAndPlay();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
