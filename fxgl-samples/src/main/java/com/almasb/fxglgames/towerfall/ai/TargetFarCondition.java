/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.towerfall.ai;

import com.almasb.fxgl.ai.Condition;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxglgames.towerfall.TowerfallApp;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TargetFarCondition extends Condition {

    @Override
    public boolean evaluate() {
        return getObject().distance(FXGL.<TowerfallApp>getAppCast().getPlayer()) > 500;
    }
}
