package com.almasb.fxgl.app;

import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FXApplication extends Application {

    private GameApplication app;

    /**
     * This is the main entry point as run by the JavaFX platform.
     */
    @Override
    public void start(Stage stage) throws Exception {
        app = GameApplication.getInstance();

        // do init

        startFXGL(app.getSettings(), stage);
    }

    private void startFXGL(ReadOnlyGameSettings settings, Stage stage) {
        //log.debug("Starting FXGL");

        FXGL.configure(app, settings, stage);
        FXGL.startLoop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
