/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.components;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.script.Script;

/**
 * Control that runs scripted entity behavior from a javascript file.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class JSControl extends Component {

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
    public void onUpdate(double tpf) {
        script.call("onUpdate", entity, tpf);
    }
}
