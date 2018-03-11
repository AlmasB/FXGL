/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package manual;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public class MemoryTest extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Memory Test");
        settings.setFullScreenAllowed(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initGame() {}

    @Override
    protected void initPhysics() {}

    private Text text;

    @Override
    protected void initUI() {
        text = new Text();
        text.setFont(Font.font("Lucida Console", 18));
        text.relocate(100, 100);

        getGameScene().addUINode(text);
    }

    private static final double MB = 1024 * 1024.0;

    @Override
    protected void onUpdate(double tpf) {
        text.setText(String.format("Used:  %7.1f MB"
                + "\nFree:  %7.1f MB"
                + "\nTotal: %7.1f MB"
                + "\nMax:   %7.1f MB",
                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB,
                Runtime.getRuntime().freeMemory() / MB,
                Runtime.getRuntime().totalMemory() / MB,
                Runtime.getRuntime().maxMemory() / MB));


//                "Occupied: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MB
//                + "\nFree: " + Runtime.getRuntime().freeMemory() / MB
//                + "\nTotal: " + Runtime.getRuntime().totalMemory() / MB
//                + "\nMax:   " + Runtime.getRuntime().maxMemory() / MB);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
