/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.controls;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.script.Script;

/**
 * Control that runs scripted entity behavior from a javascript file.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class JSControl extends Control {

    private Script script;

    /**
     * Constructs new instance with given source filename.
     *
     * @param scriptFileName javascript source
     */
    public JSControl(String scriptFileName) {
        this.script = FXGL.getAssetLoader().loadScript(scriptFileName);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        script.call("onUpdate", entity, tpf);
    }
}
