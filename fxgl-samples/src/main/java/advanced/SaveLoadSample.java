/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.profile.SaveLoadHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.EnumSet;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SaveLoadSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setMainMenuEnabled(true);
        settings.setEnabledMenuItems(EnumSet.allOf(MenuItem.class));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("time", 0.0);
    }

    @Override
    protected void onPreInit() {
        getSaveLoadService().addHandler(new SaveLoadHandler() {
            @Override
            public void onSave(DataFile data) {
                // create a new bundle to store your data
                var bundle = new Bundle("gameData");

                // store some data
                double time = getd("time");
                bundle.put("time", time);

                // give the bundle to data file
                data.putBundle(bundle);
            }

            @Override
            public void onLoad(DataFile data) {
                // get your previously saved bundle
                var bundle = data.getBundle("gameData");

                // retrieve some data
                double time = bundle.get("time");

                // update your game with saved data
                set("time", time);
            }
        });
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, "Save", () -> {
            getSaveLoadService().saveAndWriteTask("save1.sav").run();
        });

        onKeyDown(KeyCode.G, "Load", () -> {
            getSaveLoadService().readAndLoadTask("save1.sav").run();
        });
    }

    @Override
    protected void initGame() {
        run(() -> inc("time", 1.0), Duration.seconds(1.0));
    }

    @Override
    protected void initUI() {
        var text = getUIFactoryService().newText("", Color.BLACK, 18.0);
        text.textProperty().bind(getdp("time").asString());

        addUINode(text, 100, 100);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
