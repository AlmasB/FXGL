/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.text.TextLevelLoader;
import com.almasb.fxgl.ui.FXGLButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import static com.almasb.fxgl.dsl.FXGL.*;
import static javafx.scene.shape.Shape.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ProgressBarApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(650);
        settings.setHeight(357);
    }

    private double t = 0.0;

    private static final double MAX = 3.0;

    private Shape shape;

    @Override
    protected void initGame() {
        var rect1 = new Rectangle(450, 45);
        rect1.setTranslateX(130);
        rect1.setTranslateY(45);

        var rect2 = new Rectangle(20, 20);
        rect2.setTranslateX(460);
        rect2.setTranslateY(90);

        var rect2mid = new Rectangle(30, 15);
        rect2mid.setTranslateX(500);
        rect2mid.setTranslateY(90);

        var rect3 = new Rectangle(20, 20);
        rect3.setTranslateX(545);
        rect3.setTranslateY(90);

        var circle1 = new Circle(70, 70, 70);
        var circle2 = new Circle(70, 70, 40);


        shape = subtract(circle1, circle2);
        shape = union(shape, rect1);
        shape = union(shape, rect2);
        shape = union(shape, rect2mid);
        shape = union(shape, rect3);



        shape.setStrokeWidth(4.5);
        shape.setStroke(Color.GRAY);



        addUINode(shape, 30, 30);
    }

    @Override
    protected void onUpdate(double tpf) {
        t += tpf;

        if (t >= MAX) {
            t = 0.0;
        }

        var offset = t / MAX;

        var gradient = new LinearGradient(0.0, 0.5, 1.0, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(offset, Color.GREEN),
                new Stop(1, Color.TRANSPARENT)
        );

        //shape.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 100%, green " + (int)((offset*100)) +  "%, transparent " + (int)((1-offset)*100) + "%);");

        shape.setStyle("-fx-fill: linear-gradient(from 0% 50% to 100% 50%, green " + (int)((offset*100)) + "%, transparent " + (int)((offset*100)) + "%);");

        //shape.setFill(gradient);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
