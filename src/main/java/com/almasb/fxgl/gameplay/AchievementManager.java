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

package com.almasb.fxgl.gameplay;

import com.almasb.fxgl.settings.UserProfile;
import com.almasb.fxgl.settings.UserProfileSavable;
import com.almasb.fxgl.ui.NotificationManager;
import com.almasb.fxgl.util.FXGLLogger;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Logger;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class AchievementManager implements UserProfileSavable {

    private static final Logger log = FXGLLogger.getLogger("FXGL.AchievementManager");

    private NotificationManager notificationManager;

    private ObservableList<Achievement> achievements = FXCollections.observableArrayList();

    private ChangeListener<Boolean> notifier = (observable, oldValue, newValue) -> {
        if (newValue) {
            notificationManager.pushNotification("You got an achievement! ");
        }
    };

    public AchievementManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void registerAchievement(Achievement a) {
        long count = achievements.stream()
                .map(Achievement::getName)
                .filter(n -> n.equals(a.getName()))
                .count();

        if (count > 0)
            throw new IllegalArgumentException("Achievement with name \"" + a.getName()
                + "\" exists");


        a.setOnAchieved(() -> {
            notificationManager.pushNotification("You got an achievement! "
                + a.getName());
        });
        achievements.add(a);
        log.finer("Registered new achievement \"" + a.getName() + "\"");
    }

    public ObservableList<Achievement> getAchievements() {
        return FXCollections.unmodifiableObservableList(achievements);
    }

    @Override
    public void save(UserProfile profile) {
        UserProfile.Bundle bundle = new UserProfile.Bundle("achievement");

        achievements.forEach(a -> bundle.put(a.getName(), a.isAchieved()));

        profile.putBundle(bundle);
    }

    @Override
    public void load(UserProfile profile) {
        UserProfile.Bundle bundle = profile.getBundle("achievement");

        achievements.forEach(a -> {
            boolean achieved = bundle.get(a.getName());
            if (achieved)
                a.setAchieved();
        });
    }
}
