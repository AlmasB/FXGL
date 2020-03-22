/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.action;

/**
 * Default action.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class IdleAction extends InstantAction {

    public IdleAction() {
        // idle action is always complete
        setComplete();
    }

    @Override
    protected void performOnce(double tpf) {
        // no-op idle
    }
}
