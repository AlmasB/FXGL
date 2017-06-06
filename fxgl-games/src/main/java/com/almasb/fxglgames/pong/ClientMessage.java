/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pong;

import java.io.Serializable;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ClientMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public boolean up;
    public boolean down;
    public boolean stop;

    public ClientMessage(boolean up, boolean down, boolean stop) {
        this.up = up;
        this.down = down;
        this.stop = stop;
    }
}
