/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import com.almasb.fxgl.entity.component.Component;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class NetworkComponent extends Component {

    private static int uniqueID = 0;

    private int id;

    public NetworkComponent() {
        id = uniqueID++;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}
