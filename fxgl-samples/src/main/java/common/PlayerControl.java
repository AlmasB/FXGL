/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package common;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
public class PlayerControl extends Control {

    private PositionComponent position;

    private double speed = 0;

    @Override
    public void onUpdate(Entity entity, double tpf) {
        speed = tpf * 60;
    }

    public void up() {
        position.translateY(-5 * speed);
    }

    public void down() {
        position.translateY(5 * speed);
    }

    public void left() {
        position.translateX(-5 * speed);
    }

    public void right() {
        position.translateX(5 * speed);
    }
}
