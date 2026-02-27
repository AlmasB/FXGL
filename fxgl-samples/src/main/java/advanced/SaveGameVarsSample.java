/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to automatically save and load world properties.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
public class SaveGameVarsSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void onPreInit() {
        // register a handler that automatically saves world properties when running
        // saveLoadHandler.save()
        getSaveLoadService().addWorldPropertiesHandler();
    }

    @Override
    protected void initInput() {
        // press G to check values
        onKeyDown(KeyCode.G, () -> {
            System.out.println(getSceneService().getWorldProperties());
        });

        // press F to modify values
        onKeyDown(KeyCode.F, () -> {
            inc("testInt", +3);
            inc("testDouble", +1.7);
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        // some default values
        vars.put("testInt", 5);
        vars.put("testDouble", 2.3);
    }

    @Override
    protected void initGame() {
        // load from some file
        getSaveLoadService().readAndLoadTask("save.dat").run();
    }

    @Override
    protected void onExit() {
        // save to some file before exit
        getSaveLoadService().saveAndWriteTask("save.dat").run();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
