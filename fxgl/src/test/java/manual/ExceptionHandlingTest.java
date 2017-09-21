/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package manual;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import org.junit.jupiter.api.Disabled;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Disabled
public class ExceptionHandlingTest extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ExceptionHandlingTest");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);

        //throw new RuntimeException("Exception in initSettings()");
    }

    @Override
    protected void initInput() {
        //throw new RuntimeException("Exception in initInput()");
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        //throw new RuntimeException("Exception in initGame()");
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {
        //throw new RuntimeException("Exception in onUpdate()");
    }

    @Override
    protected void onPostUpdate(double tpf) {
        //throw new RuntimeException("Exception in onPostUpdate()");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
