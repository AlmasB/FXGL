package com.almasb.fxgl.asset;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class Music {

    private MediaPlayer mediaPlayer;
    private boolean ready = false;

    /*package-private*/ Music() {

    }

    /*package-private*/ void setMedia(Media media) {
        mediaPlayer = new MediaPlayer(media);
        ready = true;
    }

    public void playOnce() {
        if (!ready)
            return;

        mediaPlayer.setCycleCount(1);
        mediaPlayer.play();
    }

    public void resume() {
        if (!ready)
            return;

        mediaPlayer.play();
    }

    public void pause() {
        if (!ready)
            return;

        mediaPlayer.pause();
    }

    public void stop() {
        if (!ready)
            return;

        mediaPlayer.stop();
    }

    public void loop() {
        if (!ready)
            return;

        mediaPlayer.setCycleCount(Integer.MAX_VALUE);
        mediaPlayer.play();
    }
}
