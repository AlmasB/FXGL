/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.action;

/**
 * Instant action is always complete after performed once.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class InstantAction extends Action {

    @Override
    protected final void onUpdate(double tpf) {
        performOnce(tpf);
        setComplete();
    }

    protected abstract void performOnce(double tpf);
}
