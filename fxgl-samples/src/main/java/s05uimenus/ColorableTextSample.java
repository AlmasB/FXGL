/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.InputModifier;
import com.almasb.fxgl.input.KeyTrigger;
import com.almasb.fxgl.input.TriggerView;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.FXGLTextFlow;
import com.almasb.fxgl.ui.TextFlowBuilder;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;

/**
 * Shows how to use JavaFX UI within FXGL.
 */
public class ColorableTextSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ColorableTextSample");
        settings.setVersion("0.1");






    }

    @Override
    protected void initUI() {
        TextFlow uiText = TextFlowBuilder.start()
                .append("Press ")
                .append(KeyCode.V, Color.ORANGE)
                .append(" to use ")
                .append("Enhanced Vision", Color.ORANGE)
                .build();

        HBox box = new HBox(5);

        char c = 'A';

        while (c != 'N') {
            box.getChildren().add(new TriggerView(new KeyTrigger(KeyCode.getKeyCode("" + c))));
            c++;
        }

        HBox box2 = new HBox(5);

        while (c != 'Z' + 1) {
            box2.getChildren().add(new TriggerView(new KeyTrigger(KeyCode.getKeyCode("" + c))));
            c++;
        }

        FXGLTextFlow t2 = new FXGLTextFlow();
        //t2.append("Discovered: ").append("The New Temple", Color.DARKCYAN);
        t2.append(new TriggerView(new KeyTrigger(KeyCode.A, InputModifier.CTRL))).append("   ");
        t2.append(new TriggerView(new KeyTrigger(KeyCode.B, InputModifier.SHIFT))).append("   ");
        t2.append(new TriggerView(new KeyTrigger(KeyCode.C, InputModifier.ALT))).append("   ");
        t2.append(new TriggerView(new KeyTrigger(KeyCode.SPACE, InputModifier.ALT))).append("   ");


        FXGLTextFlow t3 = new FXGLTextFlow();
        t3.append("Hold: ").append(MouseButton.PRIMARY, Color.ORANGE).append(" and ").append(MouseButton.SECONDARY, Color.ORANGE).append(" to shoot");

        VBox vbox = new VBox(20);
        vbox.setTranslateX(0);
        vbox.setTranslateY(0);
        vbox.getChildren().addAll(
                box,
                box2,
                uiText,
                t2,
                t3
        );

        getGameScene().addUINode(vbox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
