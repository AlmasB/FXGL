/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.script.Script;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;

/**
 * This is an example of a minimalistic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScriptSample3 extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ScriptSample3");
        settings.setVersion("0.1");
    }

    @Override
    protected void initInput() {
        DSLKt.onKeyDown(KeyCode.F, "call", () -> {
            Script script = getAssetLoader().loadScript("forest.js");
            script.call("doCall");
            script.call("doCall");

            Script parser2 = getAssetLoader().loadScript("forest.js");
            parser2.call("doCall");
            parser2.call("doCall");

            System.out.println("calling parser1");

            script.call("doCall");
            script.call("doCall");

            script.call("inject", -999);
            script.call("printHP");

            parser2.call("printHP");
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
