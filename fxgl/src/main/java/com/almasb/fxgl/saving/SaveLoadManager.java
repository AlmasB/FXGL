/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.saving;

import com.almasb.fxgl.app.ReadOnlyGameSettings;
import com.almasb.fxgl.core.concurrent.IOTask;
import com.almasb.fxgl.io.FS;
import com.almasb.sslogger.Logger;

import java.util.Collections;
import java.util.List;

/**
 * Convenient access to saving and loading game data.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class SaveLoadManager {

    private static final Logger log = Logger.get(SaveLoadManager.class);

    private final String PROFILE_FILE_NAME;
    private final String PROFILES_DIR;
    private final String SAVE_DIR;

    private final String SAVE_FILE_EXT;
    private final String DATA_FILE_EXT;

    private final FS fs;
    
    public SaveLoadManager(ReadOnlyGameSettings settings) {
        fs = new FS(settings.isDesktop());

        PROFILES_DIR = settings.getProfileDir();
        DATA_FILE_EXT = settings.getDataFileExt();
        SAVE_FILE_EXT = settings.getSaveFileExt();
        SAVE_DIR = settings.getSaveDir();
        PROFILE_FILE_NAME = settings.getProfileName();
        
        if (!fs.exists(PROFILES_DIR)) {
            createProfilesDir();
        }
    }

    private void createProfilesDir() {
        log.debug("Creating profiles dir");

        fs.createDirectoryTask(PROFILES_DIR)
                .then(n -> fs.writeDataTask(Collections.singletonList("This directory contains user profiles."), PROFILES_DIR + "Readme.txt"))
                .onFailure(e -> {
                    log.warning("Failed to create profiles dir: " + e);
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                })
                .run();
    }

    /**
     * A task that reads all profile names.
     */
    public IOTask<List<String> > readProfileNamesTask() {
        log.debug("Reading profile names");
        return fs.loadDirectoryNamesTask("./" + PROFILES_DIR, false);
    }

    /**
     * Delete profile.
     *
     * @param profileName name of profile to delete
     */
    public IOTask<Void> deleteProfileTask(String profileName) {
        log.debug("Deleting profile: " + profileName);
        return fs.deleteDirectoryTask("./" + PROFILES_DIR + profileName);
    }
    
    public ProfileManager getProfileManager(String profileName) {
        return new ProfileManager(fs, profileName,
                PROFILES_DIR,
                PROFILE_FILE_NAME,
                SAVE_DIR,
                SAVE_FILE_EXT,
                DATA_FILE_EXT
                );
    }
}
