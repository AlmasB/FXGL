/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.control;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.parser.JavaScriptParser;

/**
 * Control that runs scripted entity behavior from a javascript file.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class JSControl extends Control {

    private JavaScriptParser parser;

    /**
     * Constructs new instance with given source, either string or filename.
     *
     * @param script javascript source
     */
    public JSControl(String script) {
        parser = new JavaScriptParser(script);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        parser.callFunction("onUpdate", entity, tpf);
    }
}
