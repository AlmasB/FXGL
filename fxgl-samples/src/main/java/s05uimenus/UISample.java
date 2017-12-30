/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Shows how to use JavaFX UI within FXGL.
 */
public class UISample extends GameApplication {

    // 1. declare JavaFX or FXGL UI object
    private Text uiText;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("UISample");
        settings.setVersion("0.1");






    }

    @Override
    protected void initUI() {
        // 2. initialize the object
        uiText = new Text();
        uiText.setFont(Font.font(18));

        // 3. position the object
        uiText.setTranslateX(400);
        uiText.setTranslateY(300);

        // 4. bind text property to some data of interest
        //uiText.textProperty().bind(getMasterTimer().tickProperty().asString("Tick: [%d]"));

        // 5. add UI object to scene
        getGameScene().addUINode(uiText);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
