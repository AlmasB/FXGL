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
package com.almasb.fxgl;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SaveLoadManager {

    private static final String SAVE_DIR = "saves/";

    /**
     * Save serializable data onto a disk file system under "saves/"
     * which is created if necessary in the directory where the game is run from
     *
     * All extra directories will also be created if necessary
     *
     * @param data
     * @param fileName
     * @throws Exception
     */
    public void save(Serializable data, String fileName) throws Exception {
        Path saveFile = Paths.get("./" + SAVE_DIR + fileName);

        if (!Files.exists(saveFile.getParent())) {
            Files.createDirectories(saveFile.getParent());
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(saveFile))) {
            oos.writeObject(data);
        }
    }

    /**
     * Load serializable data from external
     * file on disk file system from "saves/" directory which is
     * in the directory where the game is run from
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public <T> T load(String fileName) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get("./" + SAVE_DIR + fileName)))) {
            return (T)ois.readObject();
        }
    }

    /**
     *
     * @param fileName
     * @return true if file was deleted, false if file wasn't deleted for any reason
     */
    public boolean delete(String fileName) {
        try {
            return Files.deleteIfExists(Paths.get("./" + SAVE_DIR + fileName));
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Loads file names of existing saves from "saves/".
     *
     * Returns {@link Optional#empty()} if "saves/" directory
     * doesn't exist or an exception occurred
     *
     * @return Optional containing list of file names
     */
    public Optional<List<String> > loadFileNames() {
        Path saveDir = Paths.get("./" + SAVE_DIR);

        if (!Files.exists(saveDir)) {
            return Optional.empty();
        }

        try (Stream<Path> files = Files.walk(saveDir)) {
            return Optional.of(files.filter(Files::isRegularFile)
                        .map(file -> saveDir.relativize(file).toString().replace("\\", "/"))
                        .collect(Collectors.toList()));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Loads last modified save file from "saves/"
     *
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
                }
                catch (Exception e) {
                    return -1;
                }
            }).findFirst().orElseThrow(Exception::new);

            String fileName = saveDir.relativize(file).toString().replace("\\", "/");
            return Optional.of(load(fileName));
        }
        catch (Exception e) {
            return Optional.empty();
        }
    }
}
