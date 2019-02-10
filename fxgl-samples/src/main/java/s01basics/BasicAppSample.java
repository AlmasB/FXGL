/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s01basics;

import com.almasb.fxgl.app.*;
import com.almasb.fxgl.dsl.FXGL;

import java.util.EnumSet;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is an example of a minimalistic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BasicAppSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("BasicAppSample");
        settings.setVersion("0.1");
        settings.setMenuEnabled(true);
        settings.setIntroEnabled(false);
        settings.setEnabledMenuItems(EnumSet.allOf(MenuItem.class));
    }

    @Override
    protected void initGame() {
        BasicAppSample app = getAppCast();

        System.out.println(app);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
