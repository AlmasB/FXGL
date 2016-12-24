/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.net;

import com.almasb.easyio.IOTask;

import java.io.InputStream;
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
    IOTask<Void> openBrowserTask(String url);
}
