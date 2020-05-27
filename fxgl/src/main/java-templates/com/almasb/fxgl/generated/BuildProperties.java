/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.generated;

/**
 * Stores properties that are filtered (populated) by templating-maven-plugin from pom.xml
 * during build.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class BuildProperties {

    public static final String VERSION = "${project.version}";
    public static final String BUILD = "${timestamp}";
}
