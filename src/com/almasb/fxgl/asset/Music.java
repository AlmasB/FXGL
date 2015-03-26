package com.almasb.fxgl.asset;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Music {

    private final MediaPlayer mediaPlayer;

    /*package-private*/ Music(Media media) {
        mediaPlayer = new MediaPlayer(media);
    }

    public void playOnce() {
        mediaPlayer.setCycleCount(1);
        mediaPlayer.play();
    }

    public void resume() {
        mediaPlayer.play();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void loop() {
        mediaPlayer.setCycleCount(Integer.MAX_VALUE);
        mediaPlayer.play();
    }
}
