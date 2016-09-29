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
package com.almasb.fxgl.asset;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.parser.KVFile;
import com.almasb.fxgl.scene.CSS;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.FontFactory;
import com.almasb.fxgl.ui.UI;
import com.almasb.fxgl.ui.UIController;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.text.Font;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Handles all resource (asset) loading operations.
 * <p>
 * The "assets" directory must be located in source folder ("src" by default).
 * If you are using the Maven directory structure then under "src/main/resources/".
 * <p>
 * Resources (assets) will be searched for in these specified directories:
 * <ul>
 * <li>Texture - /assets/textures/</li>
 * <li>Sound - /assets/sounds/</li>
 * <li>Music - /assets/music/</li>
 * <li>Text (List&lt;String&gt;) - /assets/text/</li>
 * <li>KVFile - /assets/kv/</li>
 * <li>Data - /assets/data/</li>
 * <li>Scripts - /assets/scripts/</li>
 * <li>Behavior Tree - /assets/ai/</li>
 * <li>FXML - /assets/ui/</li>
 * <li>CSS - /assets/ui/css/</li>
 * <li>Font - /assets/ui/fonts/</li>
 * <li>App icons - /assets/ui/icons/</li>
 * <li>Cursors - /assets/ui/cursors/</li>
 * <li>Resource bundles - /assets/properties/</li>
 * </ul>
 *
 * If you need to access the "raw" JavaFX objects (e.g. Image), you can use
 * {@link AssetLoader#getStream(String)} to obtain an InputStream and then
 * parse into whatever resource you need.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface AssetLoader {

    /**
     * Loads texture with given name from /assets/textures/.
     * Either returns a valid texture or throws an exception in case of errors.
     * <p>
     * Supported image formats are:
     * <ul>
     * <li><a href="http://msdn.microsoft.com/en-us/library/dd183376(v=vs.85).aspx">BMP</a></li>
     * <li><a href="http://www.w3.org/Graphics/GIF/spec-gif89a.txt">GIF</a></li>
     * <li><a href="http://www.ijg.org">JPEG</a></li>
     * <li><a href="http://www.libpng.org/pub/png/spec/">PNG</a></li>
     * </ul>
     * </p>
     *
     * @param name texture name without the /assets/textures/, e.g. "player.png"
     * @return texture
     * @throws IllegalArgumentException if asset not found or loading error
     */
    Texture loadTexture(String name);

    /**
     * Loads texture with given name from /assets/textures/.
     * Then resizes it to given width and height without preserving aspect ratio.
     * Either returns a valid texture or throws an exception in case of errors.
     * <p>
     * Supported image formats are:
     * <ul>
     * <li><a href="http://msdn.microsoft.com/en-us/library/dd183376(v=vs.85).aspx">BMP</a></li>
     * <li><a href="http://www.w3.org/Graphics/GIF/spec-gif89a.txt">GIF</a></li>
     * <li><a href="http://www.ijg.org">JPEG</a></li>
     * <li><a href="http://www.libpng.org/pub/png/spec/">PNG</a></li>
     * </ul>
     * </p>
     *
     * @param name texture name without the /assets/textures/, e.g. "player.png"
     * @param width requested width
     * @param height requested height
     * @return texture
     * @throws IllegalArgumentException if asset not found or loading error
     */
    Texture loadTexture(String name, double width, double height);

    /**
     * Loads sound with given name from /assets/sounds/.
     * Either returns a valid sound or throws an exception in case of errors.
     * <p>
     * Supported sound format:
     * <li>WAV</li>
     *
     * @param name sound name without the /assets/sounds/, e.g. "explosion.wav"
     * @return sound
     * @throws IllegalArgumentException if asset not found or loading error
     */
    Sound loadSound(String name);

    /**
     * Loads sound with given name from /assets/music/.
     * Either returns a valid sound or throws an exception in case of errors.
     * <p>
     * Supported music format:
     * <li>MP3</li>
     *
     * @param name music name without the /assets/music/, e.g. "background_music.mp3"
     * @return music
     * @throws IllegalArgumentException if asset not found or loading error
     */
    Music loadMusic(String name);

    /**
     * Loads text file with given name from /assets/text/
     * into List<String> where each element represents a line
     * in the file. Either returns a valid list with lines read from the file
     * or throws an exception in case of errors.
     *
     * @param name text file name without the /assets/text/, e.g. "level_0.txt"
     * @return list of lines from file
     * @throws IllegalArgumentException if asset not found or loading error
     */
    List<String> loadText(String name);

    /**
     * Loads KVFile with given name from /assets/kv/.
     * Either returns a valid KVFile or throws exception in case of errors.
     *
     * @param name KVFile name without the /assets/kv/, .e.g "settings.kv"
     * @return kv file
     * @throws IllegalArgumentException if asset not found or loading error
     */
    KVFile loadKV(String name);

    /**
     * Loads script with given name from /assets/scripts/ as a single string.
     * Either returns loaded string or throws exception in case of errors.
     *
     * @param name script file without the /assets/scripts/, e.g. "skill_heal.js"
     * @return script as a String
     * @throws IllegalArgumentException if asset not found or loading error
     */
    String loadScript(String name);

    /**
     * Loads resource bundle with given name from "/assets/properties/".
     *
     * @param name must be under "/assets/properties/", e.g. system.properties, game.properties
     * @return resource bundle
     * @throws IllegalArgumentException if asset not found or loading error
     */
    ResourceBundle loadResourceBundle(String name);

    /**
     * Loads cursor image with given name from /assets/ui/cursors/.
     * Either returns a valid image or throws exception in case of errors.
     *
     * @param name image name without the /assets/ui/cursors/, e.g. "attack_cursor.png"
     * @return cursor image
     * @throws IllegalArgumentException if asset not found or loading error
     */
    Image loadCursorImage(String name);

    /**
     * Loads an FXML (.fxml) file from /assets/ui/.
     * Either returns a valid parsed UI or throws an exception in case of errors.
     *
     * @param name FXML file name
     * @param controller the controller object
     * @return a UI object parsed from .fxml
     * @throws IllegalArgumentException if asset not found or loading/parsing error
     */
    UI loadUI(String name, UIController controller);

    /**
     * Loads a CSS file from /assets/ui/css/.
     * Can be applied by calling object.getStyleSheets().add(css.getExternalForm()).
     * Either returns ready CSS or throws exception in case of errors.
     *
     * @param name CSS file name without the /assets/ui/css/, e.g. "ui_button.css"
     * @return css
     * @throws IllegalArgumentException if asset not found or loading error
     */
    CSS loadCSS(String name);

    /**
     * Loads a native JavaFX font with given name from /assets/ui/fonts/
     * wrapped in a FontFactory, which later can be used to produce fonts
     * with different sizes without accessing the font file.
     * Either returns a valid font factory or throws exception in case of errors.
     * <p>
     * Supported font formats are:
     * <ul>
     * <li>TTF</li>
     * <li>OTF</li>
     * </ul>
     * </p>
     *
     * @param name font file name without the /assets/ui/fonts/, e.g. "quest_font.ttf"
     * @return font factory
     * @throws IllegalArgumentException if asset not found or loading error
     */
    FontFactory loadFont(String name);

    /**
     * Loads an app icon from /assets/ui/icons/.
     * Either returns a valid image or throws an exception in case of errors.
     *
     * @param name image name without the /assets/ui/icons/, e.g. "app_icon.png"
     * @return app icon image
     * @throws IllegalArgumentException if asset not found or loading error
     */
    Image loadAppIcon(String name);

    /**
     * Loads a behavior tree from /assets/ai/.
     * Either returns a valid behavior tree or throws an exception in case of errors.
     *
     * @param name tree name without the /assets/ai/, e.g. "patrol.tree"
     * @param <T> tree type
     * @return loaded and parsed behavior tree
     * @throws IllegalArgumentException if asset not found or loading error
     */
    <T> BehaviorTree<T> loadBehaviorTree(String name);

    /**
     * Opens a stream to resource with given name.
     * The caller is responsible for closing the stream.
     * Either returns a valid stream or throws an exception.
     *
     * This is useful for loading resources that do not fall under any asset category.
     * Resource is anything located within the source root / resource root, whether "src/" or "resources/".
     * The resource name must always begin with "/", e.g. "/assets/textures/player.png".
     *
     * @param name resource name
     * @return resource stream
     * @throws IllegalArgumentException if any error occurs or stream is null
     */
    InputStream getStream(String name);

    /**
     * Loads file names from a directory.
     * Note: directory name must be in the format "/assets/...".
     * Returned file names are relativized to the given directory name.
     *
     * @param directory name of directory
     * @return list of file names
     * @throws IllegalArgumentException if directory does not start with "/assets/" or was not found
     */
    List<String> loadFileNames(String directory);

    /**
     * Pre-loads all textures / sounds / music / text / fonts and binary data
     * from their respective folders.
     */
    void cache();

    /**
     * Release all cached assets.
     */
    void clearCache();
}
