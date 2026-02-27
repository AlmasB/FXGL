/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics.box2d.particle;

import javafx.scene.paint.Color;

/**
 * Small color object for each particle
 *
 * @author dmurph
 */
public class ParticleColor {
    public byte r, g, b, a;

    public ParticleColor(byte r, byte g, byte b, byte a) {
        set(r, g, b, a);
    }

    public ParticleColor(Color color) {
        set(color);
    }

    public void set(Color color) {
        set(
                (byte) (255 * color.getRed()),
                (byte) (255 * color.getGreen()),
                (byte) (255 * color.getBlue()),
                (byte) (255 * color.getOpacity())
        );
    }

    public void set(ParticleColor color) {
        set(color.r, color.g, color.b, color.a);
    }

    public void set(byte r, byte g, byte b, byte a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}
