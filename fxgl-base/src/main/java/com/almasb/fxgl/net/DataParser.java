/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.net;

import java.io.Serializable;

/**
 * Parser for network data
 *
 * @param <T>
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface DataParser<T extends Serializable> {

    /**
     * Called when data arrives from the other end
     * of network connection
     *
     * @param data the data object
     */
    void parse(T data);
}
