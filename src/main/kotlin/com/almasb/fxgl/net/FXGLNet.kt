/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.net

import com.almasb.easyio.IOTask
import com.almasb.easyio.taskOf
import com.almasb.easyio.voidTaskOf
import com.almasb.fxgl.app.FXGL
import com.google.inject.Singleton
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.file.Path

/**
 * Main Net service provider for FXGL.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Singleton
class FXGLNet : Net {

    override fun downloadTask(url: String): IOTask<Path> = DownloadTask(url)

    override fun openStreamTask(url: String) = taskOf("openStream($url)", { URL(url).openStream() })

    /**
     * Loads pom.xml from GitHub server's master branch
     * and parses the "version" tag.
     */
    override fun getLatestVersionTask() = openStreamTask(FXGL.getString("url.pom")).then {

        return@then taskOf("latestVersion", {

            InputStreamReader(it).useLines {
                return@taskOf it.first { it.contains("<version>") }
                        .trim()
                        .removeSurrounding("<version>", "</version>")
            }
        })
    }

    override fun openBrowserTask(url: String) = voidTaskOf("openBrowser($url)", { FXGL.getApp().hostServices.showDocument(url) })
}