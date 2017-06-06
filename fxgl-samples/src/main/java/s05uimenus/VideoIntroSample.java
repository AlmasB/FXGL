/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.scene.IntroScene;
import com.almasb.fxgl.scene.SceneFactory;
import com.almasb.fxgl.scene.intro.VideoIntroScene;
import com.almasb.fxgl.service.ServiceType;
import com.almasb.fxgl.settings.GameSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Shows how to use external video instead of an animation intro.
 */
public class VideoIntroSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("VideoIntroSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);

        // 1. set intro enabled to true
        settings.setIntroEnabled(true);

        // 2. set menu enabled to true
        settings.setMenuEnabled(true);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);

        settings.addServiceType(new ServiceType<SceneFactory>() {
            @Override
            public Class<SceneFactory> service() {
                return SceneFactory.class;
            }

            @Override
            public Class<? extends SceneFactory> serviceProvider() {
                return MySceneFactory.class;
            }
        });
    }

    public static class MySceneFactory extends SceneFactory {
        @NotNull
        @Override
        public IntroScene newIntro() {
            return new VideoIntroScene("testvideo.mp4");
        }
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {}

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
