/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.InGamePanel;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class InGamePanelSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("InGamePanelSample");
        settings.setVersion("0.1");






    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Open/Close Panel") {
            @Override
            protected void onActionBegin() {
                if (panel.isOpen())
                    panel.close();
                else
                    panel.open();
            }
        }, KeyCode.TAB);
    }

    private InGamePanel panel;

    @Override
    protected void initUI() {
        panel = new InGamePanel();

        Text text = getUIFactory().newText("Hello from Panel");
        text.setTranslateX(50);
        text.setTranslateY(50);
        panel.getChildren().add(text);

        getGameScene().addUINode(panel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
