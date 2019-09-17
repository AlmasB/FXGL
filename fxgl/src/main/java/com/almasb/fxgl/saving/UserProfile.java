/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.saving;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.sslogger.Logger;

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
     * https://github.com/AlmasB/FXGL/issues/576
     * Stores a bundle in the user profile. Bundles with same
     * name are not allowed.
     *
     * @param bundle the bundle to store
     */
    public final void putBundle(Bundle bundle) {
        if (bundles.containsKey(bundle.getName())) {
            throw new IllegalArgumentException("Bundle \"" + bundle.getName() + "\" already exists!");
        }

        bundles.put(bundle.getName(), bundle);
    }

    /**
     * https://github.com/AlmasB/FXGL/issues/576
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

    public final void log(Logger logger) {
        logger.info("Logging profile data");
        logger.info(bundles.toString());
    }
}
