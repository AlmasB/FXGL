/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.editor;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EditorGameApplication extends GameApplication {

    private EditorMainUI ui;

    public EditorGameApplication(EditorMainUI ui) {
        this.ui = ui;
    }

    @Override
    protected void initSettings(GameSettings settings) {
    }

    @Override
    protected void initInput() {
//        getInput().addTriggerListener(new TriggerListener() {
//            @Override
//            protected void onActionBegin(Trigger trigger) {
//                System.out.println(trigger);
//            }
//        });
    }

    @Override
    protected void initGame() {
//        entityBuilder()
//                .view(new Rectangle(getAppWidth(), getAppHeight(), Color.DARKGRAY))
//                .buildAndAttach();

//        entityBuilder()
//                .view(new Rectangle(64, 64, Color.BLUE))
//                .buildAndAttach();
//
//        entityBuilder()
//                .at(getAppWidth() - 64, getAppHeight() - 64)
//                .view(new Rectangle(64, 64, Color.BLUE))
//                .buildAndAttach();
//
//        entityBuilder()
//                .at(0, getAppHeight() / 2 - 32)
//                .view(new Rectangle(64, 64, Color.BLUE))
//                .with(new ProjectileComponent(new Point2D(1, 0), 150))
//                .buildAndAttach();
    }

    private boolean isAdded = false;

    @Override
    protected void onUpdate(double tpf) {
        if (!isAdded) {
            ui.notifyDone();
            // TODO: use service?
            isAdded = true;
        }
    }
}
