/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.extra.entity.components.JSComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use scripted controls.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class JavaScriptSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("JavaScriptSample");
    }

    @Override
    protected void initGame() {

        Entities.builder()
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .with(new JSComponent("spin_control.js"))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(180, 100)
                .viewFromNode(new Rectangle(40, 40))
                .with(new JSComponent("ccw_spin_control.js"))
                .buildAndAttach(getGameWorld());

        getAssetLoader().loadScript("sample.js").call("sample");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
