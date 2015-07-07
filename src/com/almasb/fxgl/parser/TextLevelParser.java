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

import com.almasb.fxgl.FXGLLogger;
import com.almasb.fxgl.asset.AssetManager;
import com.almasb.fxgl.entity.Entity;

public final class TextLevelParser {

    private static final Logger log = FXGLLogger.getLogger("TextLevelParser");

    private Map<Character, EntityProducer> producers = new HashMap<>();

    private char emptyChar = ' ';

    public void setEmptyChar(char c) {
        emptyChar = c;
    }

    public void addEntityProducer(char c, EntityProducer producer) {
        producers.put(c, producer);
    }

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

    @FunctionalInterface
    public static interface EntityProducer {
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
