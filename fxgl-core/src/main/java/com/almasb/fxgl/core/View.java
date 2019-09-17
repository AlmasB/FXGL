/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core;

import javafx.scene.Node;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface View extends Updatable, Disposable {

    Node getNode();
}
