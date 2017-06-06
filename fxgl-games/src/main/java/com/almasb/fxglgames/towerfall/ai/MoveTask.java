/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.towerfall.ai;

import com.almasb.fxgl.ai.Action;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxglgames.towerfall.CharacterControl;
import com.almasb.fxglgames.towerfall.TowerfallApp;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MoveTask extends Action {

    private CharacterControl control;

    @Override
    public void action() {
        if (control == null) {
            control = getObject().getControlUnsafe(CharacterControl.class);
        }

        if (getObject().getY() - FXGL.<TowerfallApp>getAppCast().getPlayer().getY() > 0) {
            control.jump();
        }

        boolean moveLeft = getObject().getX() - FXGL.<TowerfallApp>getAppCast().getPlayer().getX() > 0;

        if (moveLeft)
            control.left();
        else
            control.right();
    }
}
