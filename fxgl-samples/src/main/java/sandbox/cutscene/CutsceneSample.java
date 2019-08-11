/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.cutscene;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.cutscene.Cutscene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CutsceneSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, "test", () -> {
            var lines = getAssetLoader().loadText("cutscene0.txt");

            var cutscene = new Cutscene(lines);

            getCutsceneService().startCutscene(cutscene);

//            var scene = new CutsceneScene();
//            scene.start(cutscene);
//
//            getGameController().pushSubScene(scene);
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLUE);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
