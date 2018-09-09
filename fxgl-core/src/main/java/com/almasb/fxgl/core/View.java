package com.almasb.fxgl.core;

import javafx.scene.Node;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface View extends Updatable, Disposable {

    Node getNode();
}
