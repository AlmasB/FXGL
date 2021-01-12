/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.InputModifier;
import com.almasb.fxgl.input.KeyTrigger;
import com.almasb.fxgl.input.view.TriggerView;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use TriggerView for mouse buttons and key codes.
 */
public class TriggerViewSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1600);
        settings.setHeight(900);
    }

    @Override
    protected void initUI() {
        getGameScene().setBackgroundColor(Color.DARKGREY);

        HBox box1 = new HBox(5);

        char c = 'A';

        while (c != 'N') {
            box1.getChildren().add(makeView(KeyCode.getKeyCode("" + c), Color.RED, 34));
            c++;
        }

        HBox box2 = new HBox(5);

        while (c != 'Z' + 1) {
            box2.getChildren().add(makeView(KeyCode.getKeyCode("" + c), Color.BLUE, 26));
            c++;
        }

        // Text Flow

        TextFlow t1 = getUIFactoryService().newTextFlow()
                .append("Press ")
                .append(KeyCode.V, Color.LIGHTYELLOW)
                .append(" to use ")
                .append("Enhanced Vision", Color.DARKGREEN);

        TextFlow t2 = getUIFactoryService().newTextFlow()
                .append(new TriggerView(new KeyTrigger(KeyCode.A, InputModifier.CTRL))).append("   ")
                .append(new TriggerView(new KeyTrigger(KeyCode.B, InputModifier.SHIFT))).append("   ")
                .append(new TriggerView(new KeyTrigger(KeyCode.C, InputModifier.ALT))).append("   ")
                .append(new TriggerView(new KeyTrigger(KeyCode.SPACE, InputModifier.ALT))).append("   ");

        TextFlow t3 = getUIFactoryService().newTextFlow()
                .append("Hold: ")
                .append(MouseButton.PRIMARY, Color.DARKSALMON)
                .append(" and ")
                .append(MouseButton.SECONDARY, Color.LIGHTPINK)
                .append(" to shoot");

        TextFlow t4 = getUIFactoryService().newTextFlow()
                .append("Discovered Location: ")
                .append("The New Temple", Color.DARKCYAN, 34);

        addUINode(new VBox(20,
                box1,
                box2,
                makeDifferentSizeViews(KeyCode.TAB, Color.PINK),
                makeDifferentSizeViews(KeyCode.MINUS, Color.PURPLE),
                makeDifferentSizeViews(KeyCode.PLUS, Color.GRAY)
                ));

        addUINode(new VBox(20,
                t1,
                t2,
                t3,
                t4
                ),
                850, 0);
    }

    private HBox makeDifferentSizeViews(KeyCode code, Color color) {
        HBox box = new HBox(5);

        int[] sizes = new int[] { 108, 50, 34, 26, 12 };

        for (int size : sizes) {
            box.getChildren().add(makeView(code, color, size));
        }

        return box;
    }

    private Node makeView(KeyCode code, Color color, int size) {
        return new TriggerView(new KeyTrigger(code), color, size);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
