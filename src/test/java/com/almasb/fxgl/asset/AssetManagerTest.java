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

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AssetManagerTest {

    private static final String[] TEXT_ASSETS = {"test1.txt"};
    private static final String[] TEXT_DATA = {"Lorem ipsum dolor sit amet, consectetuer adipiscing elit.\n" +
            "Aenean commodo ligula eget dolor.\n" +
            "Aenean massa.\n" +
            "Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\n" +
            "Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem.\n" +
            "Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu.\n" +
            "In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo.\n" +
            "Nullam dictum felis eu pede mollis pretium.\n" +
            "Integer tincidunt.\n" +
            "Cras dapibus.\n" +
            "Vivamus elementum semper nisi.\n" +
            "Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim.\n" +
            "Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet.\n" +
            "Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui.\n" +
            "Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing\n" +
            "sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante\n" +
            "tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet\n" +
            "rci eget eros faucibus tincidunt"};

    private AssetManager assetManager;

    @Before
    public void init() {
        assetManager = AssetManager.INSTANCE;
    }

    @Test
    public void loadText() {
        for (int i = 0; i < TEXT_ASSETS.length; i++) {
            String textAsset = TEXT_ASSETS[i];
            List<String> actualLines = assetManager.loadText(textAsset);
            List<String> expectedLines = Arrays.asList(TEXT_DATA[i].split("\n"));
            assertEquals(expectedLines, actualLines);
        }
    }
}
