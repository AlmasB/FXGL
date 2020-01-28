/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net;

import com.almasb.fxgl.core.EngineService;
import com.almasb.fxgl.core.concurrent.IOTask;

import java.io.InputStream;

/**
 * All operations that can be performed via networking.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class NetService extends EngineService {

    /**
     * Note: the caller is responsible for closing the stream.
     *
     * @param url link to open stream to
     * @return task that provides stream access to given link
     */
    public abstract IOTask<InputStream> openStreamTask(String url);


//    /**
//     * @param url web url of a file
//     * @return task that downloads a file from given url into running directory
//     */
//    IOTask<Path> downloadTask(String url);
//

//
//    /**
//     * @return task that loads latest FXGL version from the server as string
//     */
//    //IOTask<String> getLatestVersionTask();
//
////    /**
////     * @param url link to open
////     * @return task that opens default browser with given url
////     */
////    IOTask<?> openBrowserTask(String url);
//
//    IOTask<Server> hostMultiplayerTask();
//
//    IOTask<Client> connectMultiplayerTask(String serverIP);
//
//    /**
//     * @return network connection if active or Optional.empty() if not
//     */
//    Optional<NetworkConnection> getConnection();
//
//    <T extends Serializable> void addDataParser(Class<T> cl, DataParser<T> parser);
}
