/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Map;

/**
 * Shows how to use JavaFX UI within FXGL.
 */
public class UISample2 extends GameApplication {

    // 1. declare JavaFX or FXGL UI object
    private Text uiText;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("UISample2");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("key", 1);
    }

    @Override
    protected void initInput() {
        DSLKt.onKey(KeyCode.A, "up", () -> {
            DSLKt.inc("key", +2);
        });
    }

    @Override
    protected void initUI() {
        Text t = DSLKt.addVarText(400, 300, "key");
        t.setFill(Color.BLACK);

//        // 2. initialize the object
//        uiText = new Text();
//        uiText.setFont(Font.font(18));
//
//        // 3. position the object
//        uiText.setTranslateX(400);
//        uiText.setTranslateY(300);
//
//        // 4. bind text property to some data of interest
//        //uiText.textProperty().bind(getMasterTimer().tickProperty().asString("Tick: [%d]"));
//
//        // 5. add UI object to scene
//        getGameScene().addUINode(uiText);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
