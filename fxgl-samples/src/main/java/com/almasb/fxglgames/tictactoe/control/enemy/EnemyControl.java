/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.tictactoe.control.enemy;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.event.Subscriber;
import com.almasb.fxglgames.tictactoe.event.AIEvent;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class EnemyControl extends Control {

    private Subscriber eventListener;

    @Override
    public void onAdded(Entity entity) {
        eventListener = FXGL.getEventBus().addEventHandler(AIEvent.WAITING, event -> {
            makeMove();
            FXGL.getEventBus().fireEvent(new AIEvent(AIEvent.MOVED));
        });
    }

    @Override
    public void onRemoved(Entity entity) {
        eventListener.unsubscribe();
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {}

    public abstract void makeMove();
}
