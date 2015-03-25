package com.almasb.fxgl.asset;

import javafx.scene.media.AudioClip;

public class Audio {

    private AudioClip clip;
    private boolean ready = false;

    /*package-private*/ Audio() {

    }

    /*package-private*/ void setAudioClip(AudioClip clip) {
        this.clip = clip;
        ready = true;
    }

    public void play() {
        if (!ready)
            return;

        clip.play();
    }

    public void stop() {
        if (!ready)
            return;

        clip.stop();
    }
}
