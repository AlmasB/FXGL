/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package manual.loading;

import com.almasb.fxgl.scene.LoadingScene;
import javafx.animation.RotateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TestLoadingScene extends LoadingScene {

    public TestLoadingScene() {

        getText().setFont(Font.font("Segoe UI", 24));
        getText().setTranslateY(50);

        Circle circle = new Circle(50, 50, 50);

        Shape shape = Shape.subtract(new Rectangle(100, 100), circle);
        shape.setFill(Color.BLUE);
        shape.setStroke(Color.YELLOW);

        RotateTransition rt = new RotateTransition(Duration.seconds(2), shape);
        rt.setByAngle(360);
        rt.setCycleCount(15);
        rt.play();

        shape.setTranslateX(700);
        shape.setTranslateY(500);

        getContentRoot().getChildren().set(1, shape);
    }
}
