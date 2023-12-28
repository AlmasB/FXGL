/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.view;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to load video files.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class VideoSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initUI() {
        var video = getAssetLoader().loadVideo("testvideo.mp4");
        video.setFitWidth(getAppWidth());
        video.setFitHeight(getAppHeight());
        video.getMediaPlayer().play();

        addUINode(video);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
