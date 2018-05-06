/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package manual;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * This is an example of a basic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public class AudioPlayerTest extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AudioPlayerTest");
        settings.setFullScreenAllowed(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initInput() {
        Input input = getInput();
        input.addInputMapping(new InputMapping("Open", KeyCode.O));
        input.addInputMapping(new InputMapping("Test", KeyCode.F));
    }

    @Override
    protected void initGame() {}

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    private Music music;

    @OnUserAction(name = "Open", type = ActionType.ON_ACTION_BEGIN)
    public void first() {
        music = getAssetLoader().loadMusic("intro.mp3");
        music.setCycleCount(2);

        getAudioPlayer().playMusic(music);
    }

    @OnUserAction(name = "Test", type = ActionType.ON_ACTION_BEGIN)
    public void second() {
        //getAudioPlayer().stopAllMusic();

        getAudioPlayer().pauseMusic(music);

        getMasterTimer().runOnceAfter(() -> {
            getAudioPlayer().resumeMusic(music);
        }, Duration.seconds(2));

//        getAudioPlayer().pauseAllMusic();
//
//        getMasterTimer().runOnceAfter(() -> {
//            getAudioPlayer().resumeAllMusic();
//        }, Duration.seconds(3));

//        getMasterTimer().runOnceAfter(() -> {
//            getAudioPlayer().stopAllMusic();
//        }, Duration.seconds(3));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
