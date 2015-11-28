/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.asset;

import com.almasb.fxgl.settings.UserProfile;
import com.almasb.fxgl.util.FXGLLogger;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SaveLoadManager {
    INSTANCE;

    private static final Logger log = FXGLLogger.getLogger("FXGL.SaveLoadManager");

    private static final String SAVE_DIR = "saves/";
    private static final String PROFILE_DIR = "profiles/";

    /**
     * Save serializable data onto a disk file system under "saves/"
     * which is created if necessary in the directory where the game is run from
     * <p>
     * All extra directories will also be created if necessary
     *
     * @param data data to save
     * @param fileName to save as
     * @return io result
     */
    public IOResult save(Serializable data, String fileName) {
        return saveImpl(data, Paths.get("./" + SAVE_DIR + fileName));
    }

    /**
     * Saves user profile to "profiles/".
     *
     * @param profile the profile to save
     * @return io result
     */
    public IOResult saveProfile(UserProfile profile) {
        return saveImpl(profile, Paths.get("./" + PROFILE_DIR + "user.profile"));
    }

    /**
     * Saves data to file, creating required directories.
     *
     * @param data data object to save
     * @param file to save as
     * @return io result
     */
    private IOResult saveImpl(Serializable data, Path file) {
        try {
            if (!Files.exists(file.getParent())) {
                Files.createDirectories(file.getParent());
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
                oos.writeObject(data);
            }

            return IOResult.success();
        } catch (Exception e) {
            log.warning("Save Failed: " + e.getMessage());
            return IOResult.failure(e.getMessage());
        }
    }

    /**
     * Load serializable data from external
     * file on disk file system from "saves/" directory which is
     * in the directory where the game is run from
     *
     * @param fileName file name to load from
     * @return instance of deserialized data structure
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> load(String fileName) {
        return loadImpl(Paths.get("./" + SAVE_DIR + fileName)).map(o -> (T)o);
    }

    /**
     *
     * @return user profile loaded from "profiles/"
     */
    public Optional<UserProfile> loadProfile() {
        boolean profileExists = Files.exists(Paths.get("./" + PROFILE_DIR + "user.profile"));
        if (!profileExists)
            return Optional.empty();

        return loadImpl(Paths.get("./" + PROFILE_DIR + "user.profile"))
                .map(o -> (UserProfile)o);
    }

    /**
     * Loads data from file into an object.
     *
     * @param file file to load from
     * @return the data object
     */
    private Optional<Object> loadImpl(Path file) {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            return Optional.of(ois.readObject());
        } catch (Exception e) {
            log.warning("Load Failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * @param fileName name of the file to delete
     * @return true if file was deleted, false if file wasn't deleted for any reason
     */
    public boolean delete(String fileName) {
        try {
            return Files.deleteIfExists(Paths.get("./" + SAVE_DIR + fileName));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Loads file names of existing saves from "saves/".
     * <p>
     * Returns {@link Optional#empty()} if "saves/" directory
     * doesn't exist or an exception occurred
     *
     * @return Optional containing list of file names
     */
    public Optional<List<String>> loadFileNames() {
        Path saveDir = Paths.get("./" + SAVE_DIR);

        if (!Files.exists(saveDir)) {
            return Optional.empty();
        }

        try (Stream<Path> files = Files.walk(saveDir)) {
            return Optional.of(files.filter(Files::isRegularFile)
                    .map(file -> saveDir.relativize(file).toString().replace("\\", "/"))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Loads last modified save file from "saves/"
     * <p>
     * Returns {@link Optional#empty()} if "saves/" directory
     * doesn't exist, an exception occurred or there are no save files
     *
     * @return last modified save file
     */
    public <T> Optional<T> loadLastModifiedFile() {
        Path saveDir = Paths.get("./" + SAVE_DIR);

        if (!Files.exists(saveDir)) {
            return Optional.empty();
        }

        try (Stream<Path> files = Files.walk(saveDir)) {
            Path file = files.filter(Files::isRegularFile).sorted((file1, file2) -> {
                try {
                    return Files.getLastModifiedTime(file2).compareTo(Files.getLastModifiedTime(file1));
                } catch (Exception e) {
                    return -1;
                }
            }).findFirst().orElseThrow(Exception::new);

            String fileName = saveDir.relativize(file).toString().replace("\\", "/");
            return load(fileName);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
