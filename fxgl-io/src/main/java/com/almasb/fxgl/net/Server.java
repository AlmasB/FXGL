/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import com.almasb.fxgl.core.concurrent.IOTask;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 * @author Jordan O'Hara (jordanohara96@gmail.com)
 * @author Byron Filer (byronfiler348@gmail.com)
 * */
public abstract class Server<T> extends Endpoint<T> {

    private ReadOnlyBooleanWrapper isListeningProperty = new ReadOnlyBooleanWrapper(false);

    public final ReadOnlyBooleanProperty listeningProperty() {
        return isListeningProperty.getReadOnlyProperty();
    }

    public final boolean isListening() {
        return isListeningProperty.getValue();
    }

    protected final void onStartedListening() {
        isListeningProperty.set(true);
    }

    protected final void onStoppedListening() {
        isListeningProperty.set(false);
    }

    /**
     * Starts listening for incoming connections on a background thread.
     */
    public final void startAsync() {
        Thread t = new Thread(startTask()::run, "ServerAsyncConnThread");
        t.setDaemon(true);
        t.start();
    }

    /**
     * @return a task that performs an IO operation to start listening for incoming connections.
     */
    public final IOTask<Void> startTask() {
        return IOTask.ofVoid("ServerStart", this::start);
    }

    protected abstract void start();

    /**
     * Stops the server. After this call, the server will no longer accept incoming connections.
     * Existing TCP connections will remain active.
     * Existing UDP connections will be terminated.
     */
    public abstract void stop();
}
