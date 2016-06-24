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

package com.almasb.fxgl.gameplay;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.io.*;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.settings.UserProfile;
import com.almasb.fxgl.util.Experimental;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class SaveLoadManager {

    private static final Logger log = FXGL.getLogger("FXGL.SaveLoadManager");

    private static final String PROFILE_FILE_NAME = FXGL.getString("fs.profilename");
    private static final String PROFILES_DIR = FXGL.getString("fs.profiledir");
    private static final String SAVE_DIR = FXGL.getString("fs.savedir");

    private static final String SAVE_FILE_EXT = FXGL.getString("fs.savefile.ext");
    private static final String DATA_FILE_EXT = FXGL.getString("fs.datafile.ext");

    private final String profileName;

    public SaveLoadManager(String profileName) {
        this.profileName = profileName;
    }

    private String profileDir() {
        return "./" + PROFILES_DIR + profileName + "/";
    }

    private String saveDir() {
        return profileDir() + SAVE_DIR;
    }

    /**
     * Save serializable data onto a disk file system under saves directory,
     * which is created if necessary in the directory where the game is run from.
     * <p>
     * All extra directories will also be created if necessary.
     *
     * @param dataFile data file
     * @param saveFile save file
     * @return io task
     */
    public IOTask<Void> saveTask(DataFile dataFile, SaveFile saveFile) {
        log.debug(() -> "Saving data: " + saveFile.getName());

        return FS.writeDataTask(saveFile, saveDir() + saveFile.getName() + SAVE_FILE_EXT)
                .then(n -> FS.writeDataTask(dataFile, saveDir() + saveFile.getName() + DATA_FILE_EXT));
    }

    /**
     * Saves user profile to "profiles/".
     *
     * @param profile the profile to save
     * @return io result
     */
    public IOTask<Void> saveProfile(UserProfile profile) {
        log.debug(() -> "Saving profile: " + profileName);
        return FS.writeDataTask(profile, profileDir() + PROFILE_FILE_NAME);
    }

    /**
     * Load serializable data from external
     * file on disk file system from saves directory which is
     * in the directory where the game is run from.
     *
     * @param saveFile file name to loadTask from
     * @return instance of deserialized data structure
     */
    public IOTask<DataFile> loadTask(SaveFile saveFile) {
        log.debug(() -> "Loading data: " + saveFile.getName());
        return FS.<DataFile>readDataTask(saveDir() + saveFile.getName() + DATA_FILE_EXT);
    }

    /**
     * @return user profile loaded from "profiles/"
     */
    public IOTask<UserProfile> loadProfileTask() {
        log.debug(() -> "Loading profile: " + profileName);
        return FS.<UserProfile>readDataTask(profileDir() + PROFILE_FILE_NAME);
    }

    /**
     * @param saveFile name of the file to delete
     * @return result of the operation
     */
    public IOTask<Void> deleteSaveFile(SaveFile saveFile) {
        log.debug(() -> "Deleting save file: " + saveFile.getName());

        return FS.deleteFileTask(saveDir() + saveFile.getName() + SAVE_FILE_EXT)
                .then(n -> FS.deleteFileTask(saveDir() + saveFile.getName() + DATA_FILE_EXT));
    }

    /**
     * Load all profile names.
     *
     * @return profile names
     */
    @Deprecated
    public static IOResult<List<String> > loadProfileNames() {
        log.debug(() -> "Loading profile names");
        return FS.loadDirectoryNames("./" + PROFILES_DIR, false);
    }

    public static IOTask<Void> deleteProfileTask(String profileName) {
        log.debug(() -> "Deleting profile: " + profileName);
        return FS.deleteDirectoryTask("./" + PROFILES_DIR + profileName);
    }

    /**
     * Loads save files with save file extension from SAVE_DIR.
     *
     * @return save files
     */
    public IOTask<List<SaveFile> > loadSaveFiles() {
        log.debug(() -> "Loading save files");

        return FS.loadFileNamesTask(saveDir(), true, Collections.singletonList(new FileExtension(SAVE_FILE_EXT)))
                .then(fileNames -> new IOTask<List<SaveFile> >() {
                            @Override
                            protected List<SaveFile> onExecute() throws Exception {

                                return fileNames.stream()
                                        .map(name -> FS.<SaveFile>readDataTask(saveDir() + name).execute())
                                        .filter(file -> file != null)
                                        .collect(Collectors.toList());
                            }
                        }
                );
    }

    /**
     * Loads last modified save file from saves directory.
     *
     * @return last modified save file
     */
    public IOTask<SaveFile> loadLastModifiedSaveFile() {
        log.debug(() -> "Loading last modified save file");

        return loadSaveFiles().then(files -> {
            return new IOTask<SaveFile>() {
                @Override
                protected SaveFile onExecute() throws Exception {
                    if (files.isEmpty()) {
                        throw new FileNotFoundException("No save files found");
                    }

                    return files.stream()
                            .sorted(SaveFile.RECENT_FIRST)
                            .findFirst()
                            .get();
                }
            };
        });
    }
}
