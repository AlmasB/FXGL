/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.asset.FXGLAssets;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;

/**
 * Shows how to use sounds.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AudioSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AudioSample");
        settings.setVersion("0.1");






    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onActionBegin() {
                FXGLAssets.SOUND_NOTIFICATION.setBalance(-1.0);
                getAudioPlayer().playSound(FXGLAssets.SOUND_NOTIFICATION);
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onActionBegin() {
                FXGLAssets.SOUND_NOTIFICATION.setBalance(1.0);
                getAudioPlayer().playSound(FXGLAssets.SOUND_NOTIFICATION);
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Mid") {
            @Override
            protected void onActionBegin() {
                FXGLAssets.SOUND_NOTIFICATION.setBalance(0.0);
                getAudioPlayer().playSound(FXGLAssets.SOUND_NOTIFICATION);
            }
        }, KeyCode.S);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
