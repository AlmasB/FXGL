/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.towerfall.ai;

import com.almasb.fxgl.ai.SingleAction;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.towerfall.CharacterControl;
import com.almasb.fxglgames.towerfall.TowerfallApp;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AttackTask extends SingleAction {

    private CharacterControl control;
    private LocalTimer attackTimer = FXGL.newLocalTimer();

    @Override
    public void onUpdate(double tpf) {
        if (control == null) {
            control = getObject().getControl(CharacterControl.class);
        }

        if (attackTimer.elapsed(Duration.seconds(0.5))) {
            control.shoot(FXGL.<TowerfallApp>getAppCast().getPlayer().getCenter());
            attackTimer.capture();
        }
    }
}
