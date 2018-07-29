/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static com.almasb.fxgl.app.DSLKt.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class LogoSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {

    }

    @Override
    protected void initUI() {
        addUINode(
                new HBox(6,
                        makeLetter("F", Color.BLUE),
                        makeLetter("X", Color.BLUE),
                        makeLetter("G", Color.BLUE),
                        makeLetter("L", Color.BLUE)
                ),
                200, 200
        );

        addUINode(
                new HBox(6,
                        makeLetter("", Color.color(0.1, 0.1, 1, 0.58)),
                        makeLetter("", Color.color(0.1, 0.1, 1, 0.58)),
                        makeLetter("", Color.color(0.1, 0.1, 1, 0.58)),
                        makeLetter("", Color.color(0.1, 0.1, 1, 0.58))
                ),
                198, 200
        );
    }

    private Node makeLetter(String letter, Color color) {

        Rectangle rect = new Rectangle(100, 100);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setFill(null);
        rect.setStroke(color);
        rect.setStrokeWidth(3);

        Text text = getUIFactory().newText(letter, Color.color(0, 0.05, 0.05, 0.98), 78);

        StackPane pane = new StackPane(rect, text);

        //pane.setEffect(new InnerShadow(22, Color.BLUE));

        return pane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
