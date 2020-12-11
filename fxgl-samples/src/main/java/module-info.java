/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

open module samples.main {
    requires kotlin.stdlib;
    requires com.almasb.fxgl.all;

    requires java.desktop;

    // these modules are not part of fxgl.all since they are use case specific
    // these modules can be used as a separate dependency, e.g. in Maven
    requires com.almasb.fxgl.controllerinput;
}