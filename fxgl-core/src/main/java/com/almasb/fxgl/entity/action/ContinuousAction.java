/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.action;

/**
 * This action runs over time.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class ContinuousAction extends Action {

    @Override
    protected final void onUpdate(double tpf) {
        perform(tpf);
    }

    protected abstract void perform(double tpf);
}
