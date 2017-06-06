/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.scene.menu;

/**
 * FXGL built-in menu styles. NOT COMPLETED YET.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public enum MenuStyle {
    FXGL_DEFAULT("fxgl_dark.css"),

    GTA5("fxgl_gta5.css"),

    CCTR("fxgl_cctr.css"),

    WARCRAFT3("fxgl_war3.css");

    private String css;

    public String getCSSFileName() {
        return css;
    }

    MenuStyle(String css) {
        this.css = css;
    }
}
