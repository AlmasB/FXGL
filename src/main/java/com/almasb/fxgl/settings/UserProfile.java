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
package com.almasb.fxgl.settings;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.logging.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User profile can store various preference settings
 * like resolution, volume, etc.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class UserProfile implements Serializable {

    private static final Logger log = FXGL.getLogger("FXGL.UserProfile");

    private static final long serialVersionUID = 1L;

    /**
     * K - bundle name, V - bundle
     */
    private Map<String, Bundle> bundles = new HashMap<>();

    private String appTitle;
    private String appVersion;

    /**
     * Constructs an empty user profile with given app title and version.
     * The title and version can later be used to check for compatibility.
     *
     * @param appTitle app title
     * @param appVersion app version
     */
    public UserProfile(String appTitle, String appVersion) {
        this.appTitle = appTitle;
        this.appVersion = appVersion;
    }

    /**
     *
     * @param appTitle app title
     * @param appVersion app version
     * @return true iff title and version are compatible with the app
     */
    public final boolean isCompatible(String appTitle, String appVersion) {
        return this.appTitle.equals(appTitle) && this.appVersion.equals(appVersion);
    }

    /**
     * Stores a bundle in the user profile. Bundles with same
     * name are not allowed.
     *
     * @param bundle the bundle to store
     */
    public final void putBundle(Bundle bundle) {
        if (bundles.containsKey(bundle.name)) {
            throw new IllegalArgumentException("Bundle \"" + bundle.name + "\" already exists!");
        }

        bundles.put(bundle.name, bundle);
    }

    /**
     *
     * @param name bundle name
     * @return bundle with given name
     */
    public final Bundle getBundle(String name) {
        Bundle bundle = bundles.get(name);
        if (bundle == null) {
            throw new IllegalArgumentException("Bundle \"" + name + "\" doesn't exist!");
        }

        return bundle;
    }

    /**
     * Bundle is used to store values mapped with certain keys.
     * Bundle name must be unique as it identifies the bundle data within profile.
     */
    public static final class Bundle implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;

        public Bundle(String name) {
            this.name = name;
        }

        private Map<String, Object> data = new HashMap<>();

        public void put(String key, Serializable value) {
            data.put(name + "." + key, value);
        }

        public <T> T get(String key) {
            return (T)data.get(name + "." + key);
        }

        /**
         * Logs contents of the bundle using FXGL log system.
         */
        public void log() {
            log.finer("Logging bundle: " + name);
            data.forEach((k,v) -> log.finer(k + "=" + v));
        }
    }
}
