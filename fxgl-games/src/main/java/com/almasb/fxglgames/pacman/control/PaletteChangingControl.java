/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxglgames.pacman.control;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
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
public class PaletteChangingControl extends AbstractControl {

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
