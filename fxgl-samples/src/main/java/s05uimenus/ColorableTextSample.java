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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;

/**
 * Shows how to use Colorable UI texts within FXGL.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ColorableTextSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ColorableTextSample");
    }

    @Override
    protected void initUI() {

        TextFlow uiText = getUIFactory().newTextFlow()
                .append("Press ")
                .append(KeyCode.V, Color.LIGHTYELLOW)
                .append(" to use ")
                .append("Enhanced Vision", Color.DARKGREEN);

        HBox box = new HBox(5);

        char c = 'A';

        while (c != 'N') {
            box.getChildren().add(new TriggerView(new KeyTrigger(KeyCode.getKeyCode("" + c)), Color.RED, 34));
            c++;
        }

        HBox box2 = new HBox(5);

        while (c != 'Z' + 1) {
            box2.getChildren().add(new TriggerView(new KeyTrigger(KeyCode.getKeyCode("" + c)), Color.BLUE, 26));
            c++;
        }

        FXGLTextFlow t2 = getUIFactory().newTextFlow();
        t2.append(new TriggerView(new KeyTrigger(KeyCode.A, InputModifier.CTRL))).append("   ");
        t2.append(new TriggerView(new KeyTrigger(KeyCode.B, InputModifier.SHIFT))).append("   ");
        t2.append(new TriggerView(new KeyTrigger(KeyCode.C, InputModifier.ALT))).append("   ");
        t2.append(new TriggerView(new KeyTrigger(KeyCode.SPACE, InputModifier.ALT))).append("   ");

        FXGLTextFlow t3 = getUIFactory().newTextFlow();
        t3.append("Hold: ").append(MouseButton.PRIMARY, Color.DARKSALMON).append(" and ").append(MouseButton.SECONDARY, Color.LIGHTPINK).append(" to shoot");

        TextFlow t4 = getUIFactory().newTextFlow()
                .append("Discovered Location: ")
                .append("The New Temple", Color.DARKCYAN, 34);

        VBox vbox = new VBox(20);
        vbox.setTranslateX(0);
        vbox.setTranslateY(0);
        vbox.getChildren().addAll(
                box,
                box2,
                uiText,
                t2,
                t3,
                t4
        );

        getGameScene().addUINode(vbox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
