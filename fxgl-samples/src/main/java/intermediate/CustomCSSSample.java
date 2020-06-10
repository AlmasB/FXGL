/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FXGLButton;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CustomCSSSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.getCSSList().add("test_fxgl_light.css");
    }

    @Override
    protected void initUI() {
        FXGL.addUINode(new FXGLButton("HELLO"), 100, 100);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
