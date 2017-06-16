/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pong;

import com.almasb.fxgl.core.math.Vec2;

import java.io.Serializable;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ServerMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    public Vec2 ballPosition;
    public double bat1PositionY;
    public double bat2PositionY;

    public ServerMessage(Vec2 ballPosition, double bat1PositionY, double bat2PositionY) {
        this.ballPosition = ballPosition;
        this.bat1PositionY = bat1PositionY;
        this.bat2PositionY = bat2PositionY;
    }
}
