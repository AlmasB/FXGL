/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.EngineService;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CustomServiceSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.addEngineService(CustomService.class);
    }
    
    // there exist various other methods that can be overridden
    // to listen for FXGL lifecycle events
    public static class CustomService extends EngineService {
        @Override
        public void onInit() {
            System.out.println("onInit()");
        }

        @Override
        public void onExit() {
            System.out.println("onExit()");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
