/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s09advanced.customservice;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.service.ServiceType;
import com.almasb.fxgl.settings.GameSettings;

/**
 * This sample shows how to define and use custom services.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class CustomServicesSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("CustomServicesSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);

        // 1. register your custom service and its provider
        settings.addServiceType(new ServiceType<Printer>() {
            @Override
            public Class<Printer> service() {
                return Printer.class;
            }

            @Override
            public Class<? extends Printer> serviceProvider() {
                return CustomPrinter.class;
            }
        });
    }

    @Override
    protected void initGame() {

        // 2. ask FXGL to give you an instance
        Printer printer = FXGL.getInstance(Printer.class);

        printer.print("Using custom service");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
