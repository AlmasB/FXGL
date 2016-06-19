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
import com.almasb.fxgl.io.DataFile;
import com.almasb.fxgl.io.FS;
import com.almasb.fxgl.io.IOResult;
import com.almasb.fxgl.io.SaveFile;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.settings.UserProfile;

import java.io.FileNotFoundException;
import java.io.Serializable;
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
     * @param data data to save
     * @param fileName to save as
     * @return io result
     */
    @Deprecated
    public IOResult<?> save(Serializable data, String fileName) {
        log.debug(() -> "Saving data: " + fileName);
        return FS.writeData(data, saveDir() + fileName);
    }

    public IOResult<?> save(DataFile dataFile, SaveFile saveFile) {
        log.debug(() -> "Saving data: " + saveFile.getName());

        // TODO: check result of this too
        FS.writeData(saveFile, saveDir() + saveFile.getName() + SAVE_FILE_EXT);

        return FS.writeData(dataFile, saveDir() + saveFile.getName() + DATA_FILE_EXT);
    }

    /**
     * Saves user profile to "profiles/".
     *
     * @param profile the profile to save
     * @return io result
     */
    public IOResult<?> saveProfile(UserProfile profile) {
        log.debug(() -> "Saving profile: " + profileName);
        return FS.writeData(profile, profileDir() + PROFILE_FILE_NAME);
    }

    /**
     * Load serializable data from external
     * file on disk file system from saves directory which is
     * in the directory where the game is run from.
     *
     * @param fileName file name to load from
     * @return instance of deserialized data structure
     */
    @Deprecated
    public IOResult<DataFile> load(String fileName) {
        log.debug(() -> "Loading data: " + fileName);
        return FS.<DataFile>readData(saveDir() + fileName + DATA_FILE_EXT);
    }

    public IOResult<DataFile> load(SaveFile saveFile) {
        log.debug(() -> "Loading data: " + saveFile);
        return FS.<DataFile>readData(saveDir() + saveFile.getName() + DATA_FILE_EXT);
    }

    /**
     * @return user profile loaded from "profiles/"
     */
    public IOResult<UserProfile> loadProfile() {
        log.debug(() -> "Loading profile: " + profileName);
        return FS.<UserProfile>readData(profileDir() + PROFILE_FILE_NAME);
    }

    /**
     * @param fileName name of the file to delete
     * @return result of the operation
     */
    @Deprecated
    public IOResult<?> deleteSaveFile(String fileName) {
        log.debug(() -> "Deleting save file: " + fileName);

        // TODO: check result of this too
        FS.deleteFile(saveDir() + fileName + SAVE_FILE_EXT);

        return FS.deleteFile(saveDir() + fileName + DATA_FILE_EXT);
    }

    public IOResult<?> deleteSaveFile(SaveFile saveFile) {
        log.debug(() -> "Deleting save file: " + saveFile);

        // TODO: check result of this too
        FS.deleteFile(saveDir() + saveFile.getName() + SAVE_FILE_EXT);

        return FS.deleteFile(saveDir() + saveFile.getName() + DATA_FILE_EXT);
    }

    /**
     * Load all profile names.
     *
     * @return profile names
     */
    public static IOResult<List<String> > loadProfileNames() {
        log.debug(() -> "Loading profile names");
        return FS.loadDirectoryNames("./" + PROFILES_DIR, false);
    }

    public static IOResult<?> deleteProfile(String profileName) {
        log.debug(() -> "Deleting profile: " + profileName);
        return FS.deleteDirectory("./" + PROFILES_DIR + profileName);
    }

    /**
     * Loads file names of existing saves from saves directory.
     *
     * @return save file names
     */
    @Deprecated
    public IOResult<List<String> > loadSaveFileNames() {
        log.debug(() -> "Loading save file names");
        return FS.loadFileNames(saveDir(), true);
    }

    /**
     * Loads save files with save file extension from SAVE_DIR.
     *
     * @return save files
     */
    public IOResult<List<SaveFile> > loadSaveFiles() {
        log.debug(() -> "Loading save files");

        IOResult<List<String> > io = FS.loadFileNames(saveDir(), true);
        if (io.hasData()) {

            List<SaveFile> saveFiles = io.getData().stream()
                    .filter(name -> name.endsWith(SAVE_FILE_EXT))
                    .map(name -> FS.<SaveFile>readData(saveDir() + name))
                    .filter(IOResult::hasData)
                    .map(IOResult::getData)
                    .collect(Collectors.toList());

            return IOResult.success(saveFiles);
        } else {
            return IOResult.<List<SaveFile>>failure(io.getError());
        }
    }

    /**
     * Loads last modified save file from saves directory.
     *
     * @return last modified save file
     */
    public IOResult<SaveFile> loadLastModifiedSaveFile() {
        log.debug(() -> "Loading last modified save file");

        IOResult<List<SaveFile> > io = loadSaveFiles();

        if (io.hasData()) {
            if (io.getData().isEmpty()) {
                return IOResult.<SaveFile>failure(new FileNotFoundException("No save files found"));
            }

            return IOResult.success(io.getData()
                    .stream()
                    .sorted(SaveFile.RECENT_FIRST)
                    .findFirst()
                    .get());
        } else {
            return IOResult.<SaveFile>failure(io.getError());
        }
    }
}
