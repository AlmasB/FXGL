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

package com.almasb.fxgl.event;

import javafx.event.EventType;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class ProfileSelectedEvent extends MenuEvent {

    public static final EventType<ProfileSelectedEvent> ANY
            = new EventType<>(MenuEvent.ANY, "PROFILE_SELECTED");

    private final String profileName;
    private final boolean hasSaves;

    public ProfileSelectedEvent(String profileName, boolean hasSaves) {
        super(ANY);
        this.profileName = profileName;
        this.hasSaves = hasSaves;
    }

    public String getProfileName() {
        return profileName;
    }

    public boolean hasSaves() {
        return hasSaves;
    }

    @Override
    public String toString() {
        return "ProfileSelectedEvent[name=" + profileName + ",hasSaves=" + hasSaves + "]";
    }
}
