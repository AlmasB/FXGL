package com.almasb.fxgl.app;

import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class FXGLApplication extends Application {

    private static GameApplication app;
    private static ReadOnlyGameSettings settings;

    /**
     * This is the main entry point as run by the JavaFX platform.
     */
    @Override
    public void start(Stage stage) throws Exception {
        var engine = new Engine(app, settings, stage);

        FXGL.engine = engine;

        engine.startLoop$fxgl_engine();
    }

    static void launchFX(GameApplication app, ReadOnlyGameSettings settings, String[] args) {
        FXGLApplication.app = app;
        FXGLApplication.settings = settings;
        launch(args);
    }
}
