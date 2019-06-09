/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.text.TextLevelLoader;
import com.almasb.fxgl.ui.FXGLButton;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BasicLevelApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.getCSSList().add("test_fxgl_light.css");
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new MyEntityFactory());

        Level level = FXGL.getAssetLoader().loadLevel("test_level.txt", new TextLevelLoader(30, 30, '0'));

        FXGL.getGameWorld().setLevel(level);

        FXGL.spawn("rect", 750, 550);

        FXGL.getGameWorld().getRandom( e -> true ).ifPresent(e -> e.addComponent(new ProjectileComponent(FXGLMath.randomPoint2D(), 250)));
    }

    @Override
    protected void initUI() {
        FXGL.addUINode(new FXGLButton("HELLO"), 100, 100);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
