/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import com.almasb.fxgl.core.concurrent.IOTask;
import com.almasb.fxgl.util.Optional;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * Defines methods for Net service.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface Net {

    /**
     * @param url web url of a file
     * @return task that downloads a file from given url into running directory
     */
    IOTask<Path> downloadTask(String url);

    /**
     * Note: the caller is responsible for closing the stream.
     *
     * @param url link to open stream to
     * @return task that provides stream access to given link
     */
    IOTask<InputStream> openStreamTask(String url);

    /**
     * @return task that loads latest FXGL version from the server as string
     */
    IOTask<String> getLatestVersionTask();

    /**
     * @param url link to open
     * @return task that opens default browser with given url
     */
    IOTask<?> openBrowserTask(String url);

    IOTask<Server> hostMultiplayerTask();

    IOTask<Client> connectMultiplayerTask(String serverIP);

    /**
     * @return network connection if active or Optional.empty() if not
     */
    Optional<NetworkConnection> getConnection();

    <T extends Serializable> void addDataParser(Class<T> cl, DataParser<T> parser);
}
