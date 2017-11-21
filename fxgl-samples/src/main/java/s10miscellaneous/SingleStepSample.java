/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;

/**
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SingleStepSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("SingleStepSample");
        settings.setVersion("0.1");




        settings.setSingleStep(true);

    }

    @Override
    protected void initInput() {
        DSLKt.onKeyDown(KeyCode.ENTER, "update", () -> {
            stepLoop();
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        System.out.println("update: " + tpf);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
