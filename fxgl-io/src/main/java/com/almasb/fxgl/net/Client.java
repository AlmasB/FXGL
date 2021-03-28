/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import com.almasb.fxgl.core.concurrent.IOTask;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 */
public abstract class Client<T> extends Endpoint<T> {

    public final void connectAsync() {
        Thread t = new Thread(connectTask()::run, "ClientAsyncConnThread");
        t.setDaemon(true);
        t.start();
    }

    public final IOTask<Void> connectTask() {
        return IOTask.ofVoid("ClientConnect", this::connect);
    }

    protected abstract void connect();

    public abstract void disconnect();
}