/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.view;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.InputModifier;
import com.almasb.fxgl.input.KeyTrigger;
import com.almasb.fxgl.input.view.TriggerView;
import com.almasb.fxgl.ui.FXGLTextFlow;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;

import static com.almasb.fxgl.dsl.FXGL.addUINode;
import static com.almasb.fxgl.dsl.FXGL.getUIFactoryService;

public class ColorableTextSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1700);
        settings.setHeight(900);
    }

    @Override
    protected void initUI() {
        HBox box = new HBox(5);

        char c = 'A';

        while (c != 'N') {
            addKey(box.getChildren(), KeyCode.getKeyCode("" + c), Color.RED, 34);
            c++;
        }

        HBox box2 = new HBox(5);

        while (c != 'Z' + 1) {
            addKey(box2.getChildren(), KeyCode.getKeyCode("" + c), Color.BLUE, 26);
            c++;
        }

        // Text Flow

        TextFlow t1 = getUIFactoryService().newTextFlow()
                .append("Press ")
                .append(KeyCode.V, Color.LIGHTYELLOW)
                .append(" to use ")
                .append("Enhanced Vision", Color.DARKGREEN);

        FXGLTextFlow t2 = getUIFactoryService().newTextFlow();
        t2.append(new TriggerView(new KeyTrigger(KeyCode.A, InputModifier.CTRL))).append("   ");
        t2.append(new TriggerView(new KeyTrigger(KeyCode.B, InputModifier.SHIFT))).append("   ");
        t2.append(new TriggerView(new KeyTrigger(KeyCode.C, InputModifier.ALT))).append("   ");
        t2.append(new TriggerView(new KeyTrigger(KeyCode.SPACE, InputModifier.ALT))).append("   ");

        FXGLTextFlow t3 = getUIFactoryService().newTextFlow();
        t3.append("Hold: ")
                .append(MouseButton.PRIMARY, Color.DARKSALMON)
                .append(" and ")
                .append(MouseButton.SECONDARY, Color.LIGHTPINK)
                .append(" to shoot");

        TextFlow t4 = getUIFactoryService().newTextFlow()
                .append("Discovered Location: ")
                .append("The New Temple", Color.DARKCYAN, 34);

        addUINode(new VBox(20,
                box,
                box2,
                getKeyTestBox(KeyCode.TAB, Color.PINK),
                getKeyTestBox(KeyCode.MINUS, Color.PURPLE),
                getKeyTestBox(KeyCode.PLUS, Color.GRAY)
                ));

        addUINode(new VBox(20,
                t1,
                t2,
                t3,
                t4
                ),
                850, 0);
    }

    private HBox getKeyTestBox(KeyCode code, Color color) {
        HBox box = new HBox(5);
        addKey(box.getChildren(), code, color, 108);
        addKey(box.getChildren(), code, color, 50);
        addKey(box.getChildren(), code, color, 34);
        addKey(box.getChildren(), code, color, 26);
        addKey(box.getChildren(), code, color, 12);
        return box;
    }

    private void addKey(ObservableList<Node> list, KeyCode code, Color color, int size) {
        list.add(new TriggerView(new KeyTrigger(code), color, size));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
