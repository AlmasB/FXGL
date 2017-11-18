/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Shows how to use JavaFX UI within FXGL.
 */
public class CenterTextSample extends GameApplication {

    private Text uiText;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("CenterTextSample");
        settings.setVersion("0.1");






    }

    @Override
    protected void initUI() {
        uiText = new Text();
        uiText.setFont(Font.font(18));

        getGameScene().addUINode(uiText);

        getUIFactory().centerText(uiText);

        // now comment above and uncomment below, see the difference
        //getUIFactory().centerTextBind(uiText);
    }

    private StringBuilder builder = new StringBuilder(1000);

    @Override
    protected void onUpdate(double tpf) {
        builder.delete(0, 1000);
        for (int i = 0; i < FXGLMath.random(0, 100); i++) {
            builder.append(i);
        }

        uiText.setText(builder.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
