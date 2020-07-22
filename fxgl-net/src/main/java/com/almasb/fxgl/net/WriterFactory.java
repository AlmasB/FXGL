/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import java.io.OutputStream;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface WriterFactory<T> {

    MessageWriter<T> create(OutputStream out);
}
