/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to load and start cutscenes.
 * For cutscene format, see https://github.com/AlmasB/FXGL/wiki/Narrative-and-Dialogue-System-(FXGL-11)#cutscenes
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CutsceneSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        // press F to see the cutscene
        onKeyDown(KeyCode.F, () -> {
            var cutscene = getAssetLoader().loadCutscene("example_cutscene1.txt");

            getCutsceneService().startCutscene(cutscene);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
