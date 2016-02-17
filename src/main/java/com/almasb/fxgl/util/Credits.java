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

package com.almasb.fxgl.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple data structure to contain a list of credits.
 *
 * TODO: make immutable
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Credits {
    private List<String> credits = new ArrayList<>();

    public Credits() {
        populateCredits();
    }

    public Credits(Credits copy) {
        credits.addAll(copy.credits);
    }

    private void populateCredits() {
        addCredit("Powered by FXGL " + Version.getAsString());
        addCredit("Graphics Framework: JavaFX " + Version.getJavaFXAsString());
        addCredit("Physics Engine: JBox2d (jbox2d.org) " + Version.getJBox2DAsString());
        addCredit("FXGL Author: Almas Baimagambetov (AlmasB)");
        addCredit("https://github.com/AlmasB/FXGL");
    }

    public void addCredit(String credit) {
        credits.add(credit);
    }

    public List<String> getList() {
        return new ArrayList<>(credits);
    }
}
