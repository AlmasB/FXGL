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
package com.almasb.fxgl.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.almasb.fxgl.asset.AssetManager;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.util.FXGLLogger;

/**
 * Parser for levels represented by plain text
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public final class TextLevelParser {

    private static final Logger log = FXGLLogger.getLogger("TextLevelParser");

    private Map<Character, EntityProducer> producers = new HashMap<>();

    private char emptyChar = ' ';

    /**
     * Set the empty (ignored) character. If you don't
     * set this, there will a warning generated for
     * each such character.
     *
     * @param c
     * @defaultValue ' '
     */
    public void setEmptyChar(char c) {
        emptyChar = c;
    }

    /**
     * Register a callback that generates an entity when
     * given character was found during parsing
     *
     * @param c
     * @param producer
     */
    public void addEntityProducer(char c, EntityProducer producer) {
        producers.put(c, producer);
    }

    /**
     * Parses a file with given filename into a Level object.
     * The file must be located under "assets/text/". Only
     * the name of the file without the "assets/text/" is required.
     * It will be loaded by assetManager.loadText() method.
     *
     * @param levelFileName
     * @return
     * @throws Exception
     */
    public Level parse(String levelFileName) throws Exception {
        AssetManager assetManager = AssetManager.INSTANCE;
        List<String> lines = assetManager.loadText(levelFileName);

        Level level = new Level();
        level.height = lines.size();

        int maxWidth = 0;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() > maxWidth)
                maxWidth = line.length();

            for (int j = 0; j < line.length(); j++) {
                char c = line.charAt(j);
                EntityProducer producer = producers.get(c);
                if (producer != null) {
                    Entity e = producer.produce(j, i);
                    level.entities.add(e);
                }
                else if (c != emptyChar) {
                    log.warning("No producer found for character: " + c);
                }
            }
        }

        level.width = maxWidth;
        if (level.width == 0 || level.height == 0)
            log.warning("Empty level file");

        return level;
    }

    /**
     * A callback which is used as part of TextLevelParser.addEntityProducer()
     * to convert text characters into entities.
     *
     */
    @FunctionalInterface
    public static interface EntityProducer {

        /**
         * Called when registered character was found during parsing.
         * If your block is 40 units, then entity.setPosition(x*40, y*40);
         *
         * @param x column position of character
         * @param y row position of character
         * @return
         */
        public Entity produce(double x, double y);
    }

    public static final class Level {
        private List<Entity> entities = new ArrayList<>();
        private int width, height;

        /**
         * Prevent initializing from outside
         */
        private Level() {}

        /**
         *
         * @return new list containing entities for this level
         */
        public List<Entity> getEntities() {
            return new ArrayList<>(entities);
        }

        /**
         *
         * @return new array containing entities for this level
         */
        public Entity[] getEntitiesAsArray() {
            return entities.toArray(new Entity[0]);
        }

        /**
         *
         * @return max width of the level in number of characters
         */
        public int getWidth() {
            return width;
        }

        /**
         *
         * @return max height of the level in number of characters
         */
        public int getHeight() {
            return height;
        }
    }
}
