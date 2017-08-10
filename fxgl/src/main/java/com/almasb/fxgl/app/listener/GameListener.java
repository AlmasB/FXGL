package com.almasb.fxgl.app.listener;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface GameListener {

    /**
     * Implementing classes need to clean up (reset from maybe existing previous game)
     * and prepare for a new game.
     */
    void onInit();

    /**
     * Called on PLAY state update.
     *
     * @param tpf time per frame
     */
    void onUpdate(double tpf);
}
