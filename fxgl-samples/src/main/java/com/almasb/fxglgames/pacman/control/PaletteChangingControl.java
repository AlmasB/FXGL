/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pacman.control;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Rectangle2D;
import javafx.scene.transform.Scale;

import java.util.Random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PaletteChangingControl extends Control {

    private PositionComponent position;
    private ViewComponent view;
    private Texture texture;

    public PaletteChangingControl(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
        view = Entities.getView(entity);

        view.setView(texture);
        view.getView().getTransforms().addAll(new Scale(0.26, 0.26, 0, 0));
    }

    private double lastX = 0;
    private double lastY = 0;

    private double timeToSwitch = 0;
    private int spriteColor = 0;

    private Random random = new Random();

    @Override
    public void onUpdate(Entity entity, double tpf) {
        timeToSwitch += tpf;

        if (timeToSwitch >= 5.0) {
            spriteColor = 160 * random.nextInt(6);
            timeToSwitch = 0;
        }

        double dx = position.getX() - lastX;
        double dy = position.getY() - lastY;

        lastX = position.getX();
        lastY = position.getY();

        if (dx == 0 && dy == 0) {
            // didn't move
            return;
        }

        if (Math.abs(dx) > Math.abs(dy)) {
            // move was horizontal
            if (dx > 0) {
                texture.setViewport(new Rectangle2D(130*3, spriteColor, 130, 160));
            } else {
                texture.setViewport(new Rectangle2D(130*2, spriteColor, 130, 160));
            }
        } else {
            // move was vertical
            if (dy > 0) {
                texture.setViewport(new Rectangle2D(0, spriteColor, 130, 160));
            } else {
                texture.setViewport(new Rectangle2D(130, spriteColor, 130, 160));
            }
        }
    }
}
