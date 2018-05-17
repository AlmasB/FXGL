/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.saving;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.concurrent.IOTask;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.io.FS;
import com.almasb.fxgl.io.FileExtension;
import com.almasb.fxgl.scene.ProgressDialog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.almasb.fxgl.app.SystemPropertyKey.*;

/**
 * TODO: use async instead of javafx.application.Platform
 *
 * Convenient access to saving and loading game data.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class SaveLoadManager {

    private static final Logger log = Logger.get(SaveLoadManager.class);

    private static final String PROFILE_FILE_NAME = FXGL.getProperties().getString(FS_PROFILENAME);
    private static final String PROFILES_DIR = FXGL.getProperties().getString(FS_PROFILEDIR);
    private static final String SAVE_DIR = FXGL.getProperties().getString(FS_SAVEDIR);

    private static final String SAVE_FILE_EXT = FXGL.getProperties().getString(FS_SAVEFILE_EXT);
    private static final String DATA_FILE_EXT = FXGL.getProperties().getString(FS_DATAFILE_EXT);

    static {
        log.debug("Checking profiles dir: " + PROFILES_DIR);

        if (!FS.exists(PROFILES_DIR)) {
            log.debug("Creating non-existent profiles dir");

            FS.createDirectoryTask(PROFILES_DIR)
                    .then(n -> FS.writeDataTask(Collections.singletonList("This directory contains user profiles."), PROFILES_DIR + "Readme.txt"))
                    .onFailure(e -> {
                        log.warning("Failed to create profiles dir: " + e);
                        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    })
                    .run();
        }
    }

    private final String profileName;

    /**
     * Constructs manager for given profile name.
     *
     * @param profileName profile name
     */
    public SaveLoadManager(String profileName) {
        this.profileName = profileName;
    }

    private ObservableList<SaveFile> saveFiles = FXCollections.observableArrayList();

    /**
     * @return read only view of observable save files
     */
    public ObservableList<SaveFile> saveFiles() {
        return FXCollections.unmodifiableObservableList(saveFiles);
    }

    /**
     * Asynchronously (with a progress dialog) loads save files into observable list {@link #saveFiles()}.
     */
    public void querySaveFiles() {
        log.debug("Querying save files");

        loadSaveFilesTask()
                .onSuccess(files -> {
                    saveFiles.setAll(files);
                    Collections.sort(saveFiles, SaveFile.RECENT_FIRST);
                })
                .runAsyncFXWithDialog(new ProgressDialog(FXGL.getLocalizedString("menu.loadingSaveFiles")));
    }

    /**
     * @return relative path as string to profile dir
     */
    private String profileDir() {
        return "./" + PROFILES_DIR + profileName + "/";
    }

    /**
     * @return relative path as string to save dir
     */
    private String saveDir() {
        return profileDir() + SAVE_DIR;
    }

    /**
     * Save serializable data onto a disk file system under saves directory,
     * which is created if necessary in the directory where the game is start from.
     * <p>
     * All extra directories will also be created if necessary.
     *
     * @param dataFile data file
     * @param saveFile save file
     * @return saving task
     */
    public IOTask<Void> saveTask(DataFile dataFile, SaveFile saveFile) {
        log.debug("Saving data: " + saveFile.getName());

        return FS.writeDataTask(saveFile, saveDir() + saveFile.getName() + SAVE_FILE_EXT)
                .then(n -> FS.writeDataTask(dataFile, saveDir() + saveFile.getName() + DATA_FILE_EXT))
                .then(n -> IOTask.ofVoid("updateSaves", () -> {
                    Platform.runLater(() -> {
                        saveFiles.add(saveFile);
                        Collections.sort(saveFiles, SaveFile.RECENT_FIRST);
                    });
                }));
    }

    /**
     * Saves user profile to "profiles/".
     * Creates "saves/" in that directory.
     *
     * @param profile the profile to save
     * @return saving task
     */
    public IOTask<Void> saveProfileTask(UserProfile profile) {
        log.debug("Saving profile: " + profileName);
        return FS.writeDataTask(profile, profileDir() + PROFILE_FILE_NAME)
                .then(n -> new IOTask<Void>("checkSavesDir(" + saveDir() + ")") {

                    @Override
                    protected Void onExecute() throws Exception {

                        if (!FS.exists(saveDir())) {
                            log.debug("Creating non-existent saves dir");

                            FS.createDirectoryTask(saveDir())
                                    .then(n -> FS.writeDataTask(Collections.singletonList("This directory contains save files."), saveDir() + "Readme.txt"))
                                    .onFailure(e -> {
                                        log.warning("Failed to create saves dir: " + e);
                                        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                                    })
                                    .run();
                        }

                        return null;
                    }
                });
    }

    /**
     * Load serializable data from external
     * file on disk file system from saves directory which is
     * in the directory where the game is start from.
     *
     * @param saveFile save file to load
     * @return saving task
     */
    public IOTask<DataFile> loadTask(SaveFile saveFile) {
        log.debug("Loading data: " + saveFile.getName());
        return FS.<DataFile>readDataTask(saveDir() + saveFile.getName() + DATA_FILE_EXT);
    }

    /**
     * Loads user profile from "profiles/".
     *
     * @return saving task
     */
    public IOTask<UserProfile> loadProfileTask() {
        log.debug("Loading profile: " + profileName);
        return FS.<UserProfile>readDataTask(profileDir() + PROFILE_FILE_NAME);
    }

    /**
     * @param saveFile save file to delete
     * @return saving task
     */
    public IOTask<Void> deleteSaveFileTask(SaveFile saveFile) {
        log.debug("Deleting save file: " + saveFile.getName());

        return FS.deleteFileTask(saveDir() + saveFile.getName() + SAVE_FILE_EXT)
                .then(n -> FS.deleteFileTask(saveDir() + saveFile.getName() + DATA_FILE_EXT))
                .then(n -> IOTask.ofVoid("updateSaves", () -> {
                    Platform.runLater(() -> saveFiles.remove(saveFile));
                }));
    }

    /**
     * @param saveFileName save file name
     * @return true iff file exists
     */
    public boolean saveFileExists(String saveFileName) {
        log.debug("Checking if save file exists: " + saveFileName);

        return FS.exists(saveDir() + saveFileName + SAVE_FILE_EXT);
    }

    /**
     * Load all profile names.
     *
     * @return saving task
     */
    public static IOTask<List<String> > loadProfileNamesTask() {
        log.debug("Loading profile names");
        return FS.loadDirectoryNamesTask("./" + PROFILES_DIR, false);
    }

    /**
     * Delete profile.
     *
     * @param profileName name of profile to delete
     * @return saving task
     */
    public static IOTask<Void> deleteProfileTask(String profileName) {
        log.debug("Deleting profile: " + profileName);
        return FS.deleteDirectoryTask("./" + PROFILES_DIR + profileName);
    }

    /**
     * Loads save files with save file extension from SAVE_DIR.
     *
     * @return saving task
     */
    public IOTask<List<SaveFile> > loadSaveFilesTask() {
        log.debug("Loading save files");

        return FS.loadFileNamesTask(saveDir(), true, Collections.singletonList(new FileExtension(SAVE_FILE_EXT)))
                .then(fileNames -> IOTask.of("readSaveFiles", () -> {

                    List<SaveFile> list = new ArrayList<>();
                    for (String name : fileNames) {
                        SaveFile file = FS.<SaveFile>readDataTask(saveDir() + name).run();
                        if (file != null) {
                            list.add(file);
                        }
                    }
                    return list;
                }));
    }

    /**
     * Loads last modified save file from saves directory.
     *
     * @return saving task
     */
    public IOTask<SaveFile> loadLastModifiedSaveFileTask() {
        log.debug("Loading last modified save file");

        return loadSaveFilesTask().then(files -> IOTask.of("findLastSave", () -> {
            if (files.isEmpty()) {
                throw new FileNotFoundException("No save files found");
            }

            Collections.sort(files, SaveFile.RECENT_FIRST);
            return files.get(0);
        }));
    }
}
