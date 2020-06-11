/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.achievement.Achievement;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MenuSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.setFullScreenAllowed(true);
        settings.setEnabledMenuItems(EnumSet.of(MenuItem.EXTRA));
        settings.getCredits().addAll(Arrays.asList(
                "Short Name - Lead Programmer",
                "LongLongLongLongLongLongLong Name - Programmer",
                "V Short - Artist",
                "Medium-Hyphen Name - Designer",
                "More Credits - 111",
                "More Credits - 222",
                "More Credits - 333",
                "More Credits - 444",
                "More Credits - 444",
                "Example of a credit name that will definitely not fit on the screen using default font",
                "More Credits - 444",
                "More Credits - 444",
                "More Credits - 555",
                "More Credits - 666",
                "More Credits - 777"
        ));

        settings.getAchievements().add(new Achievement("Name", "description", "", 0));
        settings.getAchievements().add(new Achievement("Name2", "description2", "", 1));
    }

    @Override
    protected void initGame() {
        FXGL.entityBuilder()
                .at(300, 300)
                .view(new Rectangle(100, 100, Color.BLUE))
                .view(new Text("Example"))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
