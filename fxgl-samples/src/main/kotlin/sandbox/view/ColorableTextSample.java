/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.view;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.KeyTrigger;
import com.almasb.fxgl.input.view.TriggerView;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.getGameScene;

public class ColorableTextSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ColorableTextSample");
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

        VBox vbox = new VBox(20);
        vbox.setTranslateX(0);
        vbox.setTranslateY(0);
        vbox.getChildren().addAll(
                box,
                box2,
                getKeyTestBox(KeyCode.TAB, Color.PINK),
                getKeyTestBox(KeyCode.MINUS, Color.PURPLE),
                getKeyTestBox(KeyCode.PLUS, Color.GRAY)
        );

        getGameScene().addUINode(vbox);
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
